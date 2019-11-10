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
package com.example.repositories;

import com.example.models.records.Distance;
import com.example.models.records.Record;
import com.example.models.records.RecordRepository;
import com.example.utils.Page;
import com.example.utils.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecordRepositoryImpl implements RecordRepository {

    private final List<Record> records;

    public RecordRepositoryImpl() {
        List<Record> list = new ArrayList<>();
        int[] id = new int[]{ 1 };

        LocalDate day1 = LocalDate.of(1880, 10, 12);
        //noinspection CollectionAddAllCanBeReplacedWithConstructor
        list.addAll(List.of(
                new Record(id[0]++, 1, day1, "GMT", Distance.FIVE_KILO_METRE, 15 * 60 + 32),
                new Record(id[0]++, 2, day1, "GMT", Distance.FIVE_KILO_METRE, 15 * 60 + 49)
        ));

        LocalDate day2 = LocalDate.of(1888, 12, 4);
        list.addAll(List.of(
                new Record(id[0]++, 1, day2, "GMT", Distance.FIVE_KILO_METRE, 14 * 60 + 58),
                new Record(id[0]++, 2, day2, "GMT", Distance.FIVE_KILO_METRE, 14 * 60 + 42),
                new Record(id[0]++, 3, day2, "GMT", Distance.FIVE_KILO_METRE, 15 * 60 + 2)
        ));

        LocalDate day3 = LocalDate.of(1889, 1, 5);
        list.addAll(List.of(
                new Record(id[0]++, 1, day3, "GMT", Distance.FIVE_KILO_METRE, 13 * 60 + 51),
                new Record(id[0]++, 2, day3, "GMT", Distance.FIVE_KILO_METRE, 14 * 60 + 8),
                new Record(id[0]++, 3, day3, "GMT", Distance.FIVE_KILO_METRE, 14 * 60 + 37),
                new Record(id[0]++, 4, day3, "GMT", Distance.FIVE_KILO_METRE, 14 * 60 + 17)
        ));

        LocalDate day4 = LocalDate.of(1889, 2, 4);
        list.addAll(List.of(
                new Record(id[0]++, 1, day4, "GMT", Distance.FIVE_KILO_METRE, 13 * 60 + 55),
                new Record(id[0]++, 2, day4, "GMT", Distance.FIVE_KILO_METRE, 13 * 60 + 47)
        ));

        this.records = list;
    }

    @Override
    public Page<Record> findRecordByRunnerId(int runnerId, PageRequest pageRequest) {
        List<Record> all = records.stream()
                .filter(record -> record.runnerId == runnerId)
                .collect(Collectors.toUnmodifiableList());
        List<Record> list = all.stream()
                .skip(pageRequest.skippingCount())
                .collect(Collectors.toUnmodifiableList());
        return Page.of(pageRequest.pageIndex, list, all.size());
    }

    @Override
    public Page<Record> findRecordByDateAndTimezone(LocalDate date, String timezone, PageRequest pageRequest) {
        List<Record> all = records.stream()
                .filter(record -> record.date.equals(date) && record.timezone.equals(timezone))
                .collect(Collectors.toUnmodifiableList());
        List<Record> list = all.stream()
                .skip(pageRequest.skippingCount())
                .collect(Collectors.toUnmodifiableList());
        return Page.of(pageRequest.pageIndex, list, all.size());
    }
}
