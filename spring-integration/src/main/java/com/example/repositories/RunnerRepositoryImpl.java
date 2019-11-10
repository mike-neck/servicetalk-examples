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

import com.example.models.runners.Runner;
import com.example.models.runners.RunnerRepository;
import com.example.utils.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RunnerRepositoryImpl implements RunnerRepository {

    private final List<Runner> runners = List.of(
            new Runner(1, "Jonathan Joestar"),
            new Runner(2, "Dio Brando"),
            new Runner(3, "Robert Speedwagon"),
            new Runner(4, "William Anthonio Zeppeli")
    );

    @Override
    public Optional<Runner> findById(int id) {
        return runners.stream()
                .filter(runner -> runner.id == id)
                .findAny();
    }

    @Override
    public Page<Runner> findByNameContaining(int pageSize, int pageIndex, String nameFragment) {
        List<Runner> all = runners.stream()
                .filter(runner -> runner.name.contains(nameFragment))
                .collect(Collectors.toUnmodifiableList());
        List<Runner> list = all.stream()
                .skip(pageSize * pageIndex)
                .collect(Collectors.toUnmodifiableList());
        return Page.of(pageIndex, list, all.size());
    }
}
