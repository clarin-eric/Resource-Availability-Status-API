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
import eu.clarin.cmdi.rasa.DAO.Statistics.CategoryStatistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.StatusStatistics;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface CheckedLinkResource {
	Stream<CheckedLink> get(CheckedLinkFilter filter) throws SQLException;
    
    /**
     * Save a checked link into status table, set its nextCheckDate on urls table, move old checked link in status table into history if it exists
     * @param checkedLink checked link to be persisted
     * @return If the operation was successful
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Boolean save(CheckedLink checkedLink) throws SQLException;
    
    int getCount(CheckedLinkFilter filter) throws SQLException;
    
    Statistics getStatistics(CheckedLinkFilter filter) throws SQLException;
    
    Stream<CategoryStatistics> getCategoryStatistics(CheckedLinkFilter filter) throws SQLException;
    
    Stream<StatusStatistics> getStatusStatistics(CheckedLinkFilter filter) throws SQLException;

    /**
     * Batch retrieval with url as key with optional filtering.
     * @param urls A list of urls to be retrieved
     * @param filter Optional filter to be applied on the list of urls
     * @return A map of url to CheckedLink objects
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    @Deprecated
    Map<String, CheckedLink> get(Collection<String> urls, Optional<CheckedLinkFilter> filter) throws SQLException;
    
    Map<String, CheckedLink> getMap(CheckedLinkFilter filter) throws SQLException;
    
    CheckedLinkFilter getCheckedLinkFilter();

}
