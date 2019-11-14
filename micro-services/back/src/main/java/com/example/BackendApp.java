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

import com.example.json.Record;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.data.jackson.JacksonSerializationProvider;
import io.servicetalk.http.api.*;
import io.servicetalk.http.netty.HttpServers;
import io.servicetalk.http.router.predicate.HttpPredicateRouterBuilder;
import io.servicetalk.serialization.api.TypeHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class BackendApp {

    private static final Logger logger = LoggerFactory.getLogger(BackendApp.class);

    final BackendController controller;
    final HttpSerializationProvider serializationProvider;

    BackendApp(BackendController controller) {
        this.controller = controller;
        this.serializationProvider =
                HttpSerializationProviders.jsonSerializer(new JacksonSerializationProvider());
    }

    public static void main(String[] args) throws Exception {
        BackendController controller = new BackendController(new BackendService());
        BackendApp app = new BackendApp(controller);
        HttpServers.forPort(8000)
                .listenStreamingAndAwait(app.router())
                .awaitShutdown();
    }

    StreamingHttpService router() {
        return new HttpPredicateRouterBuilder()
                .whenMethod(HttpRequestMethod.GET)
                .andPathMatches(Pattern.compile("/api/\\p{N}+"))
                .thenRouteTo(this::get)
                .whenMethod(HttpRequestMethod.POST)
                .andPathMatches(Pattern.compile("/api"))
                .thenRouteTo(this::post)
                .buildStreaming();
    }

    Single<HttpResponse> get(HttpServiceContext ctx, HttpRequest request, HttpResponseFactory responseFactory) {
        Request req = new Request(request);
        Optional<Record> record = req.pathAtIndex(1)
                .flatMap(this::parseInt)
                .flatMap(controller::get);
        if (record.isEmpty()) {
            logger.info("path: {}, result: fail, message: request-not-found", req.path());
            return Single.succeeded(responseFactory.notFound()
                    .addHeader("content-type", "application/json")
                    .payloadBody(Map.of("message", List.of("not found")), serializationProvider.serializerFor(new TypeHolder<>() {
            })));
        }
        HttpSerializer<Record> serializer = serializationProvider.serializerFor(Record.class);
        HttpResponse response = responseFactory.ok()
                .addHeader("Content-Type", "application/json")
                .payloadBody(record.get(), serializer);
        return Single.succeeded(response);
    }

    Optional<Integer> parseInt(String value) {
        try {
            Integer integer = Integer.valueOf(value);
            return Optional.of(integer);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    Single<HttpResponse> post(HttpServiceContext ctx, HttpRequest request, HttpResponseFactory responseFactory) {
        HttpDeserializer<Record.Entry> deserializer = serializationProvider.deserializerFor(Record.Entry.class);
        Record.Entry entry = request.payloadBody(deserializer);
        Result<Record.ErrorEntry, Record> result = controller.post(entry);

        HttpSerializer<Record> serializer = serializationProvider.serializerFor(Record.class);
        HttpResponse response = result.onSuccess(record -> responseFactory.created().addHeader("content-type", "application/json").payloadBody(record, serializer))
                .onFailure(errorEntry -> responseFactory.badRequest().payloadBody(Map.of("messages", errorEntry.messages), serializationProvider.serializerFor(new TypeHolder<>() {
                })));
        return Single.succeeded(response);
    }

    static class Request {
        final HttpRequest request;

        Request(HttpRequest request) {
            this.request = request;
        }

        String path() {
            return request.path();
        }

        Optional<String> pathAtIndex(int index) {
            String path = request.path();
            int idx = path.startsWith("/") ? index + 1 : index;
            String[] fragments = path.split("/");
            if (idx < 0 || fragments.length <= idx) {
                return Optional.empty();
            }
            return Optional.ofNullable(fragments[idx]);
        }
    }
}
