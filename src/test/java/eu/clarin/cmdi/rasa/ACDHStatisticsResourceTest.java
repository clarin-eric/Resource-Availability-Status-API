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
import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.StatusStatistics;
import eu.clarin.cmdi.rasa.filters.impl.ACDHStatisticsCountFilter;
import eu.clarin.cmdi.rasa.helpers.Table;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ACDHStatisticsResourceTest extends TestConfig {

    private static final String testURL = "www.facebook.com";

    @Test
    public void AbasicStatusStatisticsTestShouldReturnCorrectResults() throws SQLException {
        StatusStatistics googleStatistics = new StatusStatistics(200, 3L, 358.3333, 440L);
        StatusStatistics statistics200 = new StatusStatistics(200, 16L, 355.625, 613L);
        StatusStatistics statistics400 = new StatusStatistics(400, 6L, 48.3333, 56L);

        List<StatusStatistics> googleList = statisticsResource.getStatusStatistics("Google");
        assertEquals(1, googleList.size());
        assertEquals(googleStatistics, googleList.get(0));

        List<StatusStatistics> overallList = statisticsResource.getStatusStatistics(null);
        assertEquals(2, overallList.size());
        assertTrue(overallList.contains(statistics200));
        assertTrue(overallList.contains(statistics400));

        assertEquals(statisticsResource.getStatusStatistics(null), statisticsResource.getStatusStatistics("Overall"));
        assertNotEquals(statisticsResource.getStatusStatistics("Something"), statisticsResource.getStatusStatistics("Overall"));
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
    public void CcountTestWithStatusCodesShouldReturnCorrectResults() throws SQLException {
        assertEquals(22, statisticsResource.countTable(new ACDHStatisticsCountFilter(Table.STATUS)));

        ACDHStatisticsCountFilter acdhStatisticsFilter = new ACDHStatisticsCountFilter(true, false, Table.STATUS);
        assertEquals(6, statisticsResource.countTable(acdhStatisticsFilter));
        assertEquals(6, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter(false, true, Table.STATUS);
        assertEquals(0, statisticsResource.countTable(acdhStatisticsFilter));
        assertEquals(0, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, true, false, Table.STATUS);
        assertEquals(0, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, false, true, Table.STATUS);
        assertEquals(0, statisticsResource.countTable(acdhStatisticsFilter));
    }

    @Test
    public void DbasicCountTestShouldReturnCorrectResults() throws SQLException {
        ACDHStatisticsCountFilter acdhStatisticsFilter = new ACDHStatisticsCountFilter(Table.STATUS);//count everything
        assertEquals(22, statisticsResource.countTable(acdhStatisticsFilter));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter(Table.URLS);//count everything
        assertEquals(22, statisticsResource.countTable(acdhStatisticsFilter));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, Table.STATUS);
        assertEquals(3, statisticsResource.countTable(acdhStatisticsFilter));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, Table.URLS);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", "GoogleRecord", Table.STATUS);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", "GoogleRecord", Table.URLS);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter(null, "GoogleRecord", Table.STATUS);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter(null, "GoogleRecord", Table.URLS);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("NotGoogle", null, Table.STATUS);
        assertEquals(19, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("NotGoogle", null, Table.URLS);
        assertEquals(19, statisticsResource.countTable((acdhStatisticsFilter)));

        linkToBeCheckedResource.save(new LinkToBeChecked(testURL, "FacebookRecord", "Facebook", null, System.currentTimeMillis()));
        checkedLinkResource.save(new CheckedLink(testURL, "GET", 200, null, 100, 100, Timestamp.valueOf(LocalDateTime.now()), "Ok", "Facebook", 0, "FacebookRecord", null));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter(Table.STATUS);
        assertEquals(23, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter(Table.URLS);
        assertEquals(23, statisticsResource.countTable((acdhStatisticsFilter)));

        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Facebook", null, Table.STATUS);
        assertEquals(1, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Facebook", null, Table.URLS);
        assertEquals(1, statisticsResource.countTable((acdhStatisticsFilter)));

        //this shouldn't have changed
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, Table.STATUS);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));
        acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, Table.URLS);
        assertEquals(3, statisticsResource.countTable((acdhStatisticsFilter)));
    }

    @Test(expected = SQLException.class)
    public void EcountUrlsTableWithStatusShouldThrowException() throws SQLException {
        ACDHStatisticsCountFilter acdhStatisticsFilter = new ACDHStatisticsCountFilter(null, null, true, true, Table.URLS);
        statisticsResource.countTable((acdhStatisticsFilter));
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
