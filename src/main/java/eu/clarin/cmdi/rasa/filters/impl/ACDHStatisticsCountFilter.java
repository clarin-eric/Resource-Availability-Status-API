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
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.StatusCodeMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ACDHStatisticsCountFilter implements StatisticsFilter {

    private String collection;
    private String record;
    private String tableName;
    private Boolean broken;
    private Boolean undetermined;

    public ACDHStatisticsCountFilter(String collection, String record, boolean broken, boolean undetermined) {
        this.collection = collection;
        this.record = record;
        this.broken = broken;
        this.undetermined = undetermined;
    }

    public ACDHStatisticsCountFilter(boolean broken, boolean undetermined) {
        this.broken = broken;
        this.undetermined = undetermined;
    }

    public ACDHStatisticsCountFilter(String collection, String record) {
        this.collection = collection;
        this.record = record;
    }

    //this method is called from within rasa depending on the method
    public void setTableName(String tableName) throws SQLException {
        if (broken != null || undetermined != null) {
            if (tableName.equals("urls")) {
                throw new SQLException("Undetermined or broken filter can not be used on the urls table. Use status view instead.");
            }
        }

        if(collection!=null || record!=null){
            if (tableName.equals("status")) {
                throw new SQLException("Collection or record filter can not be used on the status table. Use status view instead.");
            }
        }
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

        boolean firstAlready = false;
        if (collection != null) {
            query += " WHERE collection=?";
            firstAlready = true;
        }
        if (record != null) {
            if (firstAlready) {
                query += " AND";
            } else {
                query += " WHERE";
            }
            query += "  record=?";
            firstAlready = true;
        }
        if (broken != null && broken) {
            if (firstAlready) {
                query += " AND";
            } else {
                query += " WHERE";
            }
            query += "  statusCode NOT IN (";

            List<Integer> statuses = StatusCodeMapper.getOkStatuses();
            statuses.addAll(StatusCodeMapper.getUndeterminedStatuses());
            for (int status : statuses) {
                query += status + ",";
            }
            //delete the last comma
            query = query.substring(0, query.length() - 1);
            query += ")";


            firstAlready = true;
        }
        if (undetermined != null && undetermined) {
            if (firstAlready) {
                query += " AND";
            } else {
                query += " WHERE";
            }
            query += "  statusCode IN (";

            for (int status : StatusCodeMapper.getUndeterminedStatuses()) {
                query += status + ",";
            }
            //delete the last comma
            query = query.substring(0, query.length() - 1);
            query += ")";
        }

        PreparedStatement statement = con.prepareStatement(query);
        int i = 1;
        if (collection != null) {
            statement.setString(i, collection);
            i++;
        }
        if (record != null) {
            statement.setString(i, record);
        }

        return statement;
    }

    public boolean isUndetermined() {
        return undetermined;
    }

    public void setUndetermined(boolean undetermined) {
        this.undetermined = undetermined;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }
}
