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

import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.StatusStatistics;
import eu.clarin.cmdi.rasa.filters.StatisticsCountFilter;

import java.sql.SQLException;
import java.util.List;

public interface StatisticsResource {

    /**
     * Gets status statistics per collection. Status statistics are the following info per status code:
     * count,avg response time, max response time
     * calls getStatusStatistics() for all collections (if given collection is null or equals "Overall").
     * @param collection collection requested for the statistics
     * @return A list of StatusStatistics for each status code, will return empty list if given collection doesn't exist
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    List<StatusStatistics> getStatusStatistics(String collection) throws SQLException;

    /**
     * Gets overall status statistics. Status statistics are the following info per status code:
     * count,avg response time, max response time
     * @return A list of StatusStatistics for each status code
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    List<StatusStatistics> getStatusStatistics() throws SQLException;

    /**
     * Gets overall statistics per collection without grouping by status, so get overall count, avg response time and max response time
     * calls getOverallStatistics() for all collections (if given collection is null or equals "Overall").
     * will return null if given collection doesn't exist
     * @param collection collection requested for the statistics
     * @return A Statistics object for the requested collection
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Statistics getOverallStatistics(String collection) throws SQLException;

    /**
     * Gets overall statistics without grouping by status, so get overall count, avg response time and max response time
     * @return A Statistics object for the whole database
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Statistics getOverallStatistics() throws SQLException;

    /**
     * Counts table with the given filter. Table name in the filter must be set
     * @param filter Filter to be applied to the count query
     * @return number resulting from the count
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    long countTable(StatisticsCountFilter filter) throws SQLException;

}
