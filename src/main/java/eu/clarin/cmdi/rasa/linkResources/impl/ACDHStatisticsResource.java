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

import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.StatusStatistics;
import eu.clarin.cmdi.rasa.filters.impl.ACDHStatisticsCountFilter;
import eu.clarin.cmdi.rasa.linkResources.StatisticsResource;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ACDHStatisticsResource implements StatisticsResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHStatisticsResource.class);

    private Connection con;


    //set query strings once and reuse them again

    public ACDHStatisticsResource(Connection con) {
        this.con = con;
    }

    //avgDuration, maxDuration, countStatus should be named so, because in Statistics constructor, they are called as such.
    @Override
    public List<StatusStatistics> getStatusStatistics(String collection) throws SQLException {
        if (collection==null || collection.equals("Overall")) {
            return getStatusStatistics();
        }
        String query = "SELECT statusCode, AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status WHERE collection=? GROUP BY statusCode";
        try (PreparedStatement statement = getPreparedStatement(collection, query)) {
            try (ResultSet rs = statement.executeQuery()) {
                try (Stream<Record> recordStream = DSL.using(con).fetchStream(rs)) {
                    return recordStream.map(StatusStatistics::new).collect(Collectors.toList());
                }
            }
        }

    }

    //avgDuration, maxDuration, countStatus should be named so, because in Statistics constructor, they are called as such.
    @Override
    public List<StatusStatistics> getStatusStatistics() throws SQLException {
        String query = "SELECT statusCode, AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status GROUP BY statusCode";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            try (ResultSet rs = statement.executeQuery()) {
                try (Stream<Record> recordStream = DSL.using(con).fetchStream(rs)) {
                    return recordStream.map(StatusStatistics::new).collect(Collectors.toList());
                }
            }
        }

    }

    @Override
    public Statistics getOverallStatistics(String collection) throws SQLException {
        if (collection==null || collection.equals("Overall")) {
            return getOverallStatistics();
        }
        String query = "SELECT AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status WHERE collection=?";
        try (PreparedStatement statement = getPreparedStatement(collection, query)) {
            try (ResultSet rs = statement.executeQuery()) {
                Record record = DSL.using(con).fetchOne(rs);

                //return null if count is 0, ie. collection not found in database
                return (Long) record.getValue("count") == 0L ? null : record.map(Statistics::new);
            }

        }
    }

    @Override
    public Statistics getOverallStatistics() throws SQLException {
        String query = "SELECT AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            try (ResultSet rs = statement.executeQuery()) {
                Record record = DSL.using(con).fetchOne(rs);

                //return null if count is 0, ie. collection not found in database
                return (Long) record.getValue("count") == 0L ? null : record.map(Statistics::new);
            }

        }
    }

    private PreparedStatement getPreparedStatement(String collection, String query) throws SQLException {
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, collection);
        return statement;
    }

    @Override
    public long countTable(ACDHStatisticsCountFilter filter) throws SQLException {
        try (PreparedStatement statement = filter.getStatement(con)) {
            try (ResultSet rs = statement.executeQuery()) {
                Record record = DSL.using(con).fetchOne(rs);

                return (Long) record.getValue("count");
            }
        }
    }
}
