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
import eu.clarin.cmdi.rasa.DAO.Statistics.CategoryStatistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHCategoryStatisticsResourceTest extends TestConfig {

    private static final String testURL = "https://www.facebook.com";

    @Test
    public void AbasicCategoryStatisticsTestShouldReturnCorrectResults() throws SQLException {
        CategoryStatistics googleStatistics = new CategoryStatistics(Category.Ok, 3L, 358.3333, 440L);
        CategoryStatistics statisticsOk = new CategoryStatistics(Category.Ok, 16L, 355.625, 613L);
        CategoryStatistics statisticsBroken = new CategoryStatistics(Category.Broken, 6L, 48.3333, 56L);

        try(Stream<CategoryStatistics> stream = checkedLinkResource.getCategoryStatistics(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google"))){
        		List<CategoryStatistics> googleList = stream.collect(Collectors.toList());
        		assertEquals(1, googleList.size());
        		assertEquals(googleStatistics, googleList.get(0));
        }

        try(Stream<CategoryStatistics> stream = checkedLinkResource.getCategoryStatistics(checkedLinkResource.getCheckedLinkFilter())){
	        List<CategoryStatistics> overallList = stream.collect(Collectors.toList());
	        assertEquals(2, overallList.size());
	        assertTrue(overallList.contains(statisticsOk));
	        assertTrue(overallList.contains(statisticsBroken));
        }
        
        try(
    		Stream<CategoryStatistics> stream1 = checkedLinkResource.getCategoryStatistics(checkedLinkResource.getCheckedLinkFilter());
    		Stream<CategoryStatistics> stream2 = checkedLinkResource.getCategoryStatistics(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Overall"))){
    	        assertEquals(stream1.collect(Collectors.toList()), stream2.collect(Collectors.toList()));        			
		}
        try(
    		Stream<CategoryStatistics> stream1 = checkedLinkResource.getCategoryStatistics(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Something"));
    		Stream<CategoryStatistics> stream2 = checkedLinkResource.getCategoryStatistics(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Overall"))){
    	        assertNotEquals(stream1.collect(Collectors.toList()), stream2.collect(Collectors.toList()));        			
		}	
    }

    @Test
    public void BbasicOverallStatisticsTestShouldReturnCorrectResults() throws SQLException {
        Statistics googleStatistics = new Statistics(3L, 358.3333, 440L);
        Statistics overallStatistics = new Statistics(22L, 271.8182, 613L);

        Statistics googleStatisticsActual = checkedLinkResource.getStatistics(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google").setIsActive(true));
        assertEquals(googleStatistics, googleStatisticsActual);

        Statistics overallStatisticsActual = checkedLinkResource.getStatistics(checkedLinkResource.getCheckedLinkFilter());
        assertEquals(overallStatistics, overallStatisticsActual);

        assertEquals(checkedLinkResource.getStatistics(checkedLinkResource.getCheckedLinkFilter()), checkedLinkResource.getStatistics(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Overall")));
        assertNotEquals(checkedLinkResource.getStatistics(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Something")), checkedLinkResource.getStatistics(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Overall")));
    }

    @Test
    public void CcountTestWithCategoriesShouldReturnCorrectResults() throws SQLException {
        assertEquals(22, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter()));


        assertEquals(16, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setCategoryIs(Category.Ok)));

        assertEquals(0, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setCategoryIs(Category.Undetermined)));

        assertEquals(6, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setCategoryIs(Category.Broken)));

        assertEquals(3, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setCategoryIs(Category.Ok).setProviderGroupIs("Google")));

        assertEquals(0, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setCategoryIs(Category.Broken).setProviderGroupIs("Google")));

        assertEquals(3, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setCategoryIn(Category.Ok, Category.Broken).setProviderGroupIs("Google")));
    }

    @Test
    public void DbasicCountTestShouldReturnCorrectResults() throws SQLException {
        assertEquals(22, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter()));

        linkToBeCheckedResource.getCount(linkToBeCheckedResource.getLinkToBeCheckedFilter());


        assertEquals(3, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google")));

        assertEquals(3, linkToBeCheckedResource.getCount(linkToBeCheckedResource.getLinkToBeCheckedFilter().setProviderGroupIs("Google")));

        assertEquals(3, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google").setRecordIs("GoogleRecord")));

        assertEquals(3, linkToBeCheckedResource.getCount(linkToBeCheckedResource.getLinkToBeCheckedFilter().setProviderGroupIs("Google").setRecordIs("GoogleRecord")));

        assertEquals(3, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setRecordIs("GoogleRecord")));

        assertEquals(3, linkToBeCheckedResource.getCount(linkToBeCheckedResource.getLinkToBeCheckedFilter().setRecordIs("GoogleRecord")));

        assertEquals(19, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("NotGoogle")));

        assertEquals(19, linkToBeCheckedResource.getCount(linkToBeCheckedResource.getLinkToBeCheckedFilter().setProviderGroupIs("NotGoogle")));

        linkToBeCheckedResource.save(new LinkToBeChecked(testURL, "datasource", "FacebookRecord", "Facebook", null, today));
        checkedLinkResource.save(new CheckedLink(testURL,"GET", 200, null, 100l, 100, today, "Ok", 0, Category.Ok));


        assertEquals(23, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter()));

        assertEquals(23, linkToBeCheckedResource.getCount(linkToBeCheckedResource.getLinkToBeCheckedFilter()));

        assertEquals(1, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Facebook")));

        assertEquals(1, linkToBeCheckedResource.getCount(linkToBeCheckedResource.getLinkToBeCheckedFilter().setProviderGroupIs("Facebook")));

        //this shouldn't have changed
        assertEquals(3, checkedLinkResource.getCount(checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("Google")));

        assertEquals(3, linkToBeCheckedResource.getCount(linkToBeCheckedResource.getLinkToBeCheckedFilter().setProviderGroupIs("Google")));
    }


    @AfterClass
    public static void tearDownClass() {
        try {
            //it is deleted from status via cascade automatically when deleted from urls
            linkToBeCheckedResource.delete(testURL);
        } catch (SQLException e) {
            fail();
        }
    }
}
