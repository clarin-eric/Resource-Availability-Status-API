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
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
import org.apache.commons.lang3.Range;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.StringJoiner;
import java.util.function.BiFunction;

public class ACDHCheckedLinkFilter implements CheckedLinkFilter {

    private Range<Integer> status;
    private LocalDateTime before;
    private LocalDateTime after;
    private ZoneId zone;
    private String collection;
    private Category category;

    private int start = -1;
    private int end = -1;


    /**
     * Creates a checked link filter for the table status. Different constructors are for convenience. All values are nullable.
     *
     * @param status a range of integers as statuses
     * @param before urls checked before this will be in the result. It is suggested to be instantiated with ZoneId.systemDefault()
     * @param after  urls checked after this will be in the result. It is suggested to be instantiated with ZoneId.systemDefault()
     * @param zone   the timezone of the user, it is suggested to use ZoneId.systemDefault() when calling this method
     */
    public ACDHCheckedLinkFilter(Range<Integer> status, LocalDateTime before, LocalDateTime after, ZoneId zone) {
        this.status = status;
        this.before = before;
        this.after = after;
        this.zone = zone;
    }

    /**
     * Creates a checked link filter for the table status. Different constructors are for convenience. All values are nullable.
     *
     * @param status     a range of integers as statuses
     * @param before     urls checked before this will be in the result. It is suggested to be instantiated with ZoneId.systemDefault()
     * @param after      urls checked after this will be in the result. It is suggested to be instantiated with ZoneId.systemDefault()
     * @param zone       the timezone of the user, it is suggested to use ZoneId.systemDefault() when calling this method
     * @param collection collection of the links
     */
    public ACDHCheckedLinkFilter(Range<Integer> status, LocalDateTime before, LocalDateTime after, ZoneId zone, String collection) {
        this.status = status;
        this.before = before;
        this.after = after;
        this.zone = zone;
        this.collection = collection;
    }

    /**
     * Creates a checked link filter for the table status. Different constructors are for convenience. All values are nullable.
     *
     * @param collection collection of the links
     */
    public ACDHCheckedLinkFilter(String collection) {
        this.collection = collection;
    }

    /**
     * Creates a checked link filter for the table status. Different constructors are for convenience. All values are nullable.
     *
     * @param status     a range of integers as statuses
     * @param collection collection of the links
     */
    public ACDHCheckedLinkFilter(String collection, int status) {
        this.collection = collection;
        this.status = Range.between(status, status);
    }

    /**
     * Creates a checked link filter for table category. Different constructors are for convenience. All values are nullable.
     *
     * @param collection collection of the links
     * @param category   category requested
     */
    public ACDHCheckedLinkFilter(String collection, Category category) {
        this.collection = collection;
        this.category = category;
    }

    /**
     * Creates a checked link filter for the table status. Different constructors are for convenience. All values are nullable.
     *
     * @param start limits the results, starting from this entry in the database
     * @param end   limits the results until this entry in the database
     */
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

    @Override
    public Category getCategory() {
        return category;
    }

    public ACDHCheckedLinkFilter setEnd(int limitEnd) {
        this.end = limitEnd;
        return this;
    }

    public ACDHCheckedLinkFilter setStart(int start) {
        this.start = start;
        return this;
    }

    /**
     * Prepares the query based on the variables
     *
     * @param inList filters out the results, only urls within this list can be
     *               in the results
     * @return prepared query to be used in preparing the statement
     */
    private String prepareQuery(String inList) {
        StringBuilder sb = new StringBuilder();

        //if it's here, that means there is something in the where clause.
        //because it is checked before if the filter variables are set
        sb.append("SELECT * FROM status");

        StringJoiner sj = new StringJoiner(" AND ");

        if (status != null) {
            sj.add("statusCode>=? AND statusCode<=?");
        }
        if (category != null) {
            sj.add("category=?");
        }
        if (before != null) {
            sj.add("timestamp<?");
        }
        if (after != null) {
            sj.add("timestamp>?");
        }
        if (collection != null && !collection.equals("Overall")) {
            sj.add("collection=?");
        }
        if (inList != null) {
            sj.add(inList);
        }

        if (sj.length() > 0) {
            sb.append(" WHERE ").append(sj.toString());
        }

        if (start > 0 && end > 0) {
            sb.append(" LIMIT ? OFFSET ?");
        } else if (start > 0) {
            sb.append(" LIMIT 18446744073709551615 OFFSET ?");//max number because you cant use use offset without limit
        } else if (end > 0) {
            sb.append(" LIMIT ?");
        }
        return sb.toString();
    }

    /**
     * This method prepares the statement with the filter's variables with the
     * given query.
     *
     * @param con             database connection
     * @param query           query to be prepared
     * @param addInListParams function that adds parameters for the 'in list'; takes the next available parameter index and returns the parameter index to continue with
     * @return a fully prepared statement with the query and given parameters,
     * the caller can directly execute and read the results
     * @throws SQLException can occur during preparing the statement
     */
    private PreparedStatement prepareStatement(Connection con, String query, BiFunction<PreparedStatement, Integer, Integer> addInListParams) throws SQLException {
        PreparedStatement statement = con.prepareStatement(query);

        //query setting done, now fill it
        int i = 1;
        if (status != null) {
            statement.setInt(i, status.getMinimum());
            statement.setInt(i + 1, status.getMaximum());
            i += 2;
        }
        if (category != null) {
            statement.setString(i, category.name());
            i++;
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

        if (addInListParams != null) {
            i = addInListParams.apply(statement, i);
        }

        if (start > 0 && end > 0) {
//            sb.append("LIMIT ? OFFSET ?");
            statement.setInt(i, end - start + 1);
            statement.setInt(i + 1, start - 1);//start 1 would need offset 0
        } else if (start > 0) {
//            sb.append(" LIMIT 18446744073709551615 OFFSET ?");
            statement.setInt(i, start - 1);//start 1 would need offset 0
        } else if (end > 0) {
//            sb.append(" LIMIT ?");
            statement.setInt(i, end);
        }

        return statement;
    }

    @Override
    public PreparedStatement getStatement(Connection con) throws SQLException {
        String query = prepareQuery(null);
        return prepareStatement(con, query, null);
    }

    @Override
    public PreparedStatement getStatement(Connection con, String inList, BiFunction<PreparedStatement, Integer, Integer> addInListParams) throws SQLException {
        String query = prepareQuery(inList);
        return prepareStatement(con, query, addInListParams);
    }

}
