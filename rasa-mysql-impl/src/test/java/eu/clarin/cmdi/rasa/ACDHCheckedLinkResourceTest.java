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
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHCheckedLinkResourceTest extends TestConfig {

    private String testURL = "https://maps.google.com";



    @Test
    public void basicGETTestShouldReturnCorrectResults() throws SQLException {

        CheckedLink expected = new CheckedLink("http://www.ailla.org/waiting.html", "HEAD", 200, "text/html; charset=UTF-8", 100, 132, today, "Ok", "NotGoogle", 0, "record", null, Category.Ok);
        
        try(Stream<CheckedLink> stream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setUrlIs("http://www.ailla.org/waiting.html"))){
	        Optional<CheckedLink> actual = stream.findFirst();
	        assertTrue(actual.isPresent());
	        assertEquals(expected, actual.get());
        }

        for (String url : otherUrls) {
        	try(Stream<CheckedLink> stream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setUrlIs(url))){
        		Optional<CheckedLink> actual = stream.findFirst();
        		assertTrue(actual.isPresent());
            	assertEquals(actual.get().getUrl(), url);
        	}
        }
    }

    @Test
    public void basicGETWithCollectionTestShouldReturnCorrectResults() throws SQLException {

        for (String url : googleUrls) {
        	try(Stream<CheckedLink> stream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setUrlIs(url).setProviderGroupIs("Google"))){
	            Optional<CheckedLink> checkedLink = stream.findFirst();
	            assertTrue(checkedLink.isPresent());
	            assertEquals(checkedLink.get().getUrl(), url);
        	}
        }
    }

    @Test
    public void collectionFilterShouldReturnCorrectResults() throws SQLException {
        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google"))) {
            long count = links.count();
            assertEquals(3, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("noCollection"))) {
            long count = links.count();
            assertEquals(0, count);
        }

    }

    @Test
    public void categoryCollectionFilterShouldReturnCorrectResults() throws SQLException {
        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Overall").setCategoryIs(Category.Ok))) {
            long count = links.count();
            assertEquals(16, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Overall").setCategoryIs(Category.Broken))) {
            long count = links.count();
            assertEquals(6, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google").setCategoryIs(Category.Ok))) {
            long count = links.count();
            assertEquals(3, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google").setCategoryIs(Category.Broken))) {
            long count = links.count();
            assertEquals(0, count);
        }

    }

    @Test
    public void categoryRecordFilterShouldReturnCorrectResults() throws SQLException {
        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Overall").setRecordIs("record").setCategoryIs(Category.Ok))) {
            long count = links.count();
            assertEquals(13, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Overall").setRecordIs("record").setCategoryIs(Category.Broken))) {
            long count = links.count();
            assertEquals(6, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Overall").setRecordIs("GoogleRecord").setCategoryIs(Category.Broken))) {
            long count = links.count();
            assertEquals(0, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google").setRecordIs("GoogleRecord").setCategoryIs(Category.Ok))) {
            long count = links.count();
            assertEquals(3, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google").setRecordIs("GoogleRecord").setCategoryIs(Category.Broken))) {
            long count = links.count();
            assertEquals(0, count);
        }

    }

    @Test
    public void dateFiltersShouldReturnCorrectResults() throws SQLException {
        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setCheckedBetween(yesterday, tomorrow))) {
            long count = links.count();
            assertEquals(22, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setCheckedBetween(tomorrow, yesterday))) {
            long count = links.count();
            assertEquals(0, count);
        }

    }

    @Test
    public void statusCodeFilterShouldReturnCorrectResults() throws SQLException {

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google").setStatusIs(200))) {
            long count = links.count();
            assertEquals(3, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setStatusBetween(100,600))) {
            long count = links.count();
            assertEquals(22, count);
        }

        try (Stream<CheckedLink> links = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setStatusBetween(1,90))) {
            long count = links.count();
            assertEquals(0, count);
        }
    }

    @Test
    public void combinedFilterShouldReturnCorrectResults() throws SQLException {
        try (Stream<CheckedLink> links = checkedLinkResource.get(
        		checkedLinkResource.getCheckedLinkFilter()
	        		.setStatusBetween(100,300)
	        		.setProviderGroupIs("Google")
	        		.setRecordIs("GoogleRecord")
	        		.setCheckedBetween(yesterday, tomorrow)
				)) {
            long count = links.count();
            assertEquals(3, count);
        }
    }

    @Test
    public void getWithListTestShouldReturnCorrectResults() throws SQLException {
        List<String> someURLs = Arrays.asList("http://www.ailla.org/waiting.html", "http://www.ailla.org/audio_files/EMP1M1B1.mp3");

        Map<String, CheckedLink> links = checkedLinkResource.getMap(
        		checkedLinkResource.getCheckedLinkFilter()
        			.setUrlIn("http://www.ailla.org/waiting.html", "http://www.ailla.org/audio_files/EMP1M1B1.mp3")
        		);
        assertEquals(2, links.size());

        assertEquals(someURLs.get(0), links.get(someURLs.get(0)).getUrl());
        assertEquals(someURLs.get(1), links.get(someURLs.get(1)).getUrl());

        links = checkedLinkResource.getMap(
        		checkedLinkResource.getCheckedLinkFilter()
        			.setUrlIn("https://www.google.com", "https://maps.google.com")
        			.setProviderGroupIs("Google")
        		);
        assertEquals(2, links.size());

        assertEquals("https://www.google.com", links.get("https://www.google.com").getUrl());
        assertEquals("https://maps.google.com", links.get("https://maps.google.com").getUrl());
        //shouldnt be in there
        assertNull(links.get("https://drive.google.com"));

        links = checkedLinkResource.get(Collections.emptyList(), Optional.empty());
        assertEquals(0, links.size());
    }

    @Test
    public void saveWithoutTupleInUrlsTableTestShouldNotSave() throws SQLException {
        assertFalse(checkedLinkResource.save(new CheckedLink("not in urls table url", null, 0, null, 0, 0, null, null, null, 0, null, null, Category.Broken)));
    }

    @Test
    public void filterWithStartAndEndTestShouldReturnCorrectResults() throws SQLException {
        try (Stream<CheckedLink> linksStream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setLimit(0, 10))) {
            List<CheckedLink> links = linksStream.collect(Collectors.toList());
            assertEquals(10, links.size());
        }


        try (Stream<CheckedLink> linksOneOffsetStream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setLimit(13, 10))) {
            List<CheckedLink> linksOneOffset = linksOneOffsetStream.collect(Collectors.toList());
            assertEquals(9, linksOneOffset.size());
        }
    }
    
    @Test
    public void testNullInFilter() throws SQLException {
       try (Stream<CheckedLink> stream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setUrlIs(null))) {
          assertEquals(0, stream.count());
       }
       
       try (Stream<CheckedLink> stream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setUrlIn(null, null))) {
          assertEquals(0, stream.count());
       }
       
       try (Stream<CheckedLink> stream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setIngestionDateIs(null))) {
          assertEquals(0, stream.count());
       }
       
       try (Stream<CheckedLink> stream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setRecordIs(null))) {
          assertEquals(0, stream.count());
       }
    }

    //the next two methods should be run in order
    @Test
    public void ZZ1saveTestShouldSaveCorrectly() throws SQLException {
        //before saving only 3 google urls
        try (Stream<CheckedLink> googleStream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google"))) {
            assertEquals(3, googleStream.count());
        }

        //save(first urls then status)
        linkToBeCheckedResource.save(new LinkToBeChecked(testURL, "GoogleRecord", "Google", "mimeType", today));
        CheckedLink checkedLink = new CheckedLink(testURL, "HEAD", 200, null, 100, 100, tomorrow, "Ok", "Google", 0, "GoogleRecord", "mimeType",Category.Ok);
        checkedLinkResource.save(checkedLink);

        //after saving should be 3
        try (Stream<CheckedLink> googleStream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google"))) {
            assertEquals(3, googleStream.count());
        }


        //and should contain
        try (Stream<CheckedLink> googleStream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google"))) {
            assertTrue(googleStream.anyMatch(x -> Objects.equals(x, checkedLink)));
        }

    }
	/*
	 * @Test public void ZZ2getHistoryTestShouldReturnCorrectResults() throws
	 * SQLException { //add again but with time then2 CheckedLink checkedLink = new
	 * CheckedLink(testURL, "HEAD", 200, null, 100, 100, then, "Ok", "Google", 0,
	 * "GoogleRecord", "mimeType", Category.Ok); CheckedLink checkedLink1 = new
	 * CheckedLink(testURL, "HEAD", 200, null, 100, 100, then1, "Ok", "Google", 0,
	 * "GoogleRecord", "mimeType",Category.Ok); CheckedLink checkedLink2 = new
	 * CheckedLink(testURL, "HEAD", 200, null, 100, 100, then2, "Ok", "Google", 0,
	 * "GoogleRecord", "mimeType",Category.Ok);
	 * 
	 * checkedLinkResource.save(checkedLink1);
	 * 
	 * assertEquals(checkedLink1, checkedLinkResource.get(testURL).get());
	 * 
	 * List<CheckedLink> history = checkedLinkResource.getHistory(testURL,
	 * CheckedLinkResource.Order.DESC); assertEquals(2, history.size());
	 * assertEquals(checkedLink, history.get(0));
	 * 
	 * //add again, history should have 2 checkedLinkResource.save(checkedLink2);
	 * assertEquals(checkedLink2, checkedLinkResource.get(testURL).get());
	 * 
	 * history = checkedLinkResource.getHistory(testURL,
	 * CheckedLinkResource.Order.DESC); assertEquals(3, history.size());
	 * assertEquals(checkedLink1, history.get(0)); assertEquals(checkedLink,
	 * history.get(1)); }
	 * 
	 * // @Test public void ZZ3deleteTestShouldDeleteCorrectly() throws SQLException
	 * { //first status then url checkedLinkResource.delete(testURL);
	 * linkToBeCheckedResource.delete(testURL);
	 * 
	 * //after deleting only 3 google urls try (Stream<CheckedLink> googleStream =
	 * checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")))) {
	 * assertEquals(3, googleStream.count()); }
	 * 
	 * //and shouldn't contain try (Stream<CheckedLink> googleStream =
	 * checkedLinkResource.get(Optional.of(new ACDHCheckedLinkFilter("Google")))) {
	 * assertFalse(googleStream.anyMatch(x -> x.getUrl().equals(testURL))); }
	 * 
	 * }
	 */
}
