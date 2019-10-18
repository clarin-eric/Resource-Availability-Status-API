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

package eu.clarin.cmdi.rasa.linkResources;

import eu.clarin.cmdi.rasa.DAO.Statistics.StatusStatistics;
import eu.clarin.cmdi.rasa.filters.impl.ACDHStatisticsCountFilter;
import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StatisticsResource {

    /* gets status statistics per collection. status statistics are
    the following info per status code:
    count,avg response time, max response time
    for a given collection or for all collections(either "Overall" or null).
    will return empty list if given collection doesn't exist
     */
    List<StatusStatistics> getStatusStatistics(String collection) throws SQLException;

    /*get overall statistics without grouping by status
    so get overall count, avg response time and max response time
    for a given collection or for all collections(either "Overall" or null).
    will return null if given collection doesn't exist
    * */
    Statistics getOverallStatistics(String collection) throws SQLException;

    long countStatusView(Optional<ACDHStatisticsCountFilter> filter) throws SQLException;
    long countUrlsTable(Optional<ACDHStatisticsCountFilter> filter) throws SQLException;
}
