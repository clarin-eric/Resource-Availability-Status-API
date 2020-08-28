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

import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;
import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.filters.impl.ACDHLinkToBeCheckedFilter;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

//methods should be executed in alphabetical order, so letters in the start of the names matter
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHLinkToBeCheckedResourceTest extends TestConfig {

    private final String testURL = "https://mail.google.com";
    private final String testURL1 = "https://scholar.google.com";
    private static Long now;

    @BeforeClass
    public static void setup() {
        //set it once and only once
        now = System.currentTimeMillis();
    }

    @Test
    public void AAbasicSimpleGETTestShouldReturnCorrectly() throws SQLException {
        //same as the first entry in initDB.sql
        String url = "http://www.ailla.org/waiting.html";
        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(url, "record", "NotGoogle", null, null);

        assertEquals(linkToBeChecked, linkToBeCheckedResource.get(url).get());
    }


    @Test
    public void AbasicGETTestShouldReturnCorrectResults() throws SQLException {

        LinkToBeCheckedFilter filter = new ACDHLinkToBeCheckedFilter("NotGoogle");
        try (Stream<LinkToBeChecked> linksToBeChecked = linkToBeCheckedResource.get(Optional.of(filter))) {
            assertEquals(otherUrls.size(), linksToBeChecked.count());
        }


        //need to get it twice to get the size and loop through it, because streams don't allow to be operated on twice.
        //https://stackoverflow.com/questions/38044849/is-possible-to-know-the-size-of-a-stream-without-using-a-terminal-operation
        try (Stream<LinkToBeChecked> linksToBeChecked = linkToBeCheckedResource.get(Optional.of(filter))) {
            linksToBeChecked.forEach(linkToBeChecked -> {
                assertEquals("NotGoogle", linkToBeChecked.getCollection());
                assertTrue(otherUrls.contains(linkToBeChecked.getUrl()));
            });
        }

        filter = new ACDHLinkToBeCheckedFilter("Google");
        try (Stream<LinkToBeChecked> linksToBeChecked = linkToBeCheckedResource.get(Optional.of(filter))) {
            assertEquals(googleUrls.size(), linksToBeChecked.count());
        }

        try (Stream<LinkToBeChecked> linksToBeChecked = linkToBeCheckedResource.get(Optional.of(filter))) {
            linksToBeChecked.forEach(linkToBeChecked -> {
                assertEquals("Google", linkToBeChecked.getCollection());
                assertTrue(googleUrls.contains(linkToBeChecked.getUrl()));
            });
        }

    }

    @Test
    public void BbasicGETListTestShouldReturnCorrectResults() throws SQLException {

        LinkToBeCheckedFilter filter = new ACDHLinkToBeCheckedFilter("NotGoogle");
        List<LinkToBeChecked> linksToBeChecked = linkToBeCheckedResource.getList(Optional.of(filter));

        assertEquals(otherUrls.size(), linksToBeChecked.size());
        linksToBeChecked.forEach(linkToBeChecked -> {
            assertEquals("NotGoogle", linkToBeChecked.getCollection());
            assertTrue(otherUrls.contains(linkToBeChecked.getUrl()));
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
    public void CsaveTestShouldSaveCorrectly() throws SQLException {
        //before saving only 3 google urls
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertEquals(3, googleStream.count());
        }

        //save
        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(testURL, "GoogleRecord", "Google", "mimeType", now);
        linkToBeCheckedResource.save(linkToBeChecked);

        //after saving should be 4
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertEquals(4, googleStream.count());
        }

        //and should contain
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertTrue(googleStream.anyMatch(x -> Objects.equals(x, linkToBeChecked)));
        }
    }

    @Test
    public void DDeleteTestShouldSaveCorrectly() throws SQLException {
        linkToBeCheckedResource.delete(testURL);

        //after deleting only 3 google urls
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertEquals(3, googleStream.count());
        }

        //and shouldn't contain
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertFalse(googleStream.anyMatch(x -> x.getUrl().equals(testURL)));
        }

    }


    @Test
    public void EbatchInsertTestShouldInsertCorrectly() throws SQLException {
        //before saving only 3 google urls
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertEquals(3, googleStream.count());
        }

        //save
        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(testURL, "GoogleRecord", "Google", "mimeType", now);
        LinkToBeChecked linkToBeChecked1 = new LinkToBeChecked(testURL1, "GoogleRecord", "Google", "mimeType", now);
        linkToBeCheckedResource.save(Arrays.asList(linkToBeChecked, linkToBeChecked1));

        //after saving should be 5
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertEquals(5, googleStream.count());
        }

        //and should contain
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertTrue(googleStream.anyMatch(x -> Objects.equals(x, linkToBeChecked)));
        }

        //and 1 should contain
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertTrue(googleStream.anyMatch(x -> Objects.equals(x, linkToBeChecked1)));
        }
    }


    @Test
    public void FDeleteTestShouldDeleteCorrectly() throws SQLException {
        linkToBeCheckedResource.delete(Arrays.asList(testURL, testURL1));

        //after deleting only 3 google urls
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertEquals(3, googleStream.count());
        }

        //and shouldn't contain
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertFalse(googleStream.anyMatch(x -> x.getUrl().equals(testURL)));
        }

        //and shouldn't contain
        try (Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")))) {
            assertFalse(googleStream.anyMatch(x -> x.getUrl().equals(testURL1)));
        }

    }

    @Test
    public void GgetCollectionNamesTestShouldReturnCorrectNames() throws SQLException {
        List<String> collectionNames = linkToBeCheckedResource.getCollectionNames();
        assertEquals(2, collectionNames.size());
        assertTrue(collectionNames.contains("Google"));
        assertTrue(collectionNames.contains("NotGoogle"));
    }


    @Test
    public void HupdateDateTestShouldUpdateCorrectly() throws SQLException {
        //initDB.sql initializes all urls table with null as harvestDate, so lets update all to now

        //first check if its really 0
        long nowHarvestDateCount;
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter(now)))) {
            nowHarvestDateCount = stream.count();
        }
        assertEquals(0,nowHarvestDateCount);

        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.empty())) {
            List<String> toUpdateList = new ArrayList<>();
            stream.forEach(linkToBeChecked -> {
                toUpdateList.add(linkToBeChecked.getUrl());
            });
            assertTrue(linkToBeCheckedResource.updateDate(toUpdateList, now));
        }

        long allCount;
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.empty())) {
            allCount = stream.count();
        }

        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter(now)))) {
            nowHarvestDateCount = stream.count();
        }
        assertNotEquals(0,nowHarvestDateCount);
        //means all of them has the harvestDate now
        assertEquals(allCount, nowHarvestDateCount);

        //all of them should have now as their harvestDate
        //double check just to see if the filter is working correctly
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.empty())) {
            stream.forEach(linkToBeChecked -> {
                assertEquals(now, linkToBeChecked.getHarvestDate());
            });
        }


    }

    @Test
    public void IdeleteOldLinksTestShouldDeleteCorrectly() throws SQLException {

        //save with 86400000 milliseconds before which is one day less
        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(testURL, "GoogleRecord", "Google", "mimeType", now - 86400000);
        linkToBeCheckedResource.save(linkToBeChecked);

        long allCount;
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.empty())) {
            allCount = stream.count();
        }

        //and should contain with the new harvestDate
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.empty())) {
            assertTrue(stream.anyMatch(x -> Objects.equals(x, linkToBeChecked)));
        }

        //deleteOldLinks should delete the newly added linkToBeChecked
        assertEquals(1, linkToBeCheckedResource.deleteOldLinks(now));

        long newCount;
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.empty())) {
            newCount = stream.count();
        }
        assertEquals(allCount - 1, newCount);

        //and shouldn't contain
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.empty())) {
            assertFalse(stream.anyMatch(x -> Objects.equals(x, linkToBeChecked)));
        }

    }

}
