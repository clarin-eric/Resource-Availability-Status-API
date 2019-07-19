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

import eu.clarin.cmdi.linkchecker.httpLinkChecker.HTTPLinkChecker;
import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.filters.impl.ACDHCheckedLinkFilter;
import eu.clarin.cmdi.rasa.links.CheckedLink;
import org.apache.commons.lang3.Range;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ACDHCheckedLinkResourceTest extends TestConfig {

    private static List<String> urls = Arrays.asList("http://www.ailla.org/waiting.html", "http://www.ailla.org/audio_files/EMP1M1B1.mp3", "http://www.ailla.org/audio_files/WBA1M3A2.mp3", "http://www.ailla.org/text_files/WBA1M1A2a.mp3", "http://www.ailla.org/audio_files/KUA2M1A1.mp3", "http://www.ailla.org/text_files/KUA2M1.pdf", "http://www.ailla.org/audio_files/sarixojani.mp3", "http://www.ailla.org/audio_files/TEH11M7A1sa.mp3", "http://www.ailla.org/text_files/TEH11M7.pdf", "http://dspin.dwds.de:8088/ddc-sru/dta/", "http://dspin.dwds.de:8088/ddc-sru/grenzboten/", "http://dspin.dwds.de:8088/ddc-sru/rem/", "http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml", "http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml");
    private static List<String> googleUrls = Arrays.asList("https://www.google.com", "https://maps.google.com", "https://drive.google.com");


    @BeforeClass
    public static void fillDatabase() throws IOException {

        List<String> urls = Arrays.asList("http://www.ailla.org/waiting.html", "http://www.ailla.org/audio_files/EMP1M1B1.mp3", "http://www.ailla.org/audio_files/WBA1M3A2.mp3", "http://www.ailla.org/text_files/WBA1M1A2a.mp3", "http://www.ailla.org/audio_files/KUA2M1A1.mp3", "http://www.ailla.org/text_files/KUA2M1.pdf", "http://www.ailla.org/audio_files/sarixojani.mp3", "http://www.ailla.org/audio_files/TEH11M7A1sa.mp3", "http://www.ailla.org/text_files/TEH11M7.pdf", "http://dspin.dwds.de:8088/ddc-sru/dta/", "http://dspin.dwds.de:8088/ddc-sru/grenzboten/", "http://dspin.dwds.de:8088/ddc-sru/rem/", "http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml", "http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml", "http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml");

        HTTPLinkChecker httpLinkChecker = new HTTPLinkChecker(5000, 5, "RASA Test - ACDH");

        for (String url : urls) {
            CheckedLink link = httpLinkChecker.checkLink(url, 0, 0, url);

            linksChecked.insertOne(link.getMongoDocument());

        }

        for (String url : googleUrls) {
            CheckedLink link = httpLinkChecker.checkLink(url, 0, 0, url);

            link.setCollection("Google");

            linksChecked.insertOne(link.getMongoDocument());

        }

    }


    @Test
    public void basicGETTestShouldReturnCorrectResults() {

        for (String url : urls) {
            CheckedLink checkedLink = checkedLinkResource.get(url);
            assertEquals(checkedLink.getUrl(), url);
        }
    }

    @Test
    public void basicGETWithCollectionTestShouldReturnCorrectResults() {

        for (String url : googleUrls) {
            CheckedLink checkedLink = checkedLinkResource.get(url, "Google");
            assertEquals(checkedLink.getUrl(), url);
            assertEquals(checkedLink.getCollection(), "Google");
        }
    }

    @Test
    public void differentFiltersShouldReturnCorrectResults() {

        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Google");
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(3, count);

        filter = new ACDHCheckedLinkFilter("noCollection");
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(0, count);

        //provided google is up and returning 200 for the google urls (which should be always, or the society will collapse)
        filter = new ACDHCheckedLinkFilter("Google", 200);
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(3, count);

        filter = new ACDHCheckedLinkFilter(Range.between(100, 600), null, null, ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(Range.between(1, 90), null, null, ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(0, count);

        filter = new ACDHCheckedLinkFilter(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1), ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(null, LocalDateTime.now().plusDays(1), null, ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(null, null, LocalDateTime.now().minusDays(1), ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(0, count);

        filter = new ACDHCheckedLinkFilter(Range.between(100, 300), LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1), ZoneId.systemDefault(), "Google");
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(3, count);

//        filter = new ACDHCheckedLinkFilter();
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(, count);
//
//        filter = new ACDHCheckedLinkFilter();
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(, count);
//
//        filter = new ACDHCheckedLinkFilter();
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(, count);
//
//        filter = new ACDHCheckedLinkFilter();
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(, count);
//
//        filter = new ACDHCheckedLinkFilter();
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(, count);
//
//        filter = new ACDHCheckedLinkFilter();
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(, count);
//
//        filter = new ACDHCheckedLinkFilter();
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(, count);
//
//        filter = new ACDHCheckedLinkFilter();
//        links = checkedLinkResource.get(Optional.of(filter));
//        count = links.count();
//        assertEquals(, count);


    }
}
