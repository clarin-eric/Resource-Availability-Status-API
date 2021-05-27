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
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;

public class ACDHStatisticsCountFilter implements StatisticsCountFilter {
	private boolean overAll;
    private String collection;
    private String record;
    private List<Category> categories;

    /**
     * Creates a statistics filter for the tables urls and status. Different constructors are for convenience.
     *
     * @param tableName database table to count, only allowed values: URLS, STATUS
     */
    public ACDHStatisticsCountFilter(boolean overAll) throws SQLException {
    	this.overAll = overAll;
    }

    /**
     * Creates a statistics filter for the tables urls and status. Different constructors are for convenience. All are nullable.
     *
     * @param collection collection of the statistics
     * @param record     record of the statistics
     * @param categories determines which categories should be counted
     * @param tableName  database table to count, only allowed values: URLS, STATUS
     */
    public ACDHStatisticsCountFilter(String collection, String record, List<Category> categories) throws SQLException {
        this.collection = collection;
        this.record = record;
        this.categories = categories;
    }

    /**
     * Creates a statistics filter for the tables urls and status. Different constructors are for convenience. All values are nullable.
     *
     * @param categories determines which categories should be counted
     * @param tableName  database table to count, only allowed values: URLS, STATUS
     */
    public ACDHStatisticsCountFilter(List<Category> categories) throws SQLException {
        this.categories = categories;
    }

    /**
     * Creates a statistics filter for the tables urls and status. Different constructors are for convenience. All values are nullable.
     *
     * @param collection collection of the statistics
     * @param record     record of the statistics
     * @param tableName  database table to count, only allowed values: URLS, STATUS
     */
    public ACDHStatisticsCountFilter(String collection, String record, boolean overAll) throws SQLException {
    	this.overAll = overAll;
        this.collection = collection;
        this.record = record;
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
    public PreparedStatement getStatement(Connection con) throws SQLException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT COUNT(*) as count FROM ");
        sb.append(overAll?"link l":"status s");

        if(this.collection != null)
        	sb.append(", link_context lc, context c, providerGroup p");
        else if(this.record != null)
        	sb.append(", link_context lc, context c");


        StringJoiner sj = new StringJoiner(" AND ");

        if (collection != null) {
            sj.add("p.name=?");
            sj.add("c.providerGroup_id=p.id");
        }
        if (record != null) {
            sj.add("c.record=?");

        }
        if(collection != null || record != null) {
            sj.add("lc.context_id = c.id");
            sj.add((overAll?"l.id":"s.link_id") + "=lc.link_id");
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
