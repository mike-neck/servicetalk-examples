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

import io.servicetalk.http.api.HttpRequest;
import io.servicetalk.http.api.HttpResponseFactory;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RequestContext {

    private final HttpRequest request;
    private final HttpResponseFactory factory;

    public RequestContext(HttpRequest request, HttpResponseFactory factory) {
        this.request = request;
        this.factory = factory;
    }

    public HttpResponseFactory response() {
        return factory;
    }

    public String path() {
        return request.path();
    }

    @SuppressWarnings("WeakerAccess")
    public Optional<String> pathParamAtIndex(int index) {
        if (index < 0) {
            return Optional.empty();
        }
        String[] fragments = request.path().split("/");
        int idx = fragments[0].isEmpty()? index + 1: index;
        if (fragments.length <= idx) {
            return Optional.empty();
        }
        return Optional.of(fragments[idx]);
    }

    private static final Predicate<String> CAN_BE_NUMBER = Pattern.compile("^\\p{N}+$").asMatchPredicate();

    public OptionalInt pathParamAsIntAtIndex(int index) {
        return pathParamAtIndex(index)
                .filter(CAN_BE_NUMBER)
                .map(fragment -> OptionalInt.of(Integer.parseInt(fragment)))
                .orElseGet(OptionalInt::empty);
    }
}
