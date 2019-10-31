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

import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;
import eu.clarin.cmdi.rasa.linkResources.LinkToBeCheckedResource;
import eu.clarin.cmdi.rasa.DAO.LinkToBeChecked;
import org.jooq.Record;
import org.jooq.impl.DSL;

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

    private Connection con;

    public ACDHLinkToBeCheckedResource(Connection con) {
        this.con = con;
    }

    @Override
    public LinkToBeChecked get(String url) throws SQLException {
        String query = "SELECT * FROM url WHERE url=?";

        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, url);

        ResultSet rs = statement.executeQuery();

        Record record = DSL.using(con).fetchOne(rs);

        //only one element
        return record == null ? null : new LinkToBeChecked(record);
    }

    @Override
    public Stream<LinkToBeChecked> get(Optional<LinkToBeCheckedFilter> filter) throws SQLException {

        String defaultQuery = "SELECT * FROM urls";

        PreparedStatement statement;
        if (!filter.isPresent()) {
            statement = con.prepareStatement(defaultQuery);
        } else {
            statement = filter.get().getStatement(con);
        }

        ResultSet rs = statement.executeQuery();

        return DSL.using(con).fetchStream(rs).map(LinkToBeChecked::new);
    }

    @Override
    public List<LinkToBeChecked> getList(Optional<LinkToBeCheckedFilter> filter) throws SQLException {
        return get(filter).collect(Collectors.toList());
    }

    @Override
    public Boolean save(LinkToBeChecked linkToBeChecked) throws SQLException {
        String insertQuery = "INSERT IGNORE INTO urls(url,record,collection,expectedMimeType) VALUES (?,?,?,?)";

        PreparedStatement preparedStatement = con.prepareStatement(insertQuery);
        preparedStatement.setString(1, linkToBeChecked.getUrl());
        preparedStatement.setString(2, linkToBeChecked.getRecord());
        preparedStatement.setString(3, linkToBeChecked.getCollection());
        preparedStatement.setString(4, linkToBeChecked.getExpectedMimeType());

        //affected rows
        int row = preparedStatement.executeUpdate();

        return row == 1;
    }

    @Override
    public Boolean delete(String url) throws SQLException {
        String deleteQuery = "DELETE FROM urls WHERE url=?";
        PreparedStatement preparedStatement = con.prepareStatement(deleteQuery);
        preparedStatement.setString(1,url);

        //affected rows
        int row = preparedStatement.executeUpdate();

        return row == 1;
    }

    @Override
    public List<String> getCollectionNames() throws SQLException {

        String query = "SELECT DISTINCT collection from urls";
        PreparedStatement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        List<String> collectionNames = new ArrayList<>();
        while (rs.next()) {
            collectionNames.add(rs.getString("collection"));
        }
        return collectionNames;
    }

    //todo maybe insert and delete in batch method, see curation module to check if it is needed
}
