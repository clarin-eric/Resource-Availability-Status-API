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

    //returns a mongo filter depending on the non null parameters
    @Override
    public PreparedStatement getStatement(Connection con) throws SQLException {

        //if it's here, that means there is something in the where clause.
        String query = "SELECT * FROM statusView WHERE";


        boolean first = false;

        if (status != null) {
            query += " statusCode>" + status.getMinimum() + " AND statusCode<" + status.getMaximum();
            first = true;
        }

        if (before != null) {
            if (first) {
                query += " AND";
            }
            query += " timestamp<" + Timestamp.valueOf(before);
            first = true;
        }

        if (after != null) {
            if (first) {
                query += " AND";
            }
            query += " timestamp>" + Timestamp.valueOf(after);
            first = true;
        }

        if (collection != null && !collection.equals("Overall")) {
            if (first) {
                query += " AND";
            }
            query += " collection='" + collection+"'";
        }


        System.out.println("query: " + query);//TODO Delete
        PreparedStatement statement = con.prepareStatement(query);
        return statement;
    }

}
