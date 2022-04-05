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
import java.util.List;
import java.util.stream.Stream;

public interface LinkToBeCheckedResource {

   /**
    * @param filter implementation of LinkToBeCheckedFilter
    * @return stream of LinkToBeChecked, fulfilling the filter criteria
    * @throws SQLException
    */
   Stream<LinkToBeChecked> get(LinkToBeCheckedFilter filter) throws SQLException;

   int getCount(LinkToBeCheckedFilter filter) throws SQLException;

   /**
    * Save a link to be checked into urls table, if it already exists in the
    * collection, it fails but is ignored
    *
    * @param linkToBeChecked Element to be persisted
    * @return If the operation was successful(not if the linkToBeChecked was
    *         persisted)
    * @throws SQLException occurs if there was an error during statement
    *                      preparation
    */
   Boolean save(LinkToBeChecked linkToBeChecked) throws SQLException;

   /**
    * Batch insert links to be checked into urls, only not existing urls are
    * persisted.
    *
    * @param linksToBeChecked List of elements to be persisted
    * @return If at least one url from the list was persisted
    * @throws SQLException occurs if there was an error during statement
    *                      preparation or execution
    */
   Boolean save(List<LinkToBeChecked> linksToBeChecked) throws SQLException;


   /**
    * Delete an element from urls table
    *
    * @param url Url to be deleted
    * @return If the url was deleted
    * @throws SQLException occurs if there was an error during statement
    *                      preparation or execution
    */
   Boolean delete(String url) throws SQLException;

   /**
    * Delete multiple elements from the urls table
    *
    * @param urls List of urls to be deleted
    * @return If at least one url from the list was deleted
    * @throws SQLException occurs if there was an error during statement
    *                      preparation or execution
    */
   Boolean delete(List<String> urls) throws SQLException;


   List<String> getProviderGroupNames() throws SQLException;

   /**
    * delete any urls that have the harvestDate column older than the given one.
    *
    * @param date links that have older harvestDate column than this will be
    *             deleted
    * @return number of affected rows
    * @throws SQLException occurs if there was an error during statement
    *                      preparation or execution
    */
   int deleteOldLinks(Long date) throws SQLException;

   /**
    * delete any urls that have the harvestDate column older than the given one.
    *
    * @param date       links that have older harvestDate column than this will be
    *                   deleted
    * @param collection name of the collection to fulfill the delete on
    * @return number of affected rows
    * @throws SQLException occurs if there was an error during statement
    *                      preparation or execution
    */
   int deleteOldLinks(Long date, String collection) throws SQLException;

   LinkToBeCheckedFilter getLinkToBeCheckedFilter();
   
   /**
    * @return Stream of LinkToBeChecked
    * @throws SQLException
    */
   Stream<LinkToBeChecked> getNextLinksToCheck() throws SQLException;
   
   Boolean updateURLs() throws SQLException;
}
