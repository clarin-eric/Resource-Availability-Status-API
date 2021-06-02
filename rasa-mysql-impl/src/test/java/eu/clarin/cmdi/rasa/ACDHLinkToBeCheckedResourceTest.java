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
import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

//methods should be executed in alphabetical order, so letters in the start of the names matter
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHLinkToBeCheckedResourceTest extends TestConfig {

    private final String testURL = "https://mail.google.com";
    private final String testURL1 = "https://scholar.google.com";
    private static long now;

    @BeforeClass
    public static void setup() {
        //set it once and only once
        now = System.currentTimeMillis();
    }

    @Test
    public void AAbasicSimpleGETTestShouldReturnCorrectly() throws SQLException {
        //same as the first entry in initDB.sql
        String url = "http://www.ailla.org/waiting.html";

        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(url, Timestamp.valueOf("2000-01-01 00:00:00"), "record", "NotGoogle", null, (Timestamp) null);

        assertEquals(linkToBeChecked, linkToBeCheckedResource.get(url).get());
    }


    @Test
    public void GgetCollectionNamesTestShouldReturnCorrectNames() throws SQLException {
        List<String> collectionNames = linkToBeCheckedResource.getProviderGroupNames();
        assertEquals(2, collectionNames.size());
        assertTrue(collectionNames.contains("Google"));
        assertTrue(collectionNames.contains("NotGoogle"));
    }


    //@Test
    public void IdeleteOldLinksTestShouldDeleteCorrectly() throws SQLException {

        //save with 86400000 milliseconds before which is one day less
        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(testURL, new Timestamp(now - 86400000), "GoogleRecord", "Google", "mimeType", new Timestamp(now - 86400000));
        linkToBeCheckedResource.save(linkToBeChecked);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setNanos(0);//mysql vorburger embedded db hack, some version mismatch causes millesconds to be always 0
        CheckedLink checkedLink = new CheckedLink(testURL, null, null, null, null, 0, timestamp, null, "Google", 0, "GoogleRecord", "mimeType", Category.Broken);
        checkedLinkResource.save(checkedLink);

        long allCount;
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.empty())) {
            allCount = stream.count();
        }

        //and should contain with the new harvestDate
        Optional<LinkToBeChecked> linkToBeCheckedReturned = linkToBeCheckedResource.get(testURL);
        assertEquals(linkToBeChecked, linkToBeCheckedReturned.get());

        //and should contain in status table
        Optional<CheckedLink> checkedLinkReturned = checkedLinkResource.get(testURL);
        assertEquals(checkedLink, checkedLinkReturned.get());

        //deleteOldLinks should delete the newly added linkToBeChecked
        assertEquals(1, linkToBeCheckedResource.deleteOldLinks(now));

        long newCount;
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(Optional.empty())) {
            newCount = stream.count();
        }
        assertEquals(allCount - 1, newCount);

        //and shouldn't contain
        assertTrue(linkToBeCheckedResource.get(testURL).isEmpty());

        //and shouldn't contain in status table
        assertTrue(checkedLinkResource.get(testURL).isEmpty());

        //and should be in history
        List<CheckedLink> historyList = checkedLinkResource.getHistory(testURL, CheckedLinkResource.Order.DESC);
        assertEquals(1, historyList.size());
        assertEquals(checkedLink, historyList.get(0));

    }

}
