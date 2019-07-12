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

import eu.clarin.cmdi.rasa.helpers.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.helpers.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.links.CheckedLink;
import eu.clarin.cmdi.rasa.links.LinkToBeChecked;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface CheckedLinkResource {

    enum Order {
        ASC,
        DESC
    }

    /* retrieve for single url */
    CheckedLink get(String url);

    /* get all urls that match a filter */
    Stream<CheckedLink> get(Optional<CheckedLinkFilter> filter);

    /* batch retrieval with url as key with optional filtering
       get(“http://clarin.eu”, new FilterImpl(404, 2019-01-01, 2019-02-01)
    */
    Map<String, CheckedLink> get(Collection<String> url, Optional<CheckedLinkFilter> filter);

    /* retrieval of history for one URl as key with optional filtering, Order is timestamp based. */
    Stream<CheckedLink> getHistory(String url, Order order, Optional<CheckedLinkFilter> filter);

    /* retrieval of the names of all collections that are in linksChecked */
    List<String> getCollectionNames();

    /* save a checked link into linkschecked, remove it from linksToBeChecked, move old result into history if exists */
    Boolean save(CheckedLink checkedLink);

}
