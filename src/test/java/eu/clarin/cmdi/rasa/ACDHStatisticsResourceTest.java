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

import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.StatusStatistics;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ACDHStatisticsResourceTest extends TestConfig {


    @Test
    public void basicStatusStatisticsTestShouldReturnCorrectResults() throws SQLException {
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
    public void basicOverallStatisticsTestShouldReturnCorrectResults() throws SQLException {
//        Statistics googleStatistics = new Statistics(3L, 358.3333, 440L);
//        Statistics overallStatistics = new Statistics(22L, 271.8182, 613L);
//
//        Statistics googleStatisticsActual = statisticsResource.getOverallStatistics("Google");
//        assertEquals(googleStatistics, googleStatisticsActual);
//
//        Statistics overallStatisticsActual = statisticsResource.getOverallStatistics(null);
//        assertEquals(overallStatistics, overallStatisticsActual);
//
//        assertEquals(statisticsResource.getOverallStatistics(null), statisticsResource.getOverallStatistics("Overall"));
        assertNotEquals(statisticsResource.getOverallStatistics("Something"), statisticsResource.getOverallStatistics("Overall"));
    }
}
