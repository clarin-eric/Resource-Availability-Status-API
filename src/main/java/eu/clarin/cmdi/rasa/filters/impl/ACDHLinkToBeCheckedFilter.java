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

import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ACDHLinkToBeCheckedFilter implements LinkToBeCheckedFilter {

    private String collection;
    //private Date harvestDate;
    private final String collectionQuery = "SELECT * FROM urls WHERE collection=?";
    //private final String harvestDateQuery = "SELECT * FROM urls WHERE harvestDate<?";

    /**
     * Creates a link to be checked filter for the table urls
     *
     * @param collection collection of the link
     */
    public ACDHLinkToBeCheckedFilter(String collection) {
        this.collection = collection;
    }

    /**
     * Creates a link to be checked filter for the table urls with harvestDate,
     *
     * @param harvestDate rows with harvestDate older than this will be returned
     */
    //public ACDHLinkToBeCheckedFilter(Date harvestDate) {
      //  this.harvestDate = harvestDate;
    //}

    @Override
    public String getCollection() {
        return this.collection;
    }

    //@Override
    //public Date getHarvestDate() {
    //    return this.harvestDate;
    //}

    @Override
    public PreparedStatement getStatement(Connection con) throws SQLException {
        //if it comes here, then it means there is a collection to be filtered
        PreparedStatement statement;

        //if (collection != null) {
            statement = con.prepareStatement(collectionQuery);
            statement.setString(1, collection);
        //} else {
        //    statement = con.prepareStatement(harvestDateQuery);
        //    statement.setDate(1, harvestDate);
        //}
        return statement;

    }
}
