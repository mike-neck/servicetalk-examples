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

import io.servicetalk.buffer.api.Buffer;
import io.servicetalk.serialization.api.SerializationProvider;
import io.servicetalk.serialization.api.StreamingDeserializer;
import io.servicetalk.serialization.api.StreamingSerializer;
import io.servicetalk.serialization.api.TypeHolder;
import org.jetbrains.annotations.NotNull;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

public class ThymeleafSerializationProvider implements SerializationProvider {

    static SerializationProvider create() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver(Thread.currentThread().getContextClassLoader());
        templateResolver.setPrefix("/public/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);

        return new ThymeleafSerializationProvider(engine);
    }

    private final TemplateEngine engine;

    private ThymeleafSerializationProvider(TemplateEngine engine) {
        this.engine = engine;
    }

    /**
     * 
     * @param classToSerialize - should be {@link View} class or a subclass of {@link View}.
     * @param <T> - a subclass of {@link View}
     * @return - {@link StreamingSerializer}
     */
    @NotNull
    @Override
    public <T> StreamingSerializer getSerializer(@NotNull Class<T> classToSerialize) {
        assert View.class.isAssignableFrom(classToSerialize): "invalid class";
        return serializer();
    }

    @NotNull
    private StreamingSerializer serializer() {
        return (toSerialize, destination) -> {
            if (!(Objects.requireNonNull(toSerialize, "expected to be not null") instanceof View)) {
                throw new IllegalArgumentException(
                        "expected to be instance of View, but it's instance of " + toSerialize.getClass().getSimpleName());
            }
            View view = (View) toSerialize;
            BufferWriter writer = new BufferWriter(destination);
            Context context = new Context(Locale.JAPAN, view.model);
            engine.process(view.template, context, writer);
        };
    }

    @NotNull
    @Override
    public <T> StreamingSerializer getSerializer(@NotNull TypeHolder<T> typeToSerialize) {
        String typeName = typeToSerialize.type().getTypeName();
        if (!typeName.endsWith(View.class.getSimpleName())) {
            throw new IllegalArgumentException("unsupported type: " + typeName);
        }
        return serializer();
    }

    @NotNull
    @Override
    public <T> StreamingDeserializer<T> getDeserializer(@NotNull Class<T> classToDeSerialize) {
        return new UnsupportedDeserializer<>();
    }

    @NotNull
    @Override
    public <T> StreamingDeserializer<T> getDeserializer(@NotNull TypeHolder<T> typeToDeserialize) {
        return new UnsupportedDeserializer<>();
    }

    private static class UnsupportedDeserializer<T> implements StreamingDeserializer<T> {
        @NotNull
        @Override
        public Iterable<T> deserialize(@NotNull Buffer toDeserialize) {
            throw new UnsupportedOperationException("this deserializer does not support deserialize.");
        }

        @Override
        public boolean hasData() {
            throw new UnsupportedOperationException("this deserializer does not support deserialize.");
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException("this deserializer does not support deserialize.");
        }
    }

    static class BufferWriter extends Writer {

        final Buffer buffer;

        BufferWriter(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
            CharBuffer ch = CharBuffer.wrap(cbuf, off, len);
            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(ch);
            buffer.writeBytes(byteBuffer);
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }
}
