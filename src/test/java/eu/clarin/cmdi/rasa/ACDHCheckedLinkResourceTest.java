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

import org.bson.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ACDHCheckedLinkResourceTest extends TestConfig {

    @BeforeClass
    public static void fillDatabase() throws IOException {

        //this list is a combination of urls and googleUrls lists.
        List<Document> links = Arrays.asList(
                new CheckedLink("http://www.ailla.org/waiting.html", "HEAD", "Ok", 200, "text/html; charset=UTF-8", "Not Specified", 132, 1566479337981L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.ailla.org/audio_files/EMP1M1B1.mp3", "GET", "Broken", 400, "text/html; charset=UTF-8", "Not Specified", 46, 1566479338165L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.ailla.org/audio_files/WBA1M3A2.mp3", "GET", "Broken", 400, "text/html; charset=UTF-8", "Not Specified", 46, 1566479338216L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.ailla.org/text_files/WBA1M1A2a.mp3", "GET", "Broken", 400, "text/html; charset=UTF-8", "Not Specified", 46, 1566479338264L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.ailla.org/audio_files/KUA2M1A1.mp3", "GET", "Broken", 400, "text/html; charset=UTF-8", "Not Specified", 56, 1566479338312L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.ailla.org/text_files/KUA2M1.pdf", "HEAD", "Ok", 200, "text/html; charset=UTF-8", "Not Specified", 51, 1566479338340L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.ailla.org/audio_files/sarixojani.mp3", "GET", "Broken", 400, "text/html; charset=UTF-8", "Not Specified", 48, 1566479338425L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.ailla.org/audio_files/TEH11M7A1sa.mp3", "GET", "Broken", 400, "text/html; charset=UTF-8", "Not Specified", 48, 1566479338477L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.ailla.org/text_files/TEH11M7.pdf", "HEAD", "Ok", 200, "text/html; charset=UTF-8", "Not Specified", 57, 1566479338497L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://dspin.dwds.de:8088/ddc-sru/dta/", "HEAD", "Ok", 200, "application/xml;charset=utf-8", "2094", 67, 1566479338557L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://dspin.dwds.de:8088/ddc-sru/grenzboten/", "HEAD", "Ok", 200, "application/xml;charset=utf-8", "2273", 57, 1566479338629L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://dspin.dwds.de:8088/ddc-sru/rem/", "HEAD", "Ok", 200, "application/xml;charset=utf-8", "2497", 58, 1566479338688L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.deutschestextarchiv.de/rem/?d=M084E-N1.xml", "HEAD", "Ok", 200, "text/html; charset=utf-8", "Not Specified", 591, 1566479338750L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.deutschestextarchiv.de/rem/?d=M220P-N1.xml", "HEAD", "Ok", 200, "text/html; charset=utf-8", "Not Specified", 592, 1566479339346L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.deutschestextarchiv.de/rem/?d=M119-N1.xml", "HEAD", "Ok", 200, "text/html; charset=utf-8", "Not Specified", 602, 1566479339943L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.deutschestextarchiv.de/rem/?d=M171-G1.xml", "HEAD", "Ok", 200, "text/html; charset=utf-8", "Not Specified", 613, 1566479340552L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.deutschestextarchiv.de/rem/?d=M185-N1.xml", "HEAD", "Ok", 200, "text/html; charset=utf-8", "Not Specified", 605, 1566479341168L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.deutschestextarchiv.de/rem/?d=M048P-N1.xml", "HEAD", "Ok", 200, "text/html; charset=utf-8", "Not Specified", 599, 1566479341777L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("http://www.deutschestextarchiv.de/rem/?d=M112-G1.xml", "HEAD", "Ok", 200, "text/html; charset=utf-8", "Not Specified", 591, 1566479342381L, null, 0, null, null).getMongoDocument(),
                new CheckedLink("https://www.google.com", "HEAD", "Ok", 200, "text/html; charset=ISO-8859-1", "Not Specified", 222, 1566479342976L, "Google", 0, null, null).getMongoDocument(),
                new CheckedLink("https://maps.google.com", "HEAD", "Ok", 200, "text/html; charset=UTF-8", "Not Specified", 440, 1566479343454L, "Google", 2, null, null).getMongoDocument(),
                new CheckedLink("https://drive.google.com", "HEAD", "Ok", 200, "text/html; charset=UTF-8", "73232", 413, 1566479343867L, "Google", 1, null, null).getMongoDocument());

        linksChecked.insertMany(links);

    }


    @Test
    public void basicGETTestShouldReturnCorrectResults() {

        CheckedLink expected = new CheckedLink("http://www.ailla.org/waiting.html", "HEAD", "Ok", 200, "text/html; charset=UTF-8", "Not Specified", 132, 1566479337981L, null, 0, null, null);
        CheckedLink actual = checkedLinkResource.get("http://www.ailla.org/waiting.html");
        assertEquals(expected, actual);

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
    public void collectionFilterShouldReturnCorrectResults() {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Google");
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(3, count);

        filter = new ACDHCheckedLinkFilter("noCollection");
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(0, count);

    }

    @Test
    public void dateFiltersShouldReturnCorrectResults() {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1), ZoneId.systemDefault());
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
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
    }

    @Test
    public void statusCodeFilterShouldReturnCorrectResults() {

        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Google", 200);
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(3, count);

        filter = new ACDHCheckedLinkFilter(Range.between(100, 600), null, null, ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(Range.between(1, 90), null, null, ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(0, count);

    }

    @Test
    public void combinedFilterShouldReturnCorrectResults() {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(Range.between(100, 300), LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1), ZoneId.systemDefault(), "Google");
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(3, count);
    }

}
