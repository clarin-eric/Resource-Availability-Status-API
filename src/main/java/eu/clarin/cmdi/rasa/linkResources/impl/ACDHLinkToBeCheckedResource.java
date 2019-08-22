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

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;
import eu.clarin.cmdi.rasa.links.LinkToBeChecked;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ACDHLinkToBeCheckedResource implements LinkToBeCheckedResource {

    private MongoCollection<Document> linksToBeChecked;

    public ACDHLinkToBeCheckedResource(MongoCollection<Document> linksToBeChecked) {
        this.linksToBeChecked = linksToBeChecked;
    }

    @Override
    public Stream<LinkToBeChecked> get(Optional<LinkToBeCheckedFilter> filter) {
        List<LinkToBeChecked> result = new ArrayList<>();

        MongoCursor<Document> cursor;

        if (filter.isPresent()) {
            Bson mongoFilter = filter.get().getMongoFilter();
            cursor = linksToBeChecked.find(mongoFilter).noCursorTimeout(true).iterator();
        } else {
            cursor = linksToBeChecked.find().noCursorTimeout(true).iterator();
        }

        while (cursor.hasNext()) {
            result.add(new LinkToBeChecked(cursor.next()));
        }

        cursor.close();

        return result.stream();
    }

    @Override
    public List<LinkToBeChecked> getList(Optional<LinkToBeCheckedFilter> filter) {
        List<LinkToBeChecked> result = new ArrayList<>();

        MongoCursor<Document> cursor;

        if (filter.isPresent()) {
            Bson mongoFilter = filter.get().getMongoFilter();
            cursor = linksToBeChecked.find(mongoFilter).noCursorTimeout(true).iterator();
        } else {
            cursor = linksToBeChecked.find().noCursorTimeout(true).iterator();
        }

        while (cursor.hasNext()) {
            result.add(new LinkToBeChecked(cursor.next()));
        }

        cursor.close();

        return result;
    }

    @Override
    public Boolean save(LinkToBeChecked linkToBeChecked){
        try {
            linksToBeChecked.insertOne(linkToBeChecked.getMongoDocument());
            return true;
        } catch (MongoException e) {
            //duplicate key error
            //url is already in the database, do nothing
            return false;
        }

    }
}
