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
import eu.clarin.cmdi.rasa.filters.impl.ACDHStatisticsCountFilter;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHCategoryStatisticsResourceTest extends TestConfig {

    private static final String testURL = "www.facebook.com";

    @Test
    public void AbasicCategoryStatisticsTestShouldReturnCorrectResults() throws SQLException {
        CategoryStatistics googleStatistics = new CategoryStatistics("Ok", 3L, 358.3333, 440L);
        CategoryStatistics statisticsOk = new CategoryStatistics("Ok", 16L, 355.625, 613L);
        CategoryStatistics statisticsBroken = new CategoryStatistics("Broken", 6L, 48.3333, 56L);

        List<CategoryStatistics> googleList = statisticsResource.getCategoryStatistics("Google");
        assertEquals(1, googleList.size());
        assertEquals(googleStatistics, googleList.get(0));

        List<CategoryStatistics> overallList = statisticsResource.getCategoryStatistics(null);
        assertEquals(2, overallList.size());
        assertTrue(overallList.contains(statisticsOk));
        assertTrue(overallList.contains(statisticsBroken));

        assertEquals(statisticsResource.getCategoryStatistics(null), statisticsResource.getCategoryStatistics("Overall"));
        assertEquals(statisticsResource.getCategoryStatistics(), statisticsResource.getCategoryStatistics("Overall"));
        assertNotEquals(statisticsResource.getCategoryStatistics("Something"), statisticsResource.getCategoryStatistics("Overall"));
    }

    @Test
    public void BbasicOverallStatisticsTestShouldReturnCorrectResults() throws SQLException {
        Statistics googleStatistics = new Statistics(3L, 358.3333, 440L);
        Statistics overallStatistics = new Statistics(22L, 271.8182, 613L);

        Statistics googleStatisticsActual = statisticsResource.getOverallStatistics("Google");
        assertEquals(googleStatistics, googleStatisticsActual);

        Statistics overallStatisticsActual = statisticsResource.getOverallStatistics(null);
        assertEquals(overallStatistics, overallStatisticsActual);

        assertEquals(statisticsResource.getOverallStatistics(null), statisticsResource.getOverallStatistics("Overall"));
        assertNotEquals(statisticsResource.getOverallStatistics("Something"), statisticsResource.getOverallStatistics("Overall"));
    }

    @Test
    public void CcountTestWithCategoriesShouldReturnCorrectResults() throws SQLException {
        assertEquals(22, statisticsResource.countTable(new ACDHStatisticsCountFilter(false)));

        ACDHStatisticsCountFilter acdhStatisticsFilter = new ACDHStatisticsCountFilter(Collections.singletonList(Category.Ok));
        assertEquals(16, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter(Collections.singletonList(Category.Undetermined));
        assertEquals(0, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter(Collections.singletonList(Category.Broken));
        assertEquals(6, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, Collections.singletonList(Category.Ok));
        assertEquals(3, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, Collections.singletonList(Category.Broken));
        assertEquals(0, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, Arrays.asList(Category.Ok, Category.Broken));
        assertEquals(3, statisticsResource.countTable(acdhStatisticsFilter));
    }

    @Test
    public void DbasicCountTestShouldReturnCorrectResults() throws SQLException {
        ACDHStatisticsCountFilter acdhStatisticsFilter = new ACDHStatisticsCountFilter(false);//count everything
        assertEquals(22, statisticsResource.countTable(acdhStatisticsFilter));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter(true);//count everything
        assertEquals(22, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, false);
        assertEquals(3, statisticsResource.countTable(acdhStatisticsFilter));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, true);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", "GoogleRecord", false);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", "GoogleRecord", true);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter(null, "GoogleRecord", false);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter(null, "GoogleRecord", true);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("NotGoogle", null, false);
        assertEquals(19, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("NotGoogle", null, true);
        assertEquals(19, statisticsResource.countTable((acdhStatisticsFilter)));

        linkToBeCheckedResource.save(new LinkToBeChecked(testURL, new Timestamp(System.currentTimeMillis()), "FacebookRecord", "Facebook", null, new Timestamp(System.currentTimeMillis())));
        checkedLinkResource.save(new CheckedLink(testURL,"GET", 200, null, 100, 100, Timestamp.valueOf(LocalDateTime.now()), "Ok", "Facebook", 0, "FacebookRecord", null, Category.Ok));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter(false);
        assertEquals(23, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter(true);
        assertEquals(23, statisticsResource.countTable((acdhStatisticsFilter)));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Facebook", null, false);
        assertEquals(1, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Facebook", null, true);
        assertEquals(1, statisticsResource.countTable((acdhStatisticsFilter)));

        //this shouldn't have changed
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, false);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, true);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));
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
