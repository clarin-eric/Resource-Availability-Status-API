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

package eu.clarin.cmdi.rasa.filters.impl;

import eu.clarin.cmdi.rasa.filters.StatisticsCountFilter;
import eu.clarin.cmdi.rasa.helpers.Table;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;

public class ACDHStatisticsCountFilter implements StatisticsCountFilter {

    private String collection;
    private String record;
    private final Table tableName;
    private List<Category> categories;

    /**
     * Creates a statistics filter for the tables urls and status. Different constructors are for convenience.
     *
     * @param tableName database table to count, only allowed values: URLS, STATUS
     */
    public ACDHStatisticsCountFilter(Table tableName) throws SQLException {
        this.tableName = tableName;
        checkTable();
    }

    /**
     * Creates a statistics filter for the tables urls and status. Different constructors are for convenience. All are nullable.
     *
     * @param collection collection of the statistics
     * @param record     record of the statistics
     * @param categories determines which categories should be counted
     * @param tableName  database table to count, only allowed values: URLS, STATUS
     */
    public ACDHStatisticsCountFilter(String collection, String record, List<Category> categories, Table tableName) throws SQLException {
        this.collection = collection;
        this.record = record;
        this.categories = categories;
        this.tableName = tableName;
        checkTable();
    }

    /**
     * Creates a statistics filter for the tables urls and status. Different constructors are for convenience. All values are nullable.
     *
     * @param categories determines which categories should be counted
     * @param tableName  database table to count, only allowed values: URLS, STATUS
     */
    public ACDHStatisticsCountFilter(List<Category> categories, Table tableName) throws SQLException {
        this.categories = categories;
        this.tableName = tableName;
        checkTable();
    }

    /**
     * Creates a statistics filter for the tables urls and status. Different constructors are for convenience. All values are nullable.
     *
     * @param collection collection of the statistics
     * @param record     record of the statistics
     * @param tableName  database table to count, only allowed values: URLS, STATUS
     */
    public ACDHStatisticsCountFilter(String collection, String record, Table tableName) throws SQLException {
        this.collection = collection;
        this.record = record;
        this.tableName = tableName;
        checkTable();
    }

    private void checkTable() throws SQLException {
        String table = tableName.toString().toLowerCase();
        if (!table.equals("urls") && !table.equals("status")) {
            throw new SQLException("Table name not known. Possible values are status and urls.");
        }
        if (categories != null && !categories.isEmpty() && table.equals("urls")) {
            throw new SQLException("Categories filter can not be used on the urls table. Use status instead.");
        }
    }

    @Override
    public String getRecord() {
        return record;
    }

    @Override
    public String getCollection() {
        return collection;
    }

    @Override
    public String getTable() {
        return tableName.toString().toLowerCase();
    }

    @Override
    public PreparedStatement getStatement(Connection con) throws SQLException {
        StringBuilder sb = new StringBuilder();

        //if it's here, that means there is something in the where clause.
        //because it is checked before if the filter variables are set
        sb.append("SELECT COUNT(*) as count FROM ").append(tableName.toString().toLowerCase());

        StringJoiner sj = new StringJoiner(" AND ");

        if (collection != null) {
            sj.add("collection=?");
        }
        if (record != null) {
            sj.add("record=?");
        }
        if (categories != null && !categories.isEmpty()) {
            StringJoiner sjOR = new StringJoiner(" OR ");
            StringBuilder tempSB = new StringBuilder();
            tempSB.append(" ( ");
            for (Category category : categories) {
                sjOR.add("category=?");
            }
            tempSB.append(sjOR.toString());
            tempSB.append(" ) ");
            sj.add(tempSB.toString());
        }

        if (sj.length() > 0) {
            sb.append(" WHERE ").append(sj.toString());
        }

        PreparedStatement statement = con.prepareStatement(sb.toString());
        int i = 1;
        if (collection != null) {
            statement.setString(i, collection);
            i++;
        }
        if (record != null) {
            statement.setString(i, record);
            i++;
        }
        if(categories!=null){
            for(Category category:categories){
                statement.setString(i, category.name());
                i++;
            }
        }


        return statement;
    }
}
