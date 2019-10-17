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

//import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;

import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.links.CheckedLink;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


public class ACDHCheckedLinkResource implements CheckedLinkResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHCheckedLinkResource.class);

    private Connection con;

    public ACDHCheckedLinkResource(Connection con) {
        this.con = con;
    }

    @Override
    public CheckedLink get(String url) throws SQLException {
        String query = "SELECT * FROM statusView WHERE url=?";

        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, url);

        ResultSet rs = statement.executeQuery();

        Record record = DSL.using(con).fetchOne(rs);
        //only one element
        return record == null ? null : new CheckedLink(record);
    }

    @Override
    public CheckedLink get(String url, String collection) throws SQLException {
        String query = "SELECT * FROM statusView WHERE url=? AND collection=?";

        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, url);
        statement.setString(2, collection);

        ResultSet rs = statement.executeQuery();

        Record record = DSL.using(con).fetchOne(rs);
        //only one element
        return record == null ? null : new CheckedLink(record);
    }

    @Override
    public Stream<CheckedLink> get(Optional<CheckedLinkFilter> filter) throws SQLException {

        String defaultQuery = "SELECT * FROM statusView";

        PreparedStatement statement;
        if (!filter.isPresent()) {
            statement = con.prepareStatement(defaultQuery);
        } else {
            statement = filter.get().getStatement(con);
        }

        ResultSet rs = statement.executeQuery();

        return DSL.using(con).fetchStream(rs).map(CheckedLink::new);
    }

    @Override
    public Stream<CheckedLink> get(Optional<CheckedLinkFilter> filter, int start, int end) {
        //todo
//        final Iterable<Document> documents;
//
//        if (filter.isPresent()) {
//            Bson mongoFilter = filter.get().getMongoFilter();
//            documents = linksChecked.find(mongoFilter).skip(start).limit(end).noCursorTimeout(true);
//        } else {
//            documents = linksChecked.find().skip(start).limit(end).noCursorTimeout(true);
//        }
//
//        return StreamSupport.stream(documents.spliterator(), false)
//                .map(CheckedLink::new);
        return null;
    }

    @Override
    public Map<String, CheckedLink> get(Collection<String> urlCollection, Optional<CheckedLinkFilter> filter) {
        //todo
//        final FindIterable<Document> urls;
//        if (filter.isPresent()) {
//            final Bson mongoFilter = filter.get().getMongoFilter();
//            urls = linksChecked.find(and(in("url", urlCollection), mongoFilter)).noCursorTimeout(true);
//        } else {
//            urls = linksChecked.find(in("url", urlCollection));
//        }
//
//        return StreamSupport.stream(urls.spliterator(), false)
//                .collect(Collectors.toMap(doc -> doc.getString("url"), CheckedLink::new));
        return null;
    }

    //
//    @Override
//    public Stream<CheckedLink> getHistory(String url, Order order, Optional<CheckedLinkFilter> filter) {
//        final Bson sort = order.equals(Order.ASC) ? Sorts.ascending("timestamp") : Sorts.descending("timestamp");
//
//        final Iterable<Document> documents;
//
//        if (filter.isPresent()) {
//            Bson mongoFilter = filter.get().getMongoFilter();
//            documents = linksCheckedHistory.find(Filters.and(eq("url", url), mongoFilter)).noCursorTimeout(true).sort(sort);
//        } else {
//            documents = linksCheckedHistory.find(eq("url", url)).noCursorTimeout(true).sort(sort);
//        }
//
//        return StreamSupport.stream(documents.spliterator(), false)
//                .map(CheckedLink::new);
//    }
//
    @Override
    public List<String> getCollectionNames() {
//todo
        //        Iterable<String> collections = linksChecked.distinct("collection", String.class);
//
//        return StreamSupport.stream(collections.spliterator(), false)
//                .collect(Collectors.toList());
        return null;
    }


    @Override
    public Boolean save(CheckedLink checkedLink) {
//todo

//        //separate mongo actions so that one of them doesn't disturb the other
//        //save it to the history
//        Bson filter = Filters.eq("url", checkedLink.getUrl());
//        try {
//            Document oldElementDoc = linksChecked.find(filter).first();
//            if (oldElementDoc != null) {
//                oldElementDoc.remove("_id");//remove id so that it generates a new one.
//                linksCheckedHistory.insertOne(oldElementDoc);
//            }
//
//        } catch (MongoException e) {
//            _logger.error("There was an error with the url: " + checkedLink.getUrl() + " .It is being skipped. Error message: " + e.getMessage());
//            //do nothing so that the whole thread doesnt die because of one url, just skip it
//            return false;
//        }
//
//        try {
//            //replace if the url is in linksChecked already
//            //if not add new
//            FindOneAndReplaceOptions findOneAndReplaceOptions = new FindOneAndReplaceOptions();
//            linksChecked.findOneAndReplace(filter, checkedLink.getMongoDocument(), findOneAndReplaceOptions.upsert(true));
//
//        } catch (MongoException e) {
//            _logger.error("There was an error with the url: " + checkedLink.getUrl() + " .It is being skipped. Error message: " + e.getMessage());
//            //do nothing so that the whole thread doesnt die because of one url, just skip it
//            return false;
//        }
//
//        try {
//
//            //delete from linksToBeChecked(whether successful or there was an error, ist wuascht)
//            linksToBeChecked.deleteOne(filter);
//
//            return true;
//        } catch (MongoException e) {
//            _logger.error("There was an error with the url: " + checkedLink.getUrl() + " .It is being skipped. Error message: " + e.getMessage());
//            //do nothing so that the whole thread doesnt die because of one url, just skip it
//            return false;
//        }
        return false;

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
