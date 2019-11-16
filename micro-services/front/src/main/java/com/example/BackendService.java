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

import com.example.util.ResponseHandler;
import com.example.util.Result;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.http.api.HttpClient;
import io.servicetalk.http.api.HttpRequest;
import io.servicetalk.http.api.HttpSerializationProvider;
import io.servicetalk.serialization.api.TypeHolder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BackendService implements AutoCloseable {

    private final HttpSerializationProvider provider;
    private final HttpClient httpClient;

    public BackendService(HttpSerializationProvider provider, HttpClient httpClient) {
        this.provider = provider;
        this.httpClient = httpClient;
    }

    Single<Result<Record, String>> call(RecordId id) {
        String path = String.format("/api/%d", id.value);
        HttpRequest httpRequest = httpClient.get("http://localhost:8000")
                .addHeader("accept", "application/json")
                .path(path);

        ResponseHandler<Record, ErrorMessages> handler = ResponseHandler.createBuilder(provider)
                .when(status -> 200 <= status && status < 300)
                .then(Record.class)
                .others(ErrorMessages.class);

        return httpClient.request(httpRequest)
                .map(handler::handleResponse)
                .map(result -> result.errorMap(ErrorMessages::inOneLine));
    }

    @Override
    public void close() throws Exception {
        httpClient.close();
    }
}
