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

import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.filters.impl.ACDHLinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;

//method orders are alphabetical, so letters in the start of the names
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHLinkToBeCheckedResourceTest extends TestConfig {

    private String testURL = "https://mail.google.com";

    @Test
    public void AbasicGETTestShouldReturnCorrectResults() throws SQLException {

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
    public void BbasicGETListTestShouldReturnCorrectResults() throws SQLException {

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
    public void CsaveTestShouldSaveCorrectly() throws SQLException {
        //before saving only 3 google urls
        Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")));
        assertEquals(3, googleStream.count());

        //save
        LinkToBeChecked linkToBeChecked = new LinkToBeChecked(testURL, "GoogleRecord", "Google", "mimeType");
        linkToBeCheckedResource.save(linkToBeChecked);

        //after saving should be 4
        googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")));
        assertEquals(4, googleStream.count());

        //and should contain
        googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")));
        assertTrue(googleStream.anyMatch(x -> Objects.equals(x, linkToBeChecked)));
    }

    @Test
    public void DDeleteTestShouldSaveCorrectly() throws SQLException {
        linkToBeCheckedResource.delete(testURL);

        //after deleting only 3 google urls
        Stream<LinkToBeChecked> googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")));
        assertEquals(3, googleStream.count());

        //and shouldn't contain
        googleStream = linkToBeCheckedResource.get(Optional.of(new ACDHLinkToBeCheckedFilter("Google")));
        assertFalse(googleStream.anyMatch(x -> x.getUrl().equals(testURL)));
    }

}