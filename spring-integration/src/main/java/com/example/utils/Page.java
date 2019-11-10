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
package com.example.utils;

import java.util.List;

public class Page<T> {

    public final int currentPage;
    public final List<T> items;
    public final int pageSize;
    public final int maxCount;

    public static <T> Page<T> of(int currentPageIndex, List<T> items, int maxCount) {
        return new Page<>(currentPageIndex + 1, items, items.size(), maxCount);
    }

    private Page(int currentPage, List<T> items, int pageSize, int maxCount) {
        this.currentPage = currentPage;
        this.items = items;
        this.pageSize = pageSize;
        this.maxCount = maxCount;
    }
}
