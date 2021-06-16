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

        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(url, today, null, null, null, null);

        try(Stream<LinkToBeChecked> stream =  linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setUrlIs(url))){
        	assertEquals(linkToBeChecked, stream.findFirst().get());
        }
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
        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(testURL, yesterday, "GoogleRecord", "Google", "mimeType", yesterday);
        linkToBeCheckedResource.save(linkToBeChecked);


        CheckedLink checkedLink = new CheckedLink(testURL, null, null, null, null, 0, tomorrow, null, "Google", 0, "GoogleRecord", "mimeType", Category.Broken);
        checkedLinkResource.save(checkedLink);

        long allCount;
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter())) {
            allCount = stream.count();
        }

        //and should contain with the new harvestDate
        try (Stream<LinkToBeChecked> stream = linkToBeCheckedResource.get(linkToBeCheckedResource.getLinkToBeCheckedFilter().setUrlIs(testURL))) {
	        Optional<LinkToBeChecked> linkToBeCheckedReturned = stream.findFirst();
	        assertEquals(linkToBeChecked, linkToBeCheckedReturned.get());
        }
        //and should contain in status table
        try (Stream<CheckedLink> stream = checkedLinkResource.get(checkedLinkResource.getCheckedLinkFilter().setUrlIs(testURL))) {
	        Optional<CheckedLink> checkedLinkReturned = stream.findFirst();
	        assertEquals(checkedLink, checkedLinkReturned.get());
        }

    }

}
