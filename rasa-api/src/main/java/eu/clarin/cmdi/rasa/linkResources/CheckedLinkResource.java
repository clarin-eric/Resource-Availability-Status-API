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

//import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;

import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.DAO.CheckedLink;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface CheckedLinkResource {

    /**
     * ASC for ascending
     * DESC for descending
     */
    enum Order {
        ASC,
        DESC
    }

    /**
     * Retrieve CheckedLink for single url from status table
     *
     * @param url Url to be retrieved
     * @return CheckedLink for the given url
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Optional<CheckedLink> get(String url) throws SQLException;

    /* retrieve for single url from collection */

    /**
     * Retrieve CheckedLink for single url from status table from a given collection. This is faster than get(String url), because the search is narrowed down via collection.
     *
     * @param url        Url to be retrieved
     * @param collection collection of the url
     * @return Checked link for the given url from the given collection
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Optional<CheckedLink> get(String url, String collection) throws SQLException;

    /*
     * returned stream needs to be closed after use, so use it with try with resources
     */

    /**
     * Get all urls that match a filter.
     * Returned stream needs to be closed after use, so use it with try with resources.
     * Recommended to use, so it closes automatically: try(Stream<CheckedLink links=checkedLinkResource.get(Optional.of(filter))){...}
     *
     * @param filter filter to apply on the query
     * @return A Stream of CheckedLink elements. It needs to be closed after use.
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Stream<CheckedLink> get(Optional<CheckedLinkFilter> filter) throws SQLException;

    /**
     * Get all urls that match a filter but limiting from start to end
     * Returned stream needs to be closed after use, so use it with try with resources.
     * Recommended to use, so it closes automatically: try(Stream<CheckedLink links=checkedLinkResource.get(Optional.of(filter))){...}
     *
     * @param filter filter to apply on the query
     * @param start resulting urls will start from this number. If there are 20 results and start is set to 10, it will start from the 10th and go until 20.
     * @param end resulting urls will end at this number. if start is 0, this can be regarded as limit. If there are 20 results and end is set to 10, it will start from the 0 and go until 10.
     * @return A Stream of CheckedLink elements. It needs to be closed after use.
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Stream<CheckedLink> get(Optional<CheckedLinkFilter> filter, int start, int end) throws SQLException;

    /*
    */

    /**
     * Batch retrieval with url as key with optional filtering.
     * @param urls A list of urls to be retrieved
     * @param filter Optional filter to be applied on the list of urls
     * @return A map of url to CheckedLink objects
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Map<String, CheckedLink> get(Collection<String> urls, Optional<CheckedLinkFilter> filter) throws SQLException;

    /**
     * Save a checked link into status table, set its nextCheckDate on urls table, move old checked link in status table into history if it exists
     * @param checkedLink checked link to be persisted
     * @return If the operation was successful
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Boolean save(CheckedLink checkedLink) throws SQLException;

    /*  */

    /**
     * Delete a url from status table
     * @param url url to be deleted
     * @return If the operation was successful
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Boolean delete(String url) throws SQLException;

    /**
     * Retrieval of history for one url as key with optional filtering, Order is timestamp based.
     * @param url url to be retrieved
     * @param order Order of the results on the timestamp
     * @return A List of CheckedLink objects of the same url from the history table
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    List<CheckedLink> getHistory(String url, Order order) throws SQLException;

    /* Move from linksChecked to linkCheckedHistory. */

    /**
     * Save a checkedLink from status table to history table
     * @param checkedLink Checked Link to be moved
     * @return If the operation was successful
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Boolean saveToHistory(CheckedLink checkedLink) throws SQLException;

    /**
     * Save a checkedLink from status table to history table by finding it in status table first
     * @param url url of checked Link to be moved
     * @return If the operation was successful
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Boolean saveToHistory(String url) throws SQLException;

}
