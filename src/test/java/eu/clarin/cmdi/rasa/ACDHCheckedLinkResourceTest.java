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

import eu.clarin.cmdi.rasa.DAO.CheckedLink;
import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;
import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.filters.impl.ACDHCheckedLinkFilter;
import eu.clarin.cmdi.rasa.DAO.CheckedLink;
import eu.clarin.cmdi.rasa.filters.impl.ACDHCheckedLinkFilter;
import org.apache.commons.lang3.Range;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHCheckedLinkResourceTest extends TestConfig {

    private String testURL = "https://mail.google.com";

    //2019-10-11 00:00:00, same as initDB
    private static final LocalDateTime thenDateTime = LocalDateTime.of(2019, 10, 11, 0, 0, 0);
    private static final Timestamp then = Timestamp.valueOf(thenDateTime);

    @Test
    public void basicGETTestShouldReturnCorrectResults() throws SQLException {

        CheckedLink expected = new CheckedLink("http://www.ailla.org/waiting.html", "HEAD", 200, "text/html; charset=UTF-8", 100, 132, then, "NotGoogle", 0, "record", null);
        CheckedLink actual = checkedLinkResource.get("http://www.ailla.org/waiting.html");
        assertEquals(expected, actual);

        for (String url : urls) {
            CheckedLink checkedLink = checkedLinkResource.get(url);
            assertEquals(checkedLink.getUrl(), url);
        }
    }

    @Test
    public void basicGETWithCollectionTestShouldReturnCorrectResults() throws SQLException {

        for (String url : googleUrls) {
            CheckedLink checkedLink = checkedLinkResource.get(url, "Google");
            assertEquals(checkedLink.getUrl(), url);
            assertEquals(checkedLink.getCollection(), "Google");
        }
    }

    @Test
    public void collectionFilterShouldReturnCorrectResults() throws SQLException {
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
    public void dateFiltersShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(null, thenDateTime.plusDays(1), thenDateTime.minusDays(1), ZoneId.systemDefault());
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(null, thenDateTime.plusDays(1), null, ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(null, null, thenDateTime.minusDays(1), ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(22, count);

        filter = new ACDHCheckedLinkFilter(null, thenDateTime.minusDays(1), thenDateTime.plusDays(1), ZoneId.systemDefault());
        links = checkedLinkResource.get(Optional.of(filter));
        count = links.count();
        assertEquals(0, count);
    }

    @Test
    public void statusCodeFilterShouldReturnCorrectResults() throws SQLException {

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
    public void combinedFilterShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(Range.between(100, 300), thenDateTime.plusDays(1), thenDateTime.minusDays(1), ZoneId.systemDefault(), "Google");
        Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter));
        long count = links.count();
        assertEquals(3, count);
    }

    @Test
    public void saveWithoutTupleInUrlsTableTestShouldNotSave() throws SQLException {
        assertFalse(checkedLinkResource.save(new CheckedLink("not in urls table url", null, 0, null, 0, 0, null, null, 0, null, null)));
    }

    @Test
    public void filterWithStartAndEndTestShouldReturnCorrectResults() throws SQLException {
        Stream<CheckedLink> linksStream = checkedLinkResource.get(Optional.empty(), 1, 10);
        List<CheckedLink> links = linksStream.collect(Collectors.toList());
        assertEquals(10, links.size());

        Stream<CheckedLink> linksOneOffsetStream = checkedLinkResource.get(Optional.empty(), 2, 10);
        List<CheckedLink> linksOneOffset = linksOneOffsetStream.collect(Collectors.toList());
        assertEquals(9, linksOneOffset.size());

        links.remove(0);
        assertEquals(links, linksOneOffset);
    }

    //the next two methods should be run in order
    @Test
    public void ZZ1saveTestShouldSaveCorrectly() throws SQLException {
        //before saving only 3 google urls
        Stream<CheckedLink> googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")));
        assertEquals(3, googleStream.count());

        //save(first urls then status)
        linkToBeCheckedResource.save(new LinkToBeChecked(testURL,"GoogleRecord","Google","mimeType"));
        CheckedLink checkedLink = new CheckedLink(testURL, "HEAD", 200, null, 100, 100, then, "Google", 0, "GoogleRecord", "mimeType");
        checkedLinkResource.save(checkedLink);

        //after saving should be 4
        googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")));
        assertEquals(4, googleStream.count());

        //and should contain
        googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")));
        assertTrue(googleStream.anyMatch(x -> Objects.equals(x, checkedLink)));
    }

    @Test
    public void ZZ2deleteTestShouldSaveCorrectly() throws SQLException {
        //first status then url
        checkedLinkResource.delete(testURL);
        linkToBeCheckedResource.delete(testURL);

        //after deleting only 3 google urls
        Stream<CheckedLink> googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")));
        assertEquals(3, googleStream.count());

        //and shouldn't contain
        googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")));
        assertFalse(googleStream.anyMatch(x -> x.getUrl().equals(testURL)));
    }

}