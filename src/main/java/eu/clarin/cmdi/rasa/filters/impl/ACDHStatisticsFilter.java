///*
// * Copyright (C) 2019 CLARIN
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// *
// */
//
//package eu.clarin.cmdi.rasa.filters.impl;
//
//import com.mongodb.client.model.Filters;
//import eu.clarin.cmdi.rasa.filters.StatisticsFilter;
//import org.bson.conversions.Bson;
//
//public class ACDHStatisticsFilter implements StatisticsFilter {
//
//    private boolean broken;
//    private boolean undetermined;
//    private String collection;
//    private String record;
//
//    //if you have broken and undetermined true at the same time, it might cause problems...
//    public ACDHStatisticsFilter(String collection, String record, boolean broken, boolean undetermined) {
//        this.broken = broken;
//        this.collection = collection;
//        this.record = record;
//        this.undetermined = undetermined;
//    }
//
//    @Override
//    public String getRecord() {
//        return record;
//    }
//
//    @Override
//    public boolean getBroken() {
//        return broken;
//    }
//
//    @Override
//    public boolean getUndetermined() {
//        return undetermined;
//    }
//
//    @Override
//    public Bson getMongoFilter() {
//        Bson filter;
//
//        if (collection != null && !collection.equals("Overall")) {
//            filter = Filters.eq("collection", collection);
//        } else {
//            filter = Filters.where("1==1");
//        }
//
//        if(record!=null){
//            filter = Filters.eq("record", record);
//        }
//
//        if (broken) {
//            Bson brokenLinksFilter = Filters.not(Filters.in("status", 200, 302, 401, 405, 429));
//            filter = Filters.and(filter, brokenLinksFilter);
//        }
//
//        if (undetermined) {
//            Bson undeterminedLinksFilter = Filters.in("status", 401, 405, 429);
//            filter = Filters.and(filter, undeterminedLinksFilter);
//        }
//
//        return filter;
//    }
//
//    @Override
//    public String getCollection() {
//        return collection;
//    }
//}
