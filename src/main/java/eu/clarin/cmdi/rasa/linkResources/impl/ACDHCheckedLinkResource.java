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
import eu.clarin.cmdi.rasa.filters.impl.ACDHCheckedLinkFilter;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
import eu.clarin.cmdi.rasa.DAO.CheckedLink;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    public Stream<CheckedLink> get(Optional<CheckedLinkFilter> filterOptional, int start, int end) throws SQLException {
        if (start > end) {
            throw new IllegalArgumentException("start can't be greater than end.");
        }

        if (start <= 0 && end <= 0) {
            throw new IllegalArgumentException("start and end can't less than or equal to 0 at the same time.");
        }

        CheckedLinkFilter filter;
        if (filterOptional.isPresent()) {
            filter = filterOptional.get();
            filter.setStart(start);
            filter.setEnd(end);
        } else {
            filter = new ACDHCheckedLinkFilter(start, end);
        }

        PreparedStatement statement = filter.getStatement(con);
        ResultSet rs = statement.executeQuery();

        return DSL.using(con).fetchStream(rs).map(CheckedLink::new);
    }

    @Override
    public Map<String, CheckedLink> get(Collection<String> urlCollection, Optional<CheckedLinkFilter> filter) throws SQLException {
        //todo maybe find a better solution for this in list
        String inList = " url IN (";
        for (String url : urlCollection) {
            inList += "'" + url + "',";
        }
        inList = inList.substring(0, inList.length() - 1);
        inList += ")";

        String defaultQuery = "SELECT * FROM statusView WHERE" + inList;
        PreparedStatement statement;
        if (!filter.isPresent()) {
            statement = con.prepareStatement(defaultQuery);
        } else {
            statement = filter.get().getStatement(con, inList);
        }

        ResultSet rs = statement.executeQuery();

        return DSL.using(con).fetchStream(rs).map(CheckedLink::new).collect(Collectors.toMap(CheckedLink::getUrl, Function.identity()));
    }


    @Override
    public Boolean save(CheckedLink checkedLink) throws SQLException {
        String insertQuery = "INSERT IGNORE INTO status(url,statusCode,method,contentType,byteSize,duration,timestamp,redirectCount) VALUES (?,?,?,?,?,?,?,?)";

        PreparedStatement preparedStatement = con.prepareStatement(insertQuery);
        preparedStatement.setString(1, checkedLink.getUrl());
        preparedStatement.setInt(2, checkedLink.getStatus());
        preparedStatement.setString(3, checkedLink.getMethod());
        preparedStatement.setString(4, checkedLink.getContentType());
        preparedStatement.setInt(5, checkedLink.getByteSize());
        preparedStatement.setInt(6, checkedLink.getDuration());
        preparedStatement.setTimestamp(7, checkedLink.getTimestamp());
        preparedStatement.setInt(8, checkedLink.getRedirectCount());

        //affected rows
        int row = preparedStatement.executeUpdate();

        return row == 1;
    }

    @Override
    public Boolean delete(String url) throws SQLException {
        String deleteQuery = "DELETE FROM status WHERE url=?";
        PreparedStatement preparedStatement = con.prepareStatement(deleteQuery);
        preparedStatement.setString(1, url);

        //affected rows
        int row = preparedStatement.executeUpdate();

        return row == 1;
    }

    //todo later history

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
