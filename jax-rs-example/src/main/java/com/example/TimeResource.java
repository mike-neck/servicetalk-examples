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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.Optional;

@Path("times")
public class TimeResource {

    private static final Logger logger = LoggerFactory.getLogger(TimeResource.class);

    private static final DateTimeFormatter FORMATTER =
            new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendOffsetId()
            .toFormatter();

    @GET
    @Produces("application/json")
    public Response getTime(@QueryParam("timezone") String timezone) {
        logger.info("receive request timezone: {}", timezone);
        return extractTimezone(timezone)
                .map(tz -> new Time(
                        FORMATTER.format(OffsetDateTime.now(tz)),
                        tz.getId()))
                .map(time -> Response.ok(time, MediaType.APPLICATION_JSON_TYPE)
                        .build())
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("message", "Bad timezone: " + timezone))
                        .build());
    }

    private static Optional<ZoneId> extractTimezone(String timezone) {
        if (timezone == null || timezone.isBlank() || timezone.isEmpty()) {
            return Optional.of(ZoneOffset.UTC);
        }
        try {
            return Optional.ofNullable(ZoneId.of(timezone));
        } catch (DateTimeException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class Time {
        public final String time;
        public final String timeZone;

        public Time(String time, String timeZone) {
            this.time = time;
            this.timeZone = timeZone;
        }
    }
}
