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
package com.example.json;

import com.example.Result;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Record {

    public int id;
    public Distance distance;
    public int time;

    public Record() {
    }

    public Record(int id, Distance distance, int time) {
        this.id = id;
        this.distance = distance;
        this.time = time;
    }

    public static Record of(int id, Distance distance, int hour, int min, int sec) {
        int time = hour * 60 * 60 + min * 60 + sec;
        return new Record(id, distance, time);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Record{");
        sb.append("id=").append(id);
        sb.append(", distance=").append(distance);
        sb.append(", time=").append(time);
        sb.append('}');
        return sb.toString();
    }

    public static class Entry {
        public Distance distance;
        public int hour;
        public int min;
        public int sec;

        public Entry(Distance distance, int hour, int min, int sec) {
            this.distance = distance;
            this.hour = hour;
            this.min = min;
            this.sec = sec;
        }

        public Entry() {
        }

        public Result<ErrorEntry, Validated> validated() {
            List<String> message = new ArrayList<>();
            if (distance.detectProblemOnHour(hour)) {
                message.add(String.format("invalid hour value: %d", hour));
            }
            if (distance.detectProblemOnMin(min)) {
                message.add(String.format("invalid min value: %d", min));
            }
            if (distance.detectProblemOnSec(sec)) {
                message.add(String.format("invalid sec value: %d", sec));
            }
            if (message.isEmpty()) {
                return Result.success(new Validated(distance, hour, min, sec));
            }
            return Result.failure(new ErrorEntry(List.copyOf(message)));
        }

        public Record toRecord(int id) {
            return Record.of(id, distance, hour, min, sec);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Entry{");
            sb.append("distance=").append(distance);
            sb.append(", hour=").append(hour);
            sb.append(", min=").append(min);
            sb.append(", sec=").append(sec);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class Validated extends Entry {
        public Validated(Distance distance, int hour, int min, int sec) {
            super(distance, hour, min, sec);
        }
    }

    public static class ErrorEntry {
        public final List<String> messages;

        ErrorEntry(List<String> messages) {
            this.messages = messages;
        }
    }
}
