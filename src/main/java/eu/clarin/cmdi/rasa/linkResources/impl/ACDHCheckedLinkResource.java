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
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Sorts;
import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.links.CheckedLink;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.*;

public class ACDHCheckedLinkResource implements CheckedLinkResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHCheckedLinkResource.class);

    private MongoCollection<Document> linksChecked;
    private MongoCollection<Document> linksCheckedHistory;
    private MongoCollection<Document> linksToBeChecked;

    public ACDHCheckedLinkResource(MongoCollection<Document> linksChecked, MongoCollection<Document> linksCheckedHistory, MongoCollection<Document> linksToBeChecked) {
        this.linksChecked = linksChecked;
        this.linksCheckedHistory = linksCheckedHistory;
        this.linksToBeChecked = linksToBeChecked;
    }

    @Override
    public CheckedLink get(String url) {
        Document doc = linksChecked.find(eq("url", url)).first();
        return doc == null ? null : new CheckedLink(doc);
    }

    @Override
    public CheckedLink get(String url, String collection) {
        Document doc = linksChecked.find(Filters.and(eq("collection", collection), eq("url", url))).first();
        return doc == null ? null : new CheckedLink(doc);
    }

    @Override
    public Stream<CheckedLink> get(Optional<CheckedLinkFilter> filter) {
        List<CheckedLink> result = new ArrayList<>();

        MongoCursor<Document> cursor;

        if (filter.isPresent()) {
            Bson mongoFilter = filter.get().getMongoFilter();
            cursor = linksChecked.find(mongoFilter).noCursorTimeout(true).iterator();
        } else {
            cursor = linksChecked.find().noCursorTimeout(true).iterator();
        }

        while (cursor.hasNext()) {
            result.add(new CheckedLink(cursor.next()));
        }

        cursor.close();

        return result.stream();
    }

    @Override
    public Stream<CheckedLink> get(Optional<CheckedLinkFilter> filter, int start, int end) {
        List<CheckedLink> result = new ArrayList<>();

        MongoCursor<Document> cursor;

        if (filter.isPresent()) {
            Bson mongoFilter = filter.get().getMongoFilter();
            cursor = linksChecked.find(mongoFilter).skip(start).limit(end).noCursorTimeout(true).iterator();
        } else {
            cursor = linksChecked.find().skip(start).limit(end).noCursorTimeout(true).iterator();
        }

        while (cursor.hasNext()) {
            result.add(new CheckedLink(cursor.next()));
        }

        cursor.close();

        return result.stream();
    }

    @Override
    public Map<String, CheckedLink> get(Collection<String> urlCollection, Optional<CheckedLinkFilter> filter) {
        Map<String, CheckedLink> urlMap = new HashMap<>();

        if (filter.isPresent()) {
            Bson mongoFilter = filter.get().getMongoFilter();

            FindIterable<Document> urls = linksChecked.find(and(in("url", urlCollection), mongoFilter)).noCursorTimeout(true);
            for (Document doc : urls) {
                urlMap.put(doc.getString("url"), new CheckedLink(doc));
            }
        } else {
            FindIterable<Document> urls = linksChecked.find(in("url", urlCollection));
            for (Document doc : urls) {
                urlMap.put(doc.getString("url"), new CheckedLink(doc));
            }
        }

        return urlMap;
    }

    @Override
    public Stream<CheckedLink> getHistory(String url, Order order, Optional<CheckedLinkFilter> filter) {
        List<CheckedLink> checkedLinks = new ArrayList<>();

        Bson sort;

        sort = order.equals(Order.ASC) ? Sorts.ascending("timestamp") : Sorts.descending("timestamp");

        MongoCursor<Document> cursor;

        if (filter.isPresent()) {
            Bson mongoFilter = filter.get().getMongoFilter();
            cursor = linksCheckedHistory.find(Filters.and(eq("url", url), mongoFilter)).noCursorTimeout(true).sort(sort).iterator();
        } else {
            cursor = linksCheckedHistory.find(eq("url", url)).noCursorTimeout(true).sort(sort).iterator();
        }

        while (cursor.hasNext()) {
            checkedLinks.add(new CheckedLink(cursor.next()));
        }

        cursor.close();

        return checkedLinks.stream();
    }

    @Override
    public List<String> getCollectionNames() {
        MongoCursor<String> cursor = linksChecked.distinct("collection", String.class).iterator();

        List<String> collections = new ArrayList<>();
        while (cursor.hasNext()) {
            collections.add(cursor.next());
        }
        return collections;
    }

    @Override
    public Boolean save(CheckedLink checkedLink) {

        //separate mongo actions so that one of them doesn't disturb the other

        //save it to the history
        Bson filter = Filters.eq("url", checkedLink.getUrl());
        try {
            Document oldElementDoc = linksChecked.find(filter).first();
            if (oldElementDoc != null) {
                oldElementDoc.remove("_id");//remove id so that it generates a new one.
                linksCheckedHistory.insertOne(oldElementDoc);
            }

        } catch (MongoException e) {
            _logger.error("There was an error with the url: " + checkedLink.getUrl() + " .It is being skipped. Error message: " + e.getMessage());
            //do nothing so that the whole thread doesnt die because of one url, just skip it
            return false;
        }

        try {
            //replace if the url is in linksChecked already
            //if not add new
            FindOneAndReplaceOptions findOneAndReplaceOptions = new FindOneAndReplaceOptions();
            linksChecked.findOneAndReplace(filter, checkedLink.getMongoDocument(), findOneAndReplaceOptions.upsert(true));

        } catch (MongoException e) {
            _logger.error("There was an error with the url: " + checkedLink.getUrl() + " .It is being skipped. Error message: " + e.getMessage());
            //do nothing so that the whole thread doesnt die because of one url, just skip it
            return false;
        }

        try {

            //delete from linksToBeChecked(whether successful or there was an error, ist wuascht)
            linksToBeChecked.deleteOne(filter);

            return true;
        } catch (MongoException e) {
            _logger.error("There was an error with the url: " + checkedLink.getUrl() + " .It is being skipped. Error message: " + e.getMessage());
            //do nothing so that the whole thread doesnt die because of one url, just skip it
            return false;
        }


    }

//    @Override
//    public Boolean moveToHistory(CheckedLink checkedLink) {
//        String url = checkedLink.getUrl();
//        try {
//            linksCheckedHistory.insertOne(checkedLink.getMongoDocument());
//        } catch (MongoException e) {
//            //shouldnt happen, but if it does continue the loop
//            _logger.error("Error with the url: " + url + " while cleaning linkschecked (removing links from older runs). Exception message: " + e.getMessage());
//            return false;
//        }
//
//        try {
//            linksChecked.deleteOne(eq("url", url));
//        } catch (MongoException e) {
//            //shouldnt happen, but if it does continue the loop
//            _logger.error("Error with the url: " + url + " while cleaning linkschecked (removing links from older runs). Exception message: " + e.getMessage());
//            return false;
//        }
//        return true;
//    }
}
