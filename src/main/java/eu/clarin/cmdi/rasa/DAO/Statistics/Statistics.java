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

package eu.clarin.cmdi.rasa.DAO.Statistics;

import org.jooq.Record;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * General statistics: count, average response time, max response time
 */
public class Statistics {

    private long count;
    private double avgRespTime;
    private long maxRespTime;

    public Statistics(long count, double avgRespTime, long maxRespTime) {
        //these values are named in the query in ACDHStatisticsResource
        this.count = count;
        this.avgRespTime = avgRespTime;
        this.maxRespTime = maxRespTime;
    }

    public Statistics(Record record) {
        //these values are named in the query in ACDHStatisticsResource
        this.count = (Long) record.getValue("count");
        this.avgRespTime = ((BigDecimal) record.getValue("avgDuration")).doubleValue();
        Object maxDR = record.getValue("maxDuration");
        if (maxDR instanceof Integer) {
            this.maxRespTime = (Integer) maxDR;
        } else if (maxDR instanceof Long) {
            this.maxRespTime = (Long) maxDR;
        }
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getAvgRespTime() {
        return avgRespTime;
    }

    public void setAvgRespTime(double avgRespTime) {
        this.avgRespTime = avgRespTime;
    }

    public long getMaxRespTime() {
        return maxRespTime;
    }

    public void setMaxRespTime(long maxRespTime) {
        this.maxRespTime = maxRespTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return count == that.count &&
                Double.compare(that.avgRespTime, avgRespTime) == 0 &&
                maxRespTime == that.maxRespTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, avgRespTime, maxRespTime);
    }


    @Override
    public String toString() {
        return "Statistics{" +
                ", count=" + count +
                ", avgRespTime=" + avgRespTime +
                ", maxRespTime=" + maxRespTime +
                '}';
    }
}
