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

import eu.clarin.cmdi.rasa.filters.StatisticsFilter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ACDHStatisticsCountFilter implements StatisticsFilter {

    private String collection;
    private String record;
    private String tableName;

    //if you have broken and undetermined true at the same time, it might cause problems...
    public ACDHStatisticsCountFilter(String collection, String record) {
        this.collection = collection;
        this.record = record;
    }

    //this method is called from within rasa depending on the method
    public void setTableName(String tableName) {
        this.tableName = tableName;
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

        //if it's here, that means there is something in the where clause.
        //because it is checked before if the filter variables are set
        String query = "SELECT COUNT(*) as count FROM " + tableName;
        PreparedStatement statement;
        if (collection != null && record != null) {
            query += " WHERE collection=? AND record=?";
            statement = con.prepareStatement(query);
            statement.setString(1, collection);
            statement.setString(2, record);
            return statement;
        } else if (collection != null) {
            query += " WHERE collection=?";
            statement = con.prepareStatement(query);
            statement.setString(1, collection);
            return statement;
        } else if (record != null) {
            query += " WHERE record=?";
            statement = con.prepareStatement(query);
            statement.setString(1, record);
            return statement;
        } else {
            //shouldn't come here but as a safety
            return con.prepareStatement(query);
        }
    }
}
