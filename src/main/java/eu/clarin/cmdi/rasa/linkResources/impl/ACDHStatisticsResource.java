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

package eu.clarin.cmdi.rasa.linkResources.impl;

import eu.clarin.cmdi.rasa.filters.impl.ACDHStatisticsFilter;
import eu.clarin.cmdi.rasa.linkResources.StatisticsResource;
import eu.clarin.cmdi.rasa.links.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class ACDHStatisticsResource implements StatisticsResource {

    private final static Logger _logger = LoggerFactory.getLogger(ACDHStatisticsResource.class);

    private Connection con;

    public ACDHStatisticsResource(Connection con) {
        this.con = con;
    }

    @Override
    public List<Statistics> getStatusStatistics(String collection) {
//todo
        return null;
//        AggregateIterable<Document> iterable;
//        if (collection == null || collection.equals("Overall")) {
//            iterable = linksChecked.aggregate(Arrays.asList(
//                    Aggregates.group("$status",
//                            Accumulators.sum("count", 1),
//                            Accumulators.avg("avg_resp", "$duration"),
//                            Accumulators.max("max_resp", "$duration")
//                    ),
//                    Aggregates.sort(orderBy(ascending("_id")))
//            ));
//        } else {
//            iterable = linksChecked.aggregate(Arrays.asList(
//                    Aggregates.match(eq("collection", collection)),
//                    Aggregates.group("$status",
//                            Accumulators.sum("count", 1),
//                            Accumulators.avg("avg_resp", "$duration"),
//                            Accumulators.max("max_resp", "$duration")
//                    ),
//                    Aggregates.sort(orderBy(ascending("_id")))
//            ));
//        }
//
//
//        List<Statistics> stats = new ArrayList<>();
//
//        for (Document doc : iterable) {
//            Statistics statistics = new Statistics();
//            statistics.setAvgRespTime(doc.getDouble("avg_resp"));
//            statistics.setMaxRespTime(doc.getLong("max_resp"));
//            int statusCode = doc.getInteger("_id");
//            statistics.setStatus(statusCode);
//            if (statusCode == 200) {
//                statistics.setCategory("Ok");
//            } else if (statusCode == 401 || statusCode == 405 || statusCode == 429) {
//                statistics.setCategory("Undetermined");
//            } else {
//                statistics.setCategory("Broken");
//            }
//            statistics.setCount(doc.getInteger("count"));
//            stats.add(statistics);
//        }
//
//        return stats;
    }

    @Override
    public Statistics getOverallStatistics(String collection) {
        //todo
        return null;
//        AggregateIterable<Document> aggregate;
//
//        if (collection == null || collection.equals("Overall")) {
//            aggregate = linksChecked.aggregate(
//                    Arrays.asList(
//                            Aggregates.group(null,
//                                    Accumulators.sum("count", 1),
//                                    Accumulators.avg("avg_resp", "$duration"),
//                                    Accumulators.max("max_resp", "$duration")
//                            )));
//        } else {
//            aggregate = linksChecked.aggregate(
//                    Arrays.asList(
//                            Aggregates.match(eq("collection", collection)),
//                            Aggregates.group(null,
//                                    Accumulators.sum("count", 1),
//                                    Accumulators.avg("avg_resp", "$duration"),
//                                    Accumulators.max("max_resp", "$duration")
//                            )));
//        }
//
//        Document result = aggregate.first();
//
//        Statistics statistics = new Statistics();
//        if (result != null) {
//            statistics.setAvgRespTime(result.getDouble("avg_resp"));
//            statistics.setMaxRespTime(result.getLong("max_resp"));
//            statistics.setCount(result.getInteger("count"));
//        }
//
//        return statistics;
    }

    @Override
    public long countLinksChecked(Optional<ACDHStatisticsFilter> filter) {

        //todo
        return 0L;

//        if (filter.isPresent()) {
//            return linksChecked.countDocuments(filter.get().getMongoFilter());
//        } else {
//            return linksChecked.countDocuments();
//        }

    }

    @Override
    public long countLinksToBeChecked(Optional<ACDHStatisticsFilter> filter) {
//todo
        return 0L;

        //        if (filter.isPresent()) {
//            return linksToBeChecked.countDocuments(filter.get().getMongoFilter());
//        } else {
//            return linksToBeChecked.countDocuments();
//        }
    }

    //todo test this method along all other methods
    @Override
    public int getDuplicateCount(String collection) {
        //todo
        return 0;

//        AggregateIterable<Document> iterable = linksToBeChecked.aggregate(Arrays.asList(
//                Aggregates.match(eq("collection", collection)),
//                Aggregates.lookup("linksChecked", "url", "url", "checked")
//        ));
//        int duplicates = 0;
//        for (Document doc : iterable) {
//            if (!((List<?>) doc.get("checked")).isEmpty()) {
//                duplicates++;
//            }
//        }
//
//        return duplicates;
    }
}
