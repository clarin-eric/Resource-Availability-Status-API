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

import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import org.apache.commons.lang3.Range;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ACDHCheckedLinkFilter implements CheckedLinkFilter {

    private Range<Integer> status;
    private LocalDateTime before;
    private LocalDateTime after;
    private ZoneId zone;
    private String collection;

    private int start = -1;
    private int end = -1;

    //zoneId is the timezone of the user, it is suggested to use ZoneId.systemDefault() when calling this method
    //also before and after parameters should be instantiated with ZoneId.systemDefault() as well
    public ACDHCheckedLinkFilter(Range<Integer> status, LocalDateTime before, LocalDateTime after, ZoneId zone) {
        this.status = status;
        this.before = before;
        this.after = after;
        this.zone = zone;
    }

    public ACDHCheckedLinkFilter(Range<Integer> status, LocalDateTime before, LocalDateTime after, ZoneId zone, String collection) {
        this.status = status;
        this.before = before;
        this.after = after;
        this.zone = zone;
        this.collection = collection;
    }

    public ACDHCheckedLinkFilter(String collection) {
        this.collection = collection;
    }

    public ACDHCheckedLinkFilter(String collection, int status) {
        this.collection = collection;
        this.status = Range.between(status, status);
    }

    public ACDHCheckedLinkFilter(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Range<Integer> getStatus() {
        return status;
    }

    @Override
    public LocalDateTime getCheckedBeforeDate() {
        return before;
    }

    @Override
    public LocalDateTime getCheckedAfterDate() {
        return after;
    }

    @Override
    public String getCollection() {
        return collection;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    public void setEnd(int limitEnd) {
        this.end = limitEnd;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String prepareQuery(String inList) {
        //if it's here, that means there is something in the where clause.
        //because it is checked before if the filter variables are set
        String query = "SELECT * FROM statusView";

        boolean firstAlready = false;
        if (status != null) {
            query += " WHERE";
            query += " statusCode>=? AND statusCode<=?";
            firstAlready = true;
        }
        if (before != null) {
            if (firstAlready) {
                query += " AND";
            } else {
                query += " WHERE";
            }
            query += " timestamp<?";
            firstAlready = true;
        }
        if (after != null) {
            if (firstAlready) {
                query += " AND";
            } else {
                query += " WHERE";
            }
            query += " timestamp>?";
            firstAlready = true;
        }
        if (collection != null && !collection.equals("Overall")) {
            if (firstAlready) {
                query += " AND";
            } else {
                query += " WHERE";
            }
            query += " collection=?";
            firstAlready = true;
        }

        if (inList != null) {
            if (firstAlready) {
                query += " AND";
            } else {
                query += " WHERE";
            }
            query += inList;
        }

        if (start > 0 && end > 0) {
            query += " LIMIT ? OFFSET ?";
        } else if (start > 0) {
            query += " LIMIT 18446744073709551615 OFFSET ?";
        } else if (end > 0) {
            query += " LIMIT ?";
        }
        return query;
    }

    private PreparedStatement prepareStatement(Connection con, String query) throws SQLException {
        PreparedStatement statement = con.prepareStatement(query);

        //query setting done, now fill it
        int i = 1;
        if (status != null) {
            statement.setInt(i, status.getMinimum());
            statement.setInt(i + 1, status.getMaximum());
            i += 2;
        }
        if (before != null) {
            statement.setTimestamp(i, Timestamp.valueOf(before));
            i++;
        }
        if (after != null) {
            statement.setTimestamp(i, Timestamp.valueOf(after));
            i++;
        }
        if (collection != null && !collection.equals("Overall")) {
            statement.setString(i, collection);
            i++;
        }

        if (start > 0 && end > 0) {
//            query += "LIMIT ? OFFSET ?";
            statement.setInt(i, end - start + 1);
            statement.setInt(i + 1, start - 1);//start 1 would need offset 0
        } else if (start > 0) {
//            query += " LIMIT 18446744073709551615 OFFSET ?";
            statement.setInt(i, start - 1);//start 1 would need offset 0
        } else if (end > 0) {
//            query += " LIMIT ?";
            statement.setInt(i, end);
        }

        return statement;
    }

    //returns a mysql statement filter depending on the non null parameters
    @Override
    public PreparedStatement getStatement(Connection con) throws SQLException {

        String query = prepareQuery(null);
        System.out.println(query);
        return prepareStatement(con, query);
    }

    @Override
    public PreparedStatement getStatement(Connection con, String inList) throws SQLException {
        String query = prepareQuery(inList);
        return prepareStatement(con, query);
    }


}
