/*
 * Copyright 2019 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.models.records;

import java.time.LocalDate;

public class Record {

    public final int id;
    public final int runnerId;
    public final LocalDate date;
    public final String timezone;
    public final Distance distance;
    public final int time;

    public Record(int id, int runnerId, LocalDate date, String timezone, Distance distance, int time) {
        this.id = id;
        this.runnerId = runnerId;
        this.date = date;
        this.timezone = timezone;
        this.distance = distance;
        this.time = time;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Record{");
        sb.append("id=").append(id);
        sb.append(", runnerId=").append(runnerId);
        sb.append(", date=").append(date);
        sb.append(", timezone='").append(timezone).append('\'');
        sb.append(", distance=").append(distance);
        sb.append(", time=").append(time);
        sb.append('}');
        return sb.toString();
    }
}
