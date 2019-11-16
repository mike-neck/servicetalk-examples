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

import io.servicetalk.data.jackson.JacksonSerializationProvider;
import io.servicetalk.http.api.HttpSerializationProvider;
import io.servicetalk.http.api.HttpSerializationProviders;
import io.servicetalk.http.netty.HttpClients;
import io.servicetalk.http.netty.HttpServers;
import io.servicetalk.transport.api.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontendApp {

    private static final Logger logger = LoggerFactory.getLogger(FrontendApp.class);

    public static void main(String[] args) throws Exception {
        HttpSerializationProvider provider = HttpSerializationProviders.jsonSerializer(new JacksonSerializationProvider());
        BackendService backendService = new BackendService(provider, HttpClients.forMultiAddressUrl().build());
        FrontendHandler frontendHandler = new FrontendHandler(provider, backendService);
        ServerContext serverContext = HttpServers.forPort(8080)
                .listenAndAwait((ctx, request, responseFactory) -> frontendHandler.handle(request, responseFactory));
        logger.info("front end application start on http://localhost:8080");
        serverContext.awaitShutdown();
    }
}
