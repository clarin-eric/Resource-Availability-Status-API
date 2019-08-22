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

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.filters.impl.ACDHLinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.links.CheckedLink;
import eu.clarin.cmdi.rasa.links.LinkToBeChecked;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ACDHLinkToBeCheckedResourceTest extends TestConfig {

    @BeforeClass
    public static void fillDatabase() throws IOException {

        List<String> urls = Arrays.asList("http://www.ailla.org/waiting.html", "http://www.ailla.org/audio_files/EMP1M1B1.mp3", "http://www.ailla.org/audio_files/WBA1M3A2.mp3", "http://www.ailla.org/text_files/WBA1M1A2a.mp3", "http://www.ailla.org/audio_files/KUA2M1A1.mp3", "http://www.ailla.org/text_files/KUA2M1.pdf", "http://www.ailla.org/audio_files/sarixojani.mp3", "http://www.ailla.org/audio_files/TEH11M7A1sa.mp3", "http://www.ailla.org/text_files/TEH11M7.pdf", "http://dspin.dwds.de:8088/ddc-sru/dta/", "http://dspin.dwds.de:8088/ddc-sru/grenzboten/", "http://dspin.dwds.de:8088/ddc-sru/rem/", "http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml", "http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml");

        for (String url : urls) {
            LinkToBeChecked linkToBeChecked = new LinkToBeChecked(url, "record", "NotGoogle", "mimeType");
            linksToBeChecked.insertOne(linkToBeChecked.getMongoDocument());
        }

        for (String url : googleUrls) {
            LinkToBeChecked linkToBeChecked = new LinkToBeChecked(url, "record", "Google", "mimeType");
            linksToBeChecked.insertOne(linkToBeChecked.getMongoDocument());
        }

    }

    @Test
    public void basicGETTestShouldReturnCorrectResults() {

        LinkToBeCheckedFilter filter = new ACDHLinkToBeCheckedFilter("NotGoogle");
        Stream<LinkToBeChecked> linksToBeChecked = linkToBeCheckedResource.get(Optional.of(filter));

        assertEquals(urls.size(), linksToBeChecked.count());
        //need to get it twice to get the size and loop through it, because streams don't allow to be operated on twice.
        linksToBeChecked = linkToBeCheckedResource.get(Optional.of(filter));
        linksToBeChecked.forEach(linkToBeChecked -> {
            assertEquals("NotGoogle", linkToBeChecked.getCollection());
            assertTrue(urls.contains(linkToBeChecked.getUrl()));
        });

        filter = new ACDHLinkToBeCheckedFilter("Google");
        linksToBeChecked = linkToBeCheckedResource.get(Optional.of(filter));

        assertEquals(googleUrls.size(), linksToBeChecked.count());

        linksToBeChecked = linkToBeCheckedResource.get(Optional.of(filter));
        linksToBeChecked.forEach(linkToBeChecked -> {
            assertEquals("Google", linkToBeChecked.getCollection());
            assertTrue(googleUrls.contains(linkToBeChecked.getUrl()));
        });

    }

    @Test
    public void basicGETListTestShouldReturnCorrectResults() {

        LinkToBeCheckedFilter filter = new ACDHLinkToBeCheckedFilter("NotGoogle");
        List<LinkToBeChecked> linksToBeChecked = linkToBeCheckedResource.getList(Optional.of(filter));

        assertEquals(urls.size(), linksToBeChecked.size());
        linksToBeChecked.forEach(linkToBeChecked -> {
            assertEquals("NotGoogle", linkToBeChecked.getCollection());
            assertTrue(urls.contains(linkToBeChecked.getUrl()));
        });

        filter = new ACDHLinkToBeCheckedFilter("Google");
        linksToBeChecked = linkToBeCheckedResource.getList(Optional.of(filter));

        assertEquals(googleUrls.size(), linksToBeChecked.size());
        linksToBeChecked.forEach(linkToBeChecked -> {
            assertEquals("Google", linkToBeChecked.getCollection());
            assertTrue(googleUrls.contains(linkToBeChecked.getUrl()));
        });

    }

    @Test
    public void saveTestShouldSaveCorrectly() {
        String url = urls.get(0);

        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(url, "record", "NotGoogle", "mimeType");

        //delete if it exists
        linksToBeChecked.deleteOne(linkToBeChecked.getMongoDocument());

        Bson filter = Filters.eq("url", url);
        MongoCursor<Document> cursor;
        cursor = linksToBeChecked.find(filter).iterator();

        //shouldn't exist after deleting
        assertFalse(cursor.hasNext());

        //save
        linkToBeCheckedResource.save(linkToBeChecked);

        //should exist after saving
        cursor = linksToBeChecked.find(filter).iterator();
        while (cursor.hasNext()) {
            LinkToBeChecked result = new LinkToBeChecked(cursor.next());
            assertEquals(linkToBeChecked, result);
        }
    }

}
