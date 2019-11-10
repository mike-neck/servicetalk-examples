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

import com.example.handlers.RunnerHandler;
import com.example.utils.Handler;
import io.servicetalk.http.api.HttpRequestMethod;
import io.servicetalk.http.api.StreamingHttpService;
import io.servicetalk.http.netty.HttpServers;
import io.servicetalk.http.router.predicate.HttpPredicateRouterBuilder;
import io.servicetalk.transport.api.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import java.util.regex.Pattern;

public class ServiceTalkRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTalkRunner.class);

    private final RunnerHandler runnerHandler;

    public ServiceTalkRunner(RunnerHandler runnerHandler) {
        this.runnerHandler = runnerHandler;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("starting service on http://localhost:8080");
        ServerContext serverContext = HttpServers.forPort(8080)
                .listenStreamingAndAwait(router());
        serverContext.awaitShutdown();
    }

    private StreamingHttpService router() {
        return new HttpPredicateRouterBuilder()
                .whenMethod(HttpRequestMethod.GET)
                .andPathMatches(Pattern.compile("/runner/\\p{N}+"))
                .thenRouteTo(Handler.create(runnerHandler::getRunnerById))
                .buildStreaming();
    }
}
