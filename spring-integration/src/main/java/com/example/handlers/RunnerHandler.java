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
package com.example.handlers;

import com.example.models.runners.Runner;
import com.example.models.runners.RunnerRepository;
import com.example.utils.RequestContext;
import io.servicetalk.concurrent.api.Single;
import io.servicetalk.http.api.HttpResponse;
import io.servicetalk.http.api.HttpSerializationProvider;
import io.servicetalk.serialization.api.TypeHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

public class RunnerHandler {

    private static final Logger logger = LoggerFactory.getLogger(RunnerHandler.class);

    private final RunnerRepository runnerRepository;
    private final HttpSerializationProvider serializationProvider;

    public RunnerHandler(RunnerRepository runnerRepository, HttpSerializationProvider serializationProvider) {
        this.runnerRepository = runnerRepository;
        this.serializationProvider = serializationProvider;
    }

    public Single<HttpResponse> getRunnerById(RequestContext context) {
        OptionalInt id = context.pathParamAsIntAtIndex(1);
        if (id.isEmpty()) {
            return Single.succeeded(notFound(context));
        }
        Optional<Runner> result = runnerRepository.findById(id.getAsInt());
        return Single.succeeded(result.map(runner -> success(context, runner))
                .orElseGet(() -> notFound(context)));
    }

    private HttpResponse success(RequestContext context, Runner runner) {
        logger.info("request: {}, found: {}", context.path(), runner);
        return context
                .response()
                .ok()
                .addHeader("content-type", "application/json")
                .payloadBody(runner, serializationProvider.serializerFor(Runner.class));
    }

    private HttpResponse notFound(RequestContext context) {
        logger.info("request: {}, message: not found", context.path());
        return context.response()
                .notFound()
                .addHeader("content-type", "application/json")
                .payloadBody(Map.of("messages", List.of("not found " + context.path())), serializationProvider.serializerFor(new TypeHolder<Map<? super String, ? super List<? super String>>>() {
                }));
    }
}
