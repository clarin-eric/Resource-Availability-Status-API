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

import eu.clarin.cmdi.rasa.DAO.Statistics.StatusStatistics;
import eu.clarin.cmdi.rasa.filters.impl.ACDHStatisticsCountFilter;
import eu.clarin.cmdi.rasa.linkResources.StatisticsResource;
import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ACDHStatisticsResource implements StatisticsResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHStatisticsResource.class);

    private Connection con;

    public ACDHStatisticsResource(Connection con) {
        this.con = con;
    }

    //avgDuration, maxDuration, countStatus should be named so, because in Statistics constructor, they are called as such.
    @Override
    public List<StatusStatistics> getStatusStatistics(String collection) throws SQLException {
        String query;
        PreparedStatement statement;
        if (collection == null || collection.equals("Overall")) {
            query = "SELECT statusCode, AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM statusView GROUP BY statusCode";
            statement = con.prepareStatement(query);
        } else {
            query = "SELECT statusCode, AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM statusView WHERE collection=? GROUP BY statusCode";
            statement = con.prepareStatement(query);
            statement.setString(1, collection);
        }

        ResultSet rs = statement.executeQuery();

        return DSL.using(con).fetchStream(rs).map(StatusStatistics::new).collect(Collectors.toList());
    }

    @Override
    public Statistics getOverallStatistics(String collection) throws SQLException {
        String query;
        PreparedStatement statement;
        if (collection == null || collection.equals("Overall")) {
            query = "SELECT AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM statusView";
            statement = con.prepareStatement(query);
        } else {
            query = "SELECT AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM statusView WHERE collection=?";
            statement = con.prepareStatement(query);
            statement.setString(1, collection);
        }

        ResultSet rs = statement.executeQuery();
        Record record = DSL.using(con).fetchOne(rs);
        //return null if count is 0, ie. collection not found in database
        return (Long) record.getValue("count") == 0L ? null : record.map(Statistics::new);
    }

    @Override
    public long countStatusView(Optional<ACDHStatisticsCountFilter> filter) throws SQLException {
        return count(filter, "statusView");
    }

    @Override
    public long countUrlsTable(Optional<ACDHStatisticsCountFilter> filter) throws SQLException {
        return count(filter, "urls");
    }

    private long count(Optional<ACDHStatisticsCountFilter> filterOptional, String tableName) throws SQLException {
        String defaultQuery = "SELECT COUNT(*) as count FROM " + tableName;
        PreparedStatement statement;
        if (!filterOptional.isPresent()) {
            statement = con.prepareStatement(defaultQuery);
        } else {
            ACDHStatisticsCountFilter filter = filterOptional.get();
            filter.setTableName(tableName);
            statement = filter.getStatement(con);
        }
        ResultSet rs = statement.executeQuery();
        Record record = DSL.using(con).fetchOne(rs);
        return (Long) record.getValue("count");
    }

}
