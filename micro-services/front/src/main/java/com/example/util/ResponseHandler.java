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
package com.example.util;

import io.servicetalk.http.api.HttpResponse;
import io.servicetalk.http.api.HttpSerializationProvider;
import io.servicetalk.serialization.api.TypeHolder;

import java.util.Optional;
import java.util.function.IntPredicate;

public class ResponseHandler<S, F> {

    private final SingleResponseHandler<S> successMapping;
    private final SingleResponseHandler<F> failureMapping;

    private ResponseHandler(SingleResponseHandler<S> successMapping, SingleResponseHandler<F> failureMapping) {
        this.successMapping = successMapping;
        this.failureMapping = failureMapping;
    }

    public Result<S, F> handleResponse(HttpResponse response) {
        Optional<S> result = successMapping.transform(response);
        return result.map(Result::<S, F>succeeded)
                .or(() -> failureMapping.transform(response).map(Result::failed))
                .orElseThrow(() -> {
                    String message = String.format("unexpected response status, %s", response.status());
                    return new IllegalStateException(message);
                });
    }

    public static Builder createBuilder(HttpSerializationProvider provider) {
        return successCondition -> new BuilderOfSuccess() {
            @Override
            public HttpSerializationProvider provider() {
                return provider;
            }

            @Override
            public <S> BuilderOfFailure<S> then(Transform<S> successMapping) {
                return new BuilderOfFailure<>() {
                    @Override
                    public HttpSerializationProvider provider() {
                        return provider;
                    }

                    @Override
                    public <F> ResponseHandler<S, F> others(Transform<F> failureMapping) {
                        return new ResponseHandler<>(
                                new SingleResponseHandler<>(successCondition, successMapping),
                                new SingleResponseHandler<>(successCondition.negate(), failureMapping));
                    }
                };
            }
        };
    }

    public interface Builder {
        BuilderOfSuccess when(IntPredicate successCondition);
    }

    public interface BuilderOfSuccess {

        default <S> BuilderOfFailure<S> then(Class<S> klass) {
            return then(Transform.create(provider(), klass));
        }

        default <S> BuilderOfFailure<S> then(TypeHolder<S> type) {
            return then(Transform.create(provider(), type));
        }

        HttpSerializationProvider provider();

        <S> BuilderOfFailure<S> then(Transform<S> successMapping);
    }

    public interface BuilderOfFailure<S> {

        default <F> ResponseHandler<S, F> others(Class<F> klass) {
            return others(Transform.create(provider(), klass));
        }

        default <F> ResponseHandler<S, F> others(TypeHolder<F> type) {
            return others(Transform.create(provider(), type));
        }

        HttpSerializationProvider provider();

        <F> ResponseHandler<S, F> others(Transform<F> failureMapping);
    }

    private static class SingleResponseHandler<T> {
        private final IntPredicate expectedStatus;
        private final Transform<T> transform;

        private SingleResponseHandler(IntPredicate expectedStatus, Transform<T> transform) {
            this.expectedStatus = expectedStatus;
            this.transform = transform;
        }

        boolean canBeApplyTo(HttpResponse response) {
            return expectedStatus.test(response.status().code());
        }

        Optional<T> transform(HttpResponse response) {
            if (canBeApplyTo(response)) {
                return Optional.ofNullable(transform.toPayloadBody(response));
            }
            return Optional.empty();
        }
    }

    interface Transform<T> {
        T toPayloadBody(HttpResponse response);

        static <T> Transform<T> create(HttpSerializationProvider provider, Class<T> klass) {
            return response -> response.payloadBody(provider.deserializerFor(klass));
        }

        static <T> Transform<T> create(HttpSerializationProvider provider, TypeHolder<T> typeHolder) {
            return response -> response.payloadBody(provider.deserializerFor(typeHolder));
        }
    }
}
