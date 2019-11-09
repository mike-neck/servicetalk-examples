/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.example;

import io.servicetalk.concurrent.api.Single;
import io.servicetalk.http.api.HttpRequest;
import io.servicetalk.http.api.HttpResponse;
import io.servicetalk.http.api.HttpResponseFactory;
import io.servicetalk.http.netty.HttpServers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.servicetalk.http.api.HttpSerializationProviders.textSerializer;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        HttpServers.forPort(8080)
                .listenAndAwait((ctx, request, responseFactory) ->
                        hello(request, responseFactory))
                .awaitShutdown();
    }

    private static Single<HttpResponse> hello(HttpRequest request, HttpResponseFactory responseFactory) {
        Iterable<String> names = request.queryParameters("name");
        String message = StreamSupport.stream(names.spliterator(), false)
                .collect(Collectors.joining(", ", "Hello, ", "."));
        logger.info("message: {}", message);
        return Single.succeeded(
                responseFactory
                        .ok()
                        .setHeader("Content-Type", "plain/text")
                        .payloadBody(message, textSerializer()));
    }
}
