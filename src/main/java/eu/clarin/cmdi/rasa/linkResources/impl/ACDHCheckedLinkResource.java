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

package eu.clarin.cmdi.rasa.linkResources.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import eu.clarin.cmdi.rasa.helpers.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.helpers.impl.ACDHCheckedLinkFilter;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.links.CheckedLink;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;

public class ACDHCheckedLinkResource implements CheckedLinkResource {

    private MongoCollection<Document> linksChecked;
    private MongoCollection<Document> linksCheckedHistory;

    public ACDHCheckedLinkResource(MongoCollection<Document> linksChecked, MongoCollection<Document> linksCheckedHistory) {
        this.linksChecked = linksChecked;
        this.linksCheckedHistory = linksCheckedHistory;
    }

    @Override
    public CheckedLink get(URI uri) {
        Document doc = linksChecked.find(eq("url", uri.toString())).first();
        return new CheckedLink(doc);
    }

    @Override
    public Map<URI, CheckedLink> get(Collection<URI> uriCollection, Optional<CheckedLinkFilter> filter) {
        Map<URI, CheckedLink> uriMap = new HashMap<>();

        for (URI uri : uriCollection) {
            Document doc = linksChecked.find(eq("url", uri.toString())).first();

            if (doc != null) {
                CheckedLink checkedLink = new CheckedLink(doc);

                if (filter.isPresent()) {
                    ACDHCheckedLinkFilter acdhCheckedLinkFilter = (ACDHCheckedLinkFilter) filter.get();

                    if (acdhCheckedLinkFilter.matches(checkedLink)) {
                        uriMap.put(uri, checkedLink);
                    }

                } else {
                    uriMap.put(uri, checkedLink);
                }
            }
        }

        return uriMap;
    }

    @Override
    public Stream<CheckedLink> getHistory(URI uri, Order order, Optional<CheckedLinkFilter> filter) {
        List<CheckedLink> checkedLinks = new ArrayList<>();

        Bson sort;

        sort = order.equals(Order.ASC) ? Sorts.ascending("timestamp") : Sorts.descending("timestamp");

        MongoCursor<Document> cursor;

        if (filter.isPresent()) {
            Bson mongoFilter = ((ACDHCheckedLinkFilter) filter.get()).getMongoFilter();
            cursor = linksCheckedHistory.find(Filters.and(eq("url", uri.toString()), mongoFilter)).sort(sort).iterator();
        } else {
            cursor = linksCheckedHistory.find(eq("url", uri.toString())).sort(sort).iterator();
        }

        while (cursor.hasNext()) {
            checkedLinks.add(new CheckedLink(cursor.next()));
        }

        return checkedLinks.stream();
    }
}
