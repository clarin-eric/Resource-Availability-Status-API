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

import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface LinkToBeCheckedResource {

    /**
     * Retrieve LinkToBeChecked for single url
     *
     * @param url url of the row
     * @return found LinkToBeChecked
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Optional<LinkToBeChecked> get(String url) throws SQLException;

    /**
     * Get all urls that match a filter as a stream.
     * Returned stream needs to be closed after use, so use it with try with resources.
     * Recommended to use, so it closes automatically: try(Stream<LinkToBeChecked links=linkToBeCheckedResource.get(Optional.of(filter))){...}
     *
     * @param filter filter to apply on the query
     * @return A Stream of LinkToBeChecked elements. It needs to be closed after use.
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Stream<LinkToBeChecked> get(Optional<LinkToBeCheckedFilter> filter) throws SQLException;

    /**
     * Get all urls that match a filter as a list
     *
     * @param filter filter to apply on the query
     * @return A List of LinkToBeChecked elements
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    List<LinkToBeChecked> getList(Optional<LinkToBeCheckedFilter> filter) throws SQLException;

    /**
     * Save a link to be checked into urls table, if it already exists in the collection, it fails but is ignored
     *
     * @param linkToBeChecked Element to be persisted
     * @return If the operation was successful(not if the linkToBeChecked was persisted)
     * @throws SQLException occurs if there was an error during statement preparation
     */
    Boolean save(LinkToBeChecked linkToBeChecked) throws SQLException;

    /**
     * Batch insert links to be checked into urls, only not existing urls are persisted.
     *
     * @param linksToBeChecked List of elements to be persisted
     * @return If at least one url from the list was persisted
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Boolean save(List<LinkToBeChecked> linksToBeChecked) throws SQLException;

    /**
     * Delete an element from urls table
     *
     * @param url Url to be deleted
     * @return If the url was deleted
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Boolean delete(String url) throws SQLException;

    /**
     * Delete multiple elements from the urls table
     *
     * @param urls List of urls to be deleted
     * @return If at least one url from the list was deleted
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Boolean delete(List<String> urls) throws SQLException;

    /**
     * Retrieve all the names of all collections in the database(urls table)
     *
     * @return List of all collection names
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    @Deprecated
    List<String> getCollectionNames() throws SQLException;
    
    
    List<String> getProviderGroupNames() throws SQLException;

    /**
     * delete any urls that have the harvestDate column older than the given one.
     *
     * @param date links that have older harvestDate column than this will be deleted
     * @return number of affected rows
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    int deleteOldLinks(Long date) throws SQLException;
    
    /**
     * delete any urls that have the harvestDate column older than the given one.
     *
     * @param date links that have older harvestDate column than this will be deleted
     * @param collection name of the collection to fulfill the delete on
     * @return number of affected rows
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    int deleteOldLinks(Long date, String collection) throws SQLException;

    /**
     * Updates the harvestDate for the given links. This way the report generation date is persisted and
     * if there are old links in the database which are not part of the current report generation (not in the clarin records anymore)
     * they can be deleted with the deleteOldLinks() method.
     * @param linkId identifier of the link
     * @param nextFetchDate next (earliest) date the link is fetched for link checking
     * @return
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Boolean updateNextFetchDate(Long linkId, Timestamp nextFetchDate) throws SQLException;

}
