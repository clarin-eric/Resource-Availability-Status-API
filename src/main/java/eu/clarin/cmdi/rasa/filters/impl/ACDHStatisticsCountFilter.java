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
                throw new SQLException("Undetermined or broken filter can not be used on the urls table. Use status instead.");
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
        StringBuilder sb = new StringBuilder();

        //if it's here, that means there is something in the where clause.
        //because it is checked before if the filter variables are set
        sb.append("SELECT COUNT(*) as count FROM ").append(tableName);

        boolean firstAlready = false;
        if (collection != null) {
            sb.append(" WHERE collection=?");
            firstAlready = true;
        }
        if (record != null) {
            if (firstAlready) {
                sb.append(" AND");
            } else {
                sb.append(" WHERE");
            }
            sb.append("  record=?");
            firstAlready = true;
        }
        if (broken != null && broken) {
            if (firstAlready) {
                sb.append(" AND");
            } else {
                sb.append(" WHERE");
            }
            sb.append("  statusCode NOT IN (");

            List<Integer> statuses = StatusCodeMapper.getOkStatuses();
            statuses.addAll(StatusCodeMapper.getUndeterminedStatuses());
            String comma = "";
            for (int status : statuses) {
                sb.append(comma);
                comma = ",";
                sb.append(status);
            }
            sb.append(")");

            firstAlready = true;
        }
        if (undetermined != null && undetermined) {
            if (firstAlready) {
                sb.append(" AND");
            } else {
                sb.append(" WHERE");
            }
            sb.append("  statusCode IN (");

            String comma = "";
            for (int status : StatusCodeMapper.getUndeterminedStatuses()) {
                sb.append(comma);
                comma = ",";
                sb.append(status);
            }
            sb.append(")");
        }

        PreparedStatement statement = con.prepareStatement(sb.toString());
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
