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

import com.example.util.Result;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.http.api.HttpRequest;
import io.servicetalk.http.api.HttpResponse;
import io.servicetalk.http.api.HttpResponseFactory;
import io.servicetalk.http.api.HttpSerializationProvider;
import io.servicetalk.serialization.api.TypeHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

class FrontendHandler {

    private static final Logger logger = LoggerFactory.getLogger(FrontendHandler.class);

    private static final TypeHolder<Map<String, String>> MAP_TYPE = new TypeHolder<>() {
    };

    private final HttpSerializationProvider provider;
    private final BackendService service;

    FrontendHandler(HttpSerializationProvider provider, BackendService service) {
        this.provider = provider;
        this.service = service;
    }

    Single<HttpResponse> handle(HttpRequest request, HttpResponseFactory factory) {
        String id = request.queryParameter("id");
        Optional<RecordId> recordId = RecordId.fromString(id);
        if (recordId.isEmpty()) {
            HttpResponse response = factory
                    .notFound()
                    .payloadBody(Map.of("message", String.format("not found record for id %s", id)), provider.serializerFor(MAP_TYPE))
                    .addHeader("content-type", "application/json");
            logger.info("invalid format error, id: {}", id);
            return Single.succeeded(response);
        }
        Single<Result<Record, String>> recordOrMessage = service.call(recordId.get());
        return recordOrMessage
                .whenOnSuccess(result -> logger.info("response from backend: {}", result))
                .map(recOrMes -> recOrMes
                        .transformSuccess(record -> factory
                                .ok()
                                .payloadBody(record, provider.serializerFor(Record.class)))
                        .transformFailure(message -> factory
                                .notFound()
                                .payloadBody(Map.of("message", message), provider.serializerFor(MAP_TYPE))))
                .map(response -> response.addHeader("content-type", "application/json"));
    }
}
