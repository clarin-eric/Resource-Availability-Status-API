package eu.clarin.cmdi.rasa.linkResources;

import eu.clarin.cmdi.rasa.DAO.Statistics.CategoryStatistics;
import eu.clarin.cmdi.rasa.DAO.Statistics.Statistics;
import eu.clarin.cmdi.rasa.filters.StatisticsCountFilter;

import java.sql.SQLException;
import java.util.List;

public interface CategoryStatisticsResource {

    /**
     * Gets category statistics per collection. Category statistics are the following info per category:
     * count,avg response time, max response time
     * calls getCategoryStatistics() for all collections (if given collection is null or equals "Overall").
     * @param collection collection requested for the statistics
     * @return A list of CategoryStatistics for each category, will return empty list if given collection doesn't exist
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    List<CategoryStatistics> getCategoryStatistics(String collection) throws SQLException;

    /**
     * Gets overall category statistics. Category statistics are the following info per category:
     * count,avg response time, max response time
     * @return A list of CategoryStatistics for each category
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    List<CategoryStatistics> getCategoryStatistics() throws SQLException;

    /**
     * Gets overall statistics per collection without grouping by gategory, so get overall count, avg response time and max response time
     * calls getOverallStatistics() for all collections (if given collection is null or equals "Overall").
     * will return null if given collection doesn't exist
     * @param collection collection requested for the statistics
     * @return A Statistics object for the requested collection
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Statistics getOverallStatistics(String collection) throws SQLException;

    /**
     * Gets overall statistics without grouping by category, so get overall count, avg response time and max response time
     * @return A Statistics object for the whole database
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    Statistics getOverallStatistics() throws SQLException;

    /**
     * Counts table with the given filter. Table name in the filter must be set
     * @param filter Filter to be applied to the count query
     * @return number resulting from the count
     * @throws SQLException occurs if there was an error during statement preparation or execution
     */
    long countTable(StatisticsCountFilter filter) throws SQLException;
}
