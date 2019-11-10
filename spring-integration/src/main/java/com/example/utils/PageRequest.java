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

import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public class PageRequest {

    public final int pageIndex;
    public final int pageSize;

    public PageRequest(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public static PageRequest getDefault() {
        return new PageRequest(0, 3);
    }

    public static Optional<PageRequest> defaultPageSizeWithPageIndex(int pageIndex) {
        if (pageIndex < 0) {
            return Optional.empty();
        }
        return Optional.of(new PageRequest(pageIndex, 3));
    }

    public static Optional<PageRequest> defaultPageIndexWithPageSize(int pageSize) {
        if ((pageSize <= 0)) {
            return Optional.empty();
        }
        return Optional.of(new PageRequest(0, pageSize));
    }

    public static Optional<PageRequest> of(int pageSize, int pageIndex) {
        if (0 < pageSize && 0 <= pageIndex) {
            return Optional.of(new PageRequest(pageIndex, pageSize));
        }
        return Optional.empty();
    }

    public long skippingCount() {
        return pageIndex * pageSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageRequest{");
        sb.append("pageIndex=").append(pageIndex);
        sb.append(", pageSize=").append(pageSize);
        sb.append('}');
        return sb.toString();
    }
}
