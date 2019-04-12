/*
 * Copyright (C) 2019 CLARIN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package eu.clarin.cmdi.rasa;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import eu.clarin.cmdi.rasa.helpers.RasaFactory;
import eu.clarin.cmdi.rasa.helpers.impl.ACDHCheckedLinkFilter;
import eu.clarin.cmdi.rasa.helpers.impl.ACDHRasaFactory;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.linkResources.impl.ACDHCheckedLinkResource;
import eu.clarin.cmdi.rasa.links.CheckedLink;
import org.apache.commons.lang3.Range;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

//These tests are currently according to my own local database instance. They won't work anywhere else
//todo create test database
public class RASATest {

    private static MongoClient mongoClient;
    private static RasaFactory rasaFactory;
    private static ACDHCheckedLinkResource checkedLinkResource;

    @BeforeClass
    public static void setUp() throws Exception {

        mongoClient = MongoClients.create();
        rasaFactory = new ACDHRasaFactory(mongoClient, "links");
        checkedLinkResource = rasaFactory.getCheckedLinkResource();

    }

    @Test
    public void basicURLTest() throws URISyntaxException {
        String url = "http://www.aclweb.org/anthology/P84-1023";

        CheckedLink checkedLink = checkedLinkResource.get(new URI(url));
        assertEquals(checkedLink.getUrl(), url);
        assertEquals(checkedLink.getMethod(), "HEAD");
        assertEquals(checkedLink.getTimestamp(), 1542213632877L);

    }


    @Test
    public void complexURLTest() throws URISyntaxException {
        String url = "http://www.aclweb.org/anthology/P84-1023"; //status 200
        String url1 = "http://diglib.hab.de/drucke/drucke/374-5-quod-9s/start.htm"; //status 400

        ArrayList<URI> uriCollection = new ArrayList<>();
        uriCollection.add(new URI(url));
        uriCollection.add(new URI(url1));


        Map<URI, CheckedLink> checkedLinkMap = checkedLinkResource.get(uriCollection, Optional.empty());

        CheckedLink checkedLink = checkedLinkMap.get(new URI(url));
        assertEquals(checkedLink.getUrl(), url);
        assertEquals(checkedLink.getMethod(), "HEAD");
        assertEquals(checkedLink.getTimestamp(), 1542213632877L);


        checkedLink = checkedLinkMap.get(new URI(url1));
        assertEquals(checkedLink.getUrl(), url1);
        assertEquals(checkedLink.getMethod(), "GET");
        assertEquals(checkedLink.getTimestamp(), 1542490978125L);


        Range range = Range.between(300, 400);
        checkedLinkMap = checkedLinkResource.get(uriCollection, Optional.of(new ACDHCheckedLinkFilter(range, null, null)));
        assertEquals(checkedLinkMap.size(), 1);

        checkedLink = checkedLinkMap.get(new URI(url1));
        assertEquals(checkedLink.getUrl(), url1);
        assertEquals(checkedLink.getMethod(), "GET");
        assertEquals(checkedLink.getTimestamp(), 1542490978125L);

        assertNull(checkedLinkMap.get(new URI(url)));


    }

    @Test
    public void complexURLHistoryTest() throws URISyntaxException {
        String url = "www.google.com";

        Stream<CheckedLink> checkedLinkStream = checkedLinkResource.getHistory(new URI(url), CheckedLinkResource.Order.ASC, Optional.empty());

        long count = checkedLinkStream.peek(checkedLink ->
                assertEquals(checkedLink.getUrl(), url)
        ).count();

        assertEquals(count, 9);

    }


}
