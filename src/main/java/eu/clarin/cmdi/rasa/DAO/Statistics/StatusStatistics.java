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

import java.util.Objects;

/**
 * Status statistics: general statistics specific to one status code
 */
public class StatusStatistics extends Statistics {
    private int status;

    public StatusStatistics(int status, long count, double avgRespTime, long maxRespTime) {
        super(count, avgRespTime, maxRespTime);
        this.status = status;
    }

    public StatusStatistics(Record record) {
        super(record);
        this.status = (Integer) record.getValue("statusCode");
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusStatistics{" +
                "status=" + status +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StatusStatistics that = (StatusStatistics) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status);
    }
}
