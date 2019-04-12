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
import eu.clarin.cmdi.rasa.helpers.impl.ACDHCheckedLinkFilter;
import eu.clarin.cmdi.rasa.links.CheckedLink;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface CheckedLinkResource {

    enum Order {
        ASC,
        DESC
    }

    /* retrieve for single URI (will be normalised before retrieval) */
    public CheckedLink get(URI uri);

    /* batch retrieval with URI as key (to be normalised before retrieval)  with optional filtering
       get(“http://clarin.eu”, new FilterImpl(404, 2019-01-01, 2019-02-01)
    */
    public Map<URI, CheckedLink> get(Collection<URI> uri, Optional<ACDHCheckedLinkFilter> filter);

    /* retrieval of history for one URI  as key with optional filtering */
    public Stream<CheckedLink> getHistory(URI uri, Order order, Optional<ACDHCheckedLinkFilter> filter);

}
