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

package eu.clarin.cmdi.rasa.helpers.impl;

import com.mongodb.client.model.Filters;
import eu.clarin.cmdi.rasa.links.CheckedLink;
import org.apache.commons.lang3.Range;
import org.bson.conversions.Bson;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ACDHCheckedLinkFilter implements eu.clarin.cmdi.rasa.helpers.CheckedLinkFilter {

    //linkchecker is run in Vienna, thats why all timestamps in the database are Vienna based
    private ZoneId VIENNA_ZONE = ZoneId.of("Europe/Vienna");

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

    public ACDHCheckedLinkFilter(String collection) {
        this.collection = collection;
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


    public boolean matches(CheckedLink checkedLink) {

        boolean statusMatches = status == null || (status.getMaximum() >= checkedLink.getStatus() && status.getMinimum() <= checkedLink.getStatus());

        LocalDateTime checkedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(checkedLink.getTimestamp()), VIENNA_ZONE);

        //convert checked date from database to time zone of the caller
        checkedDate = checkedDate.atZone(VIENNA_ZONE).withZoneSameInstant(zone).toLocalDateTime();

        boolean beforeMatches = before == null || before.isBefore(checkedDate);
        boolean afterMatches = after == null || after.isAfter(checkedDate);

        boolean collectionMatches = collection == null || collection.equals(checkedLink.getCollection());

        return statusMatches && beforeMatches && afterMatches && collectionMatches;

    }

    //returns a mongo filter depending on the non null parameters
    @Override
    public Bson getMongoFilter() {

        Bson statusFilter;
        Bson beforeFilter;
        Bson afterFilter;
        Bson collectionFilter;

        if (status != null) {
            statusFilter = Filters.and(Filters.gte("status", status.getMinimum()), Filters.lte("status", status.getMaximum()));
        } else {
            statusFilter = Filters.where("1==1");
        }

        //here vienna zone is used because the database timestamps are all in vienna zone
        if (before != null) {
            ZonedDateTime beforeZdt = before.atZone(zone).withZoneSameInstant(VIENNA_ZONE);
            long beforeMillis = beforeZdt.toInstant().toEpochMilli();

            beforeFilter = Filters.gt("timestamp", beforeMillis);
        } else {
            beforeFilter = Filters.where("1==1");
        }


        if (after != null) {
            ZonedDateTime afterZdt = after.atZone(zone).withZoneSameInstant(VIENNA_ZONE);
            long afterMillis = afterZdt.toInstant().toEpochMilli();

            afterFilter = Filters.lt("timestamp", afterMillis);
        } else {
            afterFilter = Filters.where("1==1");
        }

        if (collection != null) {
            collectionFilter = Filters.eq("collection", collection);
        } else {
            collectionFilter = Filters.where("1==1");
        }


        return Filters.and(statusFilter, beforeFilter, afterFilter, collectionFilter);
    }

}
