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

import com.example.utils.Page;
import com.example.utils.PageRequest;

import java.time.LocalDate;

public interface RecordRepository {

    Page<Record> findRecordByRunnerId(int runnerId, PageRequest pageRequest);

    Page<Record> findRecordByDateAndTimezone(LocalDate date, String timezone, PageRequest pageRequest);
}
