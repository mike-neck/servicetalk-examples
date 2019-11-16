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

import java.util.function.Function;

public interface Result<S, F> {

    <R> Result<R, F> map(Function<? super S, ? extends R> mapping);

    <R> Result<S, R> errorMap(Function<? super F, ? extends R> mapping);

    <R> Transform<R, F> transformSuccess(Function<? super S, ? extends R> successMapping);

    interface Transform<R, F> {
        R transformFailure(Function<? super F, ? extends R> errorMapping);
    }

    static <S, F> Result<S, F> succeeded(S value) {
        return new Success<>(value);
    }

    static <S, F> Result<S, F> failed(F value) {
        return new Failure<>(value);
    }
}

class Success<S, F> implements Result<S, F> {

    final S value;

    Success(S value) {
        this.value = value;
    }

    @Override
    public <R> Result<R, F> map(Function<? super S, ? extends R> mapping) {
        return new Success<>(mapping.apply(value));
    }

    @Override
    public <R> Result<S, R> errorMap(Function<? super F, ? extends R> mapping) {
        return new Success<>(value);
    }

    @Override
    public <R> Transform<R, F> transformSuccess(Function<? super S, ? extends R> successMapping) {
        return errorMapping -> successMapping.apply(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Success{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}

class Failure<S, F> implements Result<S, F> {

    final F value;

    Failure(F value) {
        this.value = value;
    }

    @Override
    public <R> Result<R, F> map(Function<? super S, ? extends R> mapping) {
        return new Failure<>(value);
    }

    @Override
    public <R> Result<S, R> errorMap(Function<? super F, ? extends R> mapping) {
        return new Failure<>(mapping.apply(value));
    }

    @Override
    public <R> Transform<R, F> transformSuccess(Function<? super S, ? extends R> successMapping) {
        return errorMapping -> errorMapping.apply(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Failure{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
