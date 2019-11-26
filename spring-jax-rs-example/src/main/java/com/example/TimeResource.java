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

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Path("/time")
public class TimeResource {

    private static final Logger logger = LoggerFactory.getLogger(TimeResource.class);

    private final DateTimeFormatter dateTimeFormatter;
    private final Clock clock;

    @Inject
    public TimeResource(DateTimeFormatter dateTimeFormatter, Clock clock) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.clock = clock;
    }

    @GET
    @Produces("application/json")
    public Response getTime() {
        OffsetDateTime now = OffsetDateTime.now(clock);
        Time time = new Time(clock.getZone().getId(), now.format(dateTimeFormatter));
        logger.info("new request: {}", time);
        return Response.ok(time).build();
    }

    @SuppressWarnings("WeakerAccess")
    public static class Time {
        public final String timezone;
        public final String time;

        public Time(String timezone, String time) {
            this.timezone = timezone;
            this.time = time;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Time{");
            sb.append("timezone='").append(timezone).append('\'');
            sb.append(", time='").append(time).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
