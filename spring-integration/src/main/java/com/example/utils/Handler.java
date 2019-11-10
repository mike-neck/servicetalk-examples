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
package com.example.utils;

import io.servicetalk.concurrent.api.Single;
import io.servicetalk.http.api.*;

import java.util.function.Function;

public interface Handler extends HttpService {

    Single<HttpResponse> handle(RequestContext context);

    @Override
    default Single<HttpResponse> handle(HttpServiceContext ctx, HttpRequest request, HttpResponseFactory responseFactory) {
        RequestContext context = new RequestContext(request, responseFactory);
        return handle(context);
    }

    static Handler create(Function<? super RequestContext, ? extends Single<HttpResponse>> handler) {
        return handler::apply;
    }
}
