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
package com.example;

import com.example.json.Distance;
import com.example.json.Record;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BackendService {

    private final AtomicInteger idGen;
    private final Map<Integer, Record> records;

    BackendService() {
        this.idGen = new AtomicInteger(5);
        this.records = new TreeMap<>() {{
            put(1, Record.of(1, Distance.FIVE, 0, 15, 1));
            put(2, Record.of(2, Distance.FIVE, 0, 14, 59));
            put(3, Record.of(3, Distance.FIVE, 0, 14, 32));
            put(4, Record.of(4, Distance.FIVE, 0, 15, 3));
        }};
    }

    Optional<Record> getRecordById(int id) {
        Record record = records.get(id);
        return Optional.ofNullable(record);
    }

    Record saveRecord(Record.Validated validatedRecord) {
        int id = idGen.getAndIncrement();
        Record record = validatedRecord.toRecord(id);
        records.put(record.id, record);
        return record;
    }
}
