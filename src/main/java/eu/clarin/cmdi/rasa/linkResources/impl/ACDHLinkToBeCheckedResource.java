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
import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;
import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ACDHLinkToBeCheckedResource implements LinkToBeCheckedResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHLinkToBeCheckedResource.class);

    private Connection con;

    private final String insertQuery = "INSERT IGNORE INTO urls(url,record,collection,expectedMimeType) VALUES (?,?,?,?)";
    private final String deleteURLQuery = "DELETE FROM urls WHERE url=?";

    public ACDHLinkToBeCheckedResource(Connection con) {
        this.con = con;
    }

    @Override
    public Optional<LinkToBeChecked> get(String url) throws SQLException {

        String urlQuery = "SELECT * FROM urls WHERE url=?";
        try (PreparedStatement statement = con.prepareStatement(urlQuery)) {

            statement.setString(1, url);

            try (ResultSet rs = statement.executeQuery()) {

                final Record record = DSL.using(con).fetchOne(rs);
                return Optional.ofNullable(record).map(LinkToBeChecked::new);
            }
        }
    }

    //call this method in a try with resources so that the underlying resources are closed after use
    @Override
    public Stream<LinkToBeChecked> get(Optional<LinkToBeCheckedFilter> filter) throws SQLException {
        String defaultQuery = "SELECT * FROM urls";
        PreparedStatement statement = getPreparedStatement(defaultQuery, filter);
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

        return recordStream.map(LinkToBeChecked::new);
    }

    private PreparedStatement getPreparedStatement(String defaultQuery, Optional<LinkToBeCheckedFilter> filter) throws SQLException {
        PreparedStatement statement;
        if (filter.isEmpty()) {
            statement = con.prepareStatement(defaultQuery);
        } else {
            statement = filter.get().getStatement(con);
        }
        return statement;
    }

    @Override
    public List<LinkToBeChecked> getList(Optional<LinkToBeCheckedFilter> filter) throws SQLException {
        try (Stream<LinkToBeChecked> linkToBeCheckedStream = get(filter)) {
            return linkToBeCheckedStream.collect(Collectors.toList());
        }
    }

    @Override
    public Boolean save(LinkToBeChecked linkToBeChecked) throws SQLException {

        try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, linkToBeChecked.getUrl());
            preparedStatement.setString(2, linkToBeChecked.getRecord());
            preparedStatement.setString(3, linkToBeChecked.getCollection());
            preparedStatement.setString(4, linkToBeChecked.getExpectedMimeType());

            //affected rows
            int row = preparedStatement.executeUpdate();

            return row == 1;
        }
    }

    @Override
    public Boolean save(List<LinkToBeChecked> linksToBeChecked) throws SQLException {

        try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {

            for (LinkToBeChecked linkToBeChecked : linksToBeChecked) {
                preparedStatement.setString(1, linkToBeChecked.getUrl());
                preparedStatement.setString(2, linkToBeChecked.getRecord());
                preparedStatement.setString(3, linkToBeChecked.getCollection());
                preparedStatement.setString(4, linkToBeChecked.getExpectedMimeType());
                preparedStatement.addBatch();
            }

            //affected rows
            int[] row = preparedStatement.executeBatch();

            return row.length >= 1;
        }
    }

    @Override
    public Boolean delete(String url) throws SQLException {

        try (PreparedStatement preparedStatement = con.prepareStatement(deleteURLQuery)) {

            preparedStatement.setString(1, url);

            //affected rows
            int row = preparedStatement.executeUpdate();

            return row == 1;
        }
    }

    @Override
    public Boolean delete(List<String> urls) throws SQLException {

        try (PreparedStatement preparedStatement = con.prepareStatement(deleteURLQuery)) {

            for(String url:urls){
                preparedStatement.setString(1, url);
                preparedStatement.addBatch();
            }


            //affected rows
            int[] row = preparedStatement.executeBatch();

            return row.length >= 1;
        }
    }

    @Override
    public List<String> getCollectionNames() throws SQLException {

        List<String> collectionNames = new ArrayList<>();

        String collectionQuery = "SELECT DISTINCT collection from urls";
        try (PreparedStatement statement = con.prepareStatement(collectionQuery)) {
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    collectionNames.add(rs.getString("collection"));
                }

                return collectionNames;
            }
        }
    }


}
