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
import java.util.Optional;
import java.util.stream.Stream;

public interface LinkToBeCheckedResource {

    /* get all urls that match a filter */
    Stream<LinkToBeChecked> get(Optional<LinkToBeCheckedFilter> filter) throws SQLException;

    List<LinkToBeChecked> getList(Optional<LinkToBeCheckedFilter> filter) throws SQLException;

    /* save a link to be checked into urls, if it already exists in the collection, it fails but is ignored */
    Boolean save(LinkToBeChecked linkToBeChecked) throws SQLException;

    /* Delete a url from urls */
    Boolean delete(String url) throws SQLException;

    /* retrieval of the names of all collections that are in urls */
    List<String> getCollectionNames() throws SQLException;
}
