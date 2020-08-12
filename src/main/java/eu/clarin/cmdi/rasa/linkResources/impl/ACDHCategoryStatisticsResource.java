package eu.clarin.cmdi.rasa.linkResources.impl;

import eu.clarin.cmdi.rasa.DAO.Statistics.CategoryStatistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.StatusStatistics;
import eu.clarin.cmdi.rasa.filters.impl.ACDHStatisticsCountFilter;
import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.linkResources.CategoryStatisticsResource;
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

public class ACDHCategoryStatisticsResource implements CategoryStatisticsResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHCategoryStatisticsResource.class);

    private final ConnectionProvider connectionProvider;

    public ACDHCategoryStatisticsResource(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public List<CategoryStatistics> getCategoryStatistics(String collection) throws SQLException {
        if (collection == null || collection.equals("Overall")) {
            return getCategoryStatistics();
        }
        String query = "SELECT category, AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status WHERE collection=? GROUP BY category";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = getPreparedStatement(con, collection, query)) {
                try (ResultSet rs = statement.executeQuery()) {
                    try (Stream<Record> recordStream = DSL.using(con).fetchStream(rs)) {
                        //this filter is needed because when we added category field, it was null for a lot of the entries. stormychecker needs to work a little bit until all category fields are filled.
                        //TODO do the query "select * from status where category is null;" in the future. if there are 0 results, you can remove the filter
                        return recordStream.filter(record -> record.getValue("category")!=null).map(CategoryStatistics::new).collect(Collectors.toList());
//                        return recordStream.map(CategoryStatistics::new).collect(Collectors.toList());
                    }
                }
            }
        }

    }

    //avgDuration, maxDuration, countStatus should be named so, because in Statistics constructor, they are called as such.
    @Override
    public List<CategoryStatistics> getCategoryStatistics() throws SQLException {
        String query = "SELECT category, AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status GROUP BY category";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(query)) {
                try (ResultSet rs = statement.executeQuery()) {
                    try (Stream<Record> recordStream = DSL.using(con).fetchStream(rs)) {
                        //this filter is needed because when we added category field, it was null for a lot of the entries. stormychecker needs to work a little bit until all category fields are filled.
                        //TODO do the query "select * from status where category is null;" in the future. if there are 0 results, you can remove the filter
                        return recordStream.filter(record -> record.getValue("category")!=null).map(CategoryStatistics::new).collect(Collectors.toList());
//                        return recordStream.map(CategoryStatistics::new).collect(Collectors.toList());
                    }
                }
            }
        }
    }

    @Override
    public Statistics getOverallStatistics(String collection) throws SQLException {
        if (collection == null || collection.equals("Overall")) {
            return getOverallStatistics();
        }
        String query = "SELECT AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status WHERE collection=?";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = getPreparedStatement(con, collection, query)) {
                try (ResultSet rs = statement.executeQuery()) {
                    Record record = DSL.using(con).fetchOne(rs);

                    //return null if count is 0, ie. collection not found in database
                    return (Long) record.getValue("count") == 0L ? null : record.map(Statistics::new);
                }
            }
        }
    }

    @Override
    public Statistics getOverallStatistics() throws SQLException {
        String query = "SELECT AVG(duration) AS avgDuration, MAX(duration) AS maxDuration, COUNT(duration) AS count FROM status";
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(query)) {
                try (ResultSet rs = statement.executeQuery()) {
                    Record record = DSL.using(con).fetchOne(rs);

                    //return null if count is 0, ie. collection not found in database
                    return (Long) record.getValue("count") == 0L ? null : record.map(Statistics::new);
                }
            }
        }
    }

    @Override
    public long countTable(ACDHStatisticsCountFilter filter) throws SQLException {
        try (Connection con = connectionProvider.getConnection()) {
            try (PreparedStatement statement = filter.getStatement(con)) {
                try (ResultSet rs = statement.executeQuery()) {
                    Record record = DSL.using(con).fetchOne(rs);

                    return (Long) record.getValue("count");
                }
            }
        }
    }

    private PreparedStatement getPreparedStatement(Connection con, String collection, String query) throws SQLException {
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, collection);
        return statement;
    }
}
