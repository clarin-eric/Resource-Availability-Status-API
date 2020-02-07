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

import eu.clarin.cmdi.rasa.DAO.CheckedLink;
import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.filters.impl.ACDHCheckedLinkFilter;
import eu.clarin.cmdi.rasa.linkResources.CheckedLinkResource;
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

        String urlQuery = "SELECT * FROM status WHERE url=?";
        PreparedStatement statement = con.prepareStatement(urlQuery);
        statement.setString(1, url);

        ResultSet rs = statement.executeQuery();

        Record record = DSL.using(con).fetchOne(rs);
        statement.close();

        //only one element
        return record == null ? null : new CheckedLink(record);

    }

    @Override
    public CheckedLink get(String url, String collection) throws SQLException {

        String urlCollectionQuery = "SELECT * FROM status WHERE url=? AND collection=?";
        PreparedStatement statement = con.prepareStatement(urlCollectionQuery);
        statement.setString(1, url);
        statement.setString(2, collection);

        ResultSet rs = statement.executeQuery();

        Record record = DSL.using(con).fetchOne(rs);
        statement.close();

        //only one element
        return record == null ? null : new CheckedLink(record);

    }

    @Override
    public Stream<CheckedLink> get(Optional<CheckedLinkFilter> filter) throws SQLException {

        String defaultQuery = "SELECT * FROM status";
        PreparedStatement statement = getPreparedStatement(defaultQuery, filter, null);
        ResultSet rs = statement.executeQuery();

        Stream<Record> recordStream = DSL.using(con).fetchStream(rs);
        recordStream.onClose(() -> {
            try {
                rs.close();
                statement.close();
            } catch (SQLException e) {
                _logger.error("Can't close prepared statement or resultset.");
            }
        });

        return recordStream.map(CheckedLink::new);

    }

    /**
     * This method is used to be able to have the prepared statement creation in one line, so that it fits well in to a try-with-resources block
     * Then the caller methods don't need to concern themselves with closing the statement.
     * @param defaultQuery
     * @param filter
     * @param inList
     * @return
     * @throws SQLException
     */
    private PreparedStatement getPreparedStatement(String defaultQuery, Optional<CheckedLinkFilter> filter, String inList) throws SQLException {
        PreparedStatement statement;
        if (!filter.isPresent()) {
            statement = con.prepareStatement(defaultQuery);
        } else {
            if (inList != null) {
                statement = filter.get().getStatement(con, inList);
            } else {
                statement = filter.get().getStatement(con);
            }
        }
        return statement;
    }

    //call this method in a try with resources so that the underlying resources are closed after use
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

        return get(Optional.of(filter));
    }

    @Override
    public Map<String, CheckedLink> get(Collection<String> urls, Optional<CheckedLinkFilter> filter) throws SQLException {

        //if urlCollection is given, this is how all these from the collection are returned:
        //example query: select * from status where url in ('www.google.com','www.facebook.com');
        StringBuilder sb = new StringBuilder();
        sb.append(" url IN (");
        String comma = "";
        for (String url : urls) {
            sb.append(comma);
            comma = ",";
            sb.append("'").append(url).append("'");
        }
        sb.append(")");

        String defaultQuery = "SELECT * FROM status WHERE" + sb.toString();

        try (PreparedStatement statement = getPreparedStatement(defaultQuery, filter, sb.toString())) {
            try (ResultSet rs = statement.executeQuery()) {
                try (Stream<Record> recordStream = DSL.using(con).fetchStream(rs)) {
                    return recordStream.map(CheckedLink::new).collect(Collectors.toMap(CheckedLink::getUrl, Function.identity()));
                }
            }
        }

    }


    @Override
    public Boolean save(CheckedLink checkedLink) throws SQLException {

        //get old checked link
        CheckedLink oldCheckedLink = get(checkedLink.getUrl());

        if (oldCheckedLink != null) {
            //save to history
            saveToHistory(oldCheckedLink);

            //delete it
            delete(checkedLink.getUrl());
        }

        //save new one
        return insertCheckedLink(checkedLink, "status");
    }

    private PreparedStatement getInsertPreparedStatement(String tableName) throws SQLException {
        PreparedStatement preparedStatement;
        if (tableName.equals("status")) {
            String insertStatusQuery = "INSERT INTO status(url,statusCode,method,contentType,byteSize,duration,timestamp,redirectCount,collection,record,expectedMimeType,message) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = con.prepareStatement(insertStatusQuery);
        } else {//history
            String insertHistoryQuery = "INSERT INTO history(url,statusCode,method,contentType,byteSize,duration,timestamp,redirectCount,collection,record,expectedMimeType,message) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = con.prepareStatement(insertHistoryQuery);
        }
        return preparedStatement;
    }

    private Boolean insertCheckedLink(CheckedLink checkedLink, String tableName) {
        try (PreparedStatement preparedStatement = getInsertPreparedStatement(tableName)) {

            preparedStatement.setString(1, checkedLink.getUrl());
            preparedStatement.setInt(2, checkedLink.getStatus());
            preparedStatement.setString(3, checkedLink.getMethod());
            preparedStatement.setString(4, checkedLink.getContentType());
            preparedStatement.setInt(5, checkedLink.getByteSize());
            preparedStatement.setInt(6, checkedLink.getDuration());
            preparedStatement.setTimestamp(7, checkedLink.getTimestamp());
            preparedStatement.setInt(8, checkedLink.getRedirectCount());
            preparedStatement.setString(9, checkedLink.getCollection());
            preparedStatement.setString(10, checkedLink.getRecord());
            preparedStatement.setString(11, checkedLink.getExpectedMimeType());
            preparedStatement.setString(12, checkedLink.getMessage());

            //affected rows
            int row = preparedStatement.executeUpdate();

            return row == 1;

        } catch (SQLException e) {
            _logger.error("SQL Exception while saving " + checkedLink.getUrl() + " into " + tableName + ":" + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean saveToHistory(CheckedLink checkedLink) throws SQLException {
        return insertCheckedLink(checkedLink, "history");
    }

    @Override
    public Boolean delete(String url) throws SQLException {
        String deleteURLQuery = "DELETE FROM status WHERE url=?";
        try (PreparedStatement preparedStatement = con.prepareStatement(deleteURLQuery)) {

            preparedStatement.setString(1, url);

            //affected rows
            int row = preparedStatement.executeUpdate();

            return row == 1;
        }
    }

    @Override
    public List<CheckedLink> getHistory(String url, Order order) throws SQLException {

        //not requested much, so no need to optimize
        String query = "SELECT * FROM history WHERE url=? ORDER BY timestamp " + order.name();
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, url);

            try (ResultSet rs = preparedStatement.executeQuery()) {

                try (Stream<Record> recordStream = DSL.using(con).fetchStream(rs)) {
                    return recordStream.map(CheckedLink::new).collect(Collectors.toList());
                }
            }
        }

    }
}
