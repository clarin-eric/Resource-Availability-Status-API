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

package eu.clarin.cmdi.rasa.filters;

import org.apache.commons.lang3.Range;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class creates a filter for the status table with the given values through the constructor
 */
public interface CheckedLinkFilter extends Filter {
    /**
     * returns the statuses set for the filter.
     * @return range of integers that are the accepted statuses
     */
    Range<Integer> getStatus();

    /**
     *
     * @return the date set for checkedBefore
     */
    LocalDateTime getCheckedBeforeDate();

    /**
     *
     * @return the date set for checkedAfter
     */
    LocalDateTime getCheckedAfterDate();

    /**
     * Gets the collection
     * @return collection
     */
    String getCollection();

    /**
     * Gets the zoneID
     * @return zone id for the filter
     */
    ZoneId getZone();

    /**
     * Sets the start.If there are 20 results and start is set to 10, it will start from the 10th and go until 20.
     * @param start starting line to read from the database
     * @return this
     */
    CheckedLinkFilter setStart(int start);

    /**
     * Sets the end.If there are 20 results and end is set to 10, it will start from the 0 and go until 10.
     * @param end last line to read from the database
     * @return this
     */
    CheckedLinkFilter setEnd(int end);

    /**
     * Same as getStatement(Connection con) but only the urls within the inList will be returned if they match the filter variables.
     * If a url matches the given variables in the filter but is not in inList, it won't be in the results
     * @param con database connection
     * @param inList filters out the results, only urls within this list can be in the results
     * @param addInListParams function that adds parameters for the 'in list'; takes the next available parameter index and returns the parameter index to continue with
     * @return fully prepared statement with filter variables set and ready to execute
     * @throws SQLException can occur during preparing the statement
     */
    PreparedStatement getStatement(Connection con, String inList, BiFunction<PreparedStatement, Integer, Integer> addInListParams) throws SQLException;

}
