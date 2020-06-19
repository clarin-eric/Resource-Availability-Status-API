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

import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;
import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ACDHLinkToBeCheckedResource implements LinkToBeCheckedResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHLinkToBeCheckedResource.class);

    private final String insertQuery = "INSERT IGNORE INTO urls(url,record,collection,expectedMimeType,harvestDate) VALUES (?,?,?,?,?)";
    private final String deleteURLQuery = "DELETE FROM urls WHERE url=?";

    private final ConnectionProvider connectionProvider;

    public ACDHLinkToBeCheckedResource(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<LinkToBeChecked> get(String url) throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
            String urlQuery = "SELECT * FROM urls WHERE url=?";
            try (PreparedStatement statement = con.prepareStatement(urlQuery)) {

                statement.setString(1, url);

                try (ResultSet rs = statement.executeQuery()) {

                    final Record record = DSL.using(con).fetchOne(rs);
                    return Optional.ofNullable(record).map(LinkToBeChecked::new);
                }
            }
        }
    }

    //call this method in a try with resources so that the underlying resources are closed after use
    @Override
    public Stream<LinkToBeChecked> get(Optional<LinkToBeCheckedFilter> filter) throws SQLException {
        final Connection con = connectionProvider.getConnection();
        final String defaultQuery = "SELECT * FROM urls";
        final PreparedStatement statement = getPreparedStatement(con, defaultQuery, filter);
        final ResultSet rs = statement.executeQuery();

        Stream<Record> recordStream = DSL.using(con).fetchStream(rs);
        recordStream.onClose(() -> {
            try {
                rs.close();
                statement.close();
                con.close();
            } catch (SQLException e) {
                _logger.error("Can't close prepared statement or resultset or connection.");
            }
        });

        return recordStream.map(LinkToBeChecked::new);
    }


    private PreparedStatement getPreparedStatement(Connection con, String defaultQuery, Optional<LinkToBeCheckedFilter> filter) throws SQLException {
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
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, linkToBeChecked.getUrl());
                preparedStatement.setString(2, linkToBeChecked.getRecord());
                preparedStatement.setString(3, linkToBeChecked.getCollection());
                preparedStatement.setString(4, linkToBeChecked.getExpectedMimeType());
                preparedStatement.setLong(5, linkToBeChecked.getHarvestDate());

                //affected rows
                int row = preparedStatement.executeUpdate();

                return row == 1;
            }
        }
    }

    @Override
    public Boolean save(List<LinkToBeChecked> linksToBeChecked) throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {

                for (LinkToBeChecked linkToBeChecked : linksToBeChecked) {
                    preparedStatement.setString(1, linkToBeChecked.getUrl());
                    preparedStatement.setString(2, linkToBeChecked.getRecord());
                    preparedStatement.setString(3, linkToBeChecked.getCollection());
                    preparedStatement.setString(4, linkToBeChecked.getExpectedMimeType());
                    preparedStatement.setLong(5, linkToBeChecked.getHarvestDate());
                    preparedStatement.addBatch();
                }

                //affected rows
                int[] row = preparedStatement.executeBatch();

                return row.length >= 1;
            }
        }
    }

    @Override
    public Boolean delete(String url) throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(deleteURLQuery)) {

                preparedStatement.setString(1, url);

                //affected rows
                int row = preparedStatement.executeUpdate();

                return row == 1;
            }
        }
    }

    @Override
    public Boolean delete(List<String> urls) throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(deleteURLQuery)) {

                for (String url : urls) {
                    preparedStatement.setString(1, url);
                    preparedStatement.addBatch();
                }


                //affected rows
                int[] row = preparedStatement.executeBatch();

                return row.length >= 1;
            }
        }
    }

    @Override
    public List<String> getCollectionNames() throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
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

    @Override
    public int deleteOldLinks(Long date) throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {

            String deleteQuery = "DELETE FROM urls where harvestDate < ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(deleteQuery)) {
                preparedStatement.setLong(1, date);

                System.out.println("should be less than this: "+date);
                //affected rows
                return preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public Boolean updateDate(List<String> linksToBeUpdated, Long date) throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
            String updateDateQuery = "UPDATE urls SET harvestDate = ? WHERE url = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(updateDateQuery)) {

                for (String url : linksToBeUpdated) {
                    preparedStatement.setLong(1, date);
                    preparedStatement.setString(2, url);
                    preparedStatement.addBatch();
                }

                //affected rows
                int[] row = preparedStatement.executeBatch();

                return row.length >= 1;
            }
        }
    }


}
