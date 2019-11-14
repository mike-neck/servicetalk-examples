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

import java.util.function.Function;

public interface Result<L, R> {

    <T> Transform<T, L> onSuccess(Function<? super R, ? extends T> successMapping);

    <N> Result<L, N> map(Function<? super R, ? extends N> mapping);

    @FunctionalInterface
    interface Transform<T, L> {
        T onFailure(Function<? super L, ? extends T> failureMapping);
    }

    static <L, R> Result<L, R> success(R right) {
        return new Success<>(right);
    }

    static <L, R> Result<L,R> failure(L left) {
        return new Failure<>(left);
    }
}

class Success<L, R> implements Result<L, R> {
    final R right;

    Success(R right) {
        this.right = right;
    }

    @Override
    public <T> Transform<T, L> onSuccess(Function<? super R, ? extends T> successMapping) {
        return failureMapping -> successMapping.apply(right);
    }

    @Override
    public <N> Result<L, N> map(Function<? super R, ? extends N> mapping) {
        return Result.success(mapping.apply(right));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Success{");
        sb.append("right=").append(right);
        sb.append('}');
        return sb.toString();
    }
}

class Failure<L, R> implements Result<L, R> {
    final L left;

    Failure(L left) {
        this.left = left;
    }

    @Override
    public <T> Transform<T, L> onSuccess(Function<? super R, ? extends T> successMapping) {
        return failureMapping -> failureMapping.apply(left);
    }

    @Override
    public <N> Result<L, N> map(Function<? super R, ? extends N> mapping) {
        return Result.failure(left);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Failure{");
        sb.append("left=").append(left);
        sb.append('}');
        return sb.toString();
    }
}
