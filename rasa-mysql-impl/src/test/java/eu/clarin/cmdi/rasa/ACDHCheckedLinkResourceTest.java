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
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import org.apache.commons.lang3.Range;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHCheckedLinkResourceTest extends TestConfig {

    private String testURL = "https://mail.google.com";

    //2019-10-11 00:00:00, same as initDB
    private static final LocalDateTime thenDateTime = LocalDateTime.of(2019, 10, 11, 0, 0, 0);
    private static final LocalDateTime thenDateTime1 = LocalDateTime.of(2019, 10, 12, 0, 0, 0);
    private static final LocalDateTime thenDateTime2 = LocalDateTime.of(2019, 10, 13, 0, 0, 0);
    private static final Timestamp then = Timestamp.valueOf(thenDateTime);
    private static final Timestamp then1 = Timestamp.valueOf(thenDateTime1);
    private static final Timestamp then2 = Timestamp.valueOf(thenDateTime2);

    @Test
    public void basicGETTestShouldReturnCorrectResults() throws SQLException {

        CheckedLink expected = new CheckedLink("http://www.ailla.org/waiting.html", "HEAD", 200, "text/html; charset=UTF-8", 100, 132, then, "Ok", "NotGoogle", 0, "record", null, "Ok");
        Optional<CheckedLink> actual = checkedLinkResource.get("http://www.ailla.org/waiting.html");
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());

        for (String url : otherUrls) {
            Optional<CheckedLink> checkedLink = checkedLinkResource.get(url);
            assertTrue(actual.isPresent());
            assertEquals(checkedLink.get().getUrl(), url);
        }
    }

    @Test
    public void basicGETWithCollectionTestShouldReturnCorrectResults() throws SQLException {

        for (String url : googleUrls) {
            Optional<CheckedLink> checkedLink = checkedLinkResource.get(url, "Google");
            assertTrue(checkedLink.isPresent());
            assertEquals(checkedLink.get().getUrl(), url);
            assertEquals(checkedLink.get().getCollection(), "Google");
        }
    }

    @Test
    public void collectionFilterShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Google");
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(3, count);
        }

        filter = new ACDHCheckedLinkFilter("noCollection");
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(0, count);
        }

    }

    @Test
    public void categoryCollectionFilterShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Overall",Category.Ok);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(16, count);
        }

        filter = new ACDHCheckedLinkFilter("Overall",Category.Broken);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(6, count);
        }

        filter = new ACDHCheckedLinkFilter("Google",Category.Ok);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(3, count);
        }

        filter = new ACDHCheckedLinkFilter("Google",Category.Broken);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(0, count);
        }

    }

    @Test
    public void categoryRecordFilterShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Overall", "record",Category.Ok);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(13, count);
        }

        filter = new ACDHCheckedLinkFilter("Overall","record",Category.Broken);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(6, count);
        }

        filter = new ACDHCheckedLinkFilter("Overall","GoogleRecord",Category.Broken);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(0, count);
        }

        filter = new ACDHCheckedLinkFilter("Google","GoogleRecord",Category.Ok);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(3, count);
        }

        filter = new ACDHCheckedLinkFilter("Google","GoogleRecord",Category.Broken);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(0, count);
        }

    }

    @Test
    public void dateFiltersShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(null, thenDateTime.plusDays(1), thenDateTime.minusDays(1), ZoneId.systemDefault());
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(22, count);
        }

        filter = new ACDHCheckedLinkFilter(null, thenDateTime.plusDays(1), null, ZoneId.systemDefault());
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(22, count);
        }


        filter = new ACDHCheckedLinkFilter(null, null, thenDateTime.minusDays(1), ZoneId.systemDefault());
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(22, count);
        }


        filter = new ACDHCheckedLinkFilter(null, thenDateTime.minusDays(1), thenDateTime.plusDays(1), ZoneId.systemDefault());
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(0, count);
        }

    }

    @Test
    public void statusCodeFilterShouldReturnCorrectResults() throws SQLException {

        CheckedLinkFilter filter = new ACDHCheckedLinkFilter("Google", 200);
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(3, count);
        }

        filter = new ACDHCheckedLinkFilter(Range.between(100, 600), null, null, ZoneId.systemDefault());
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(22, count);
        }


        filter = new ACDHCheckedLinkFilter(Range.between(1, 90), null, null, ZoneId.systemDefault());
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(0, count);
        }




    }

    @Test
    public void combinedFilterShouldReturnCorrectResults() throws SQLException {
        CheckedLinkFilter filter = new ACDHCheckedLinkFilter(Range.between(100, 300), thenDateTime.plusDays(1), thenDateTime.minusDays(1), ZoneId.systemDefault(), "Google");
        try (Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
            long count = links.count();
            assertEquals(3, count);
        }
    }

    @Test
    public void getWithListTestShouldReturnCorrectResults() throws SQLException {
        List<String> someURLs = Arrays.asList("http://www.ailla.org/waiting.html", "http://www.ailla.org/audio_files/EMP1M1B1.mp3");

        Map<String, CheckedLink> links = checkedLinkResource.get(someURLs, Optional.empty());
        assertEquals(2, links.size());

        assertEquals(someURLs.get(0), links.get(someURLs.get(0)).getUrl());
        assertEquals(someURLs.get(1), links.get(someURLs.get(1)).getUrl());

        List<String> googleURLs = Arrays.asList("https://www.google.com", "https://maps.google.com");
        links = checkedLinkResource.get(googleURLs, Optional.of(new ACDHCheckedLinkFilter("Google")));
        assertEquals(2, links.size());

        assertEquals(googleURLs.get(0), links.get(googleURLs.get(0)).getUrl());
        assertEquals(googleURLs.get(1), links.get(googleURLs.get(1)).getUrl());
        //shouldnt be in there
        assertNull(links.get("https://drive.google.com"));

        links = checkedLinkResource.get(Collections.emptyList(), Optional.empty());
        assertEquals(0, links.size());
    }

    @Test
    public void saveWithoutTupleInUrlsTableTestShouldNotSave() throws SQLException {
        assertFalse(checkedLinkResource.save(new CheckedLink("not in urls table url", null, 0, null, 0, 0, null, null, null, 0, null, null, "Broken")));
    }

    @Test
    public void filterWithStartAndEndTestShouldReturnCorrectResults() throws SQLException {
        try (Stream<CheckedLink> linksStream = checkedLinkResource.get(Optional.empty(), 1, 10)) {
            List<CheckedLink> links = linksStream.collect(Collectors.toList());
            assertEquals(10, links.size());


            try (Stream<CheckedLink> linksOneOffsetStream = checkedLinkResource.get(Optional.empty(), 2, 10)) {
                List<CheckedLink> linksOneOffset = linksOneOffsetStream.collect(Collectors.toList());
                assertEquals(9, linksOneOffset.size());


                links.remove(0);
                assertEquals(links, linksOneOffset);
            }
        }

    }

    //the next two methods should be run in order
    @Test
    public void ZZ1saveTestShouldSaveCorrectly() throws SQLException {
        //before saving only 3 google urls
        try (Stream<CheckedLink> googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")))) {
            assertEquals(3, googleStream.count());
        }

        //save(first urls then status)
        linkToBeCheckedResource.save(new LinkToBeChecked(testURL, "GoogleRecord", "Google", "mimeType", System.currentTimeMillis()));
        CheckedLink checkedLink = new CheckedLink(testURL, "HEAD", 200, null, 100, 100, then, "Ok", "Google", 0, "GoogleRecord", "mimeType","Ok");
        checkedLinkResource.save(checkedLink);

        //after saving should be 4
        try (Stream<CheckedLink> googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")))) {
            assertEquals(4, googleStream.count());
        }


        //and should contain
        try (Stream<CheckedLink> googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")))) {
            assertTrue(googleStream.anyMatch(x -> Objects.equals(x, checkedLink)));
        }

    }

    @Test
    public void ZZ2getHistoryTestShouldReturnCorrectResults() throws SQLException {
        //add again but with time then2
        CheckedLink checkedLink = new CheckedLink(testURL, "HEAD", 200, null, 100, 100, then, "Ok", "Google", 0, "GoogleRecord", "mimeType", "Ok");
        CheckedLink checkedLink1 = new CheckedLink(testURL, "HEAD", 200, null, 100, 100, then1, "Ok", "Google", 0, "GoogleRecord", "mimeType","Ok");
        CheckedLink checkedLink2 = new CheckedLink(testURL, "HEAD", 200, null, 100, 100, then2, "Ok", "Google", 0, "GoogleRecord", "mimeType","Ok");

        checkedLinkResource.save(checkedLink1);
        assertEquals(checkedLink1, checkedLinkResource.get(testURL).get());

        List<CheckedLink> history = checkedLinkResource.getHistory(testURL, CheckedLinkResource.Order.DESC);
        assertEquals(1, history.size());
        assertEquals(checkedLink, history.get(0));

        //add again, history should have 2
        checkedLinkResource.save(checkedLink2);
        assertEquals(checkedLink2, checkedLinkResource.get(testURL).get());

        history = checkedLinkResource.getHistory(testURL, CheckedLinkResource.Order.DESC);
        assertEquals(2, history.size());
        assertEquals(checkedLink1, history.get(0));
        assertEquals(checkedLink, history.get(1));
    }

    @Test
    public void ZZ3deleteTestShouldDeleteCorrectly() throws SQLException {
        //first status then url
        checkedLinkResource.delete(testURL);
        linkToBeCheckedResource.delete(testURL);

        //after deleting only 3 google urls
        try (Stream<CheckedLink> googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")))) {
            assertEquals(3, googleStream.count());
        }

        //and shouldn't contain
        try (Stream<CheckedLink> googleStream = checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")))) {
            assertFalse(googleStream.anyMatch(x -> x.getUrl().equals(testURL)));
        }

    }

}
