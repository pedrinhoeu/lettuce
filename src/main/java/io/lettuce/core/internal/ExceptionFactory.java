/*
 * Copyright 2020-Present, Redis Ltd. and Contributors
 * All rights reserved.
 *
 * Licensed under the MIT License.
 *
 * This file contains contributions from third-party contributors
 * licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lettuce.core.internal;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import io.lettuce.core.RedisBusyException;
import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.RedisLoadingException;
import io.lettuce.core.RedisNoScriptException;
import io.lettuce.core.RedisReadOnlyException;

/**
 * Factory for Redis exceptions.
 *
 * @author Mark Paluch
 * @author Tobias Nehrlich
 * @since 4.5
 */
public abstract class ExceptionFactory {

    private static final DateTimeFormatter MINUTES = new DateTimeFormatterBuilder().appendText(ChronoField.MINUTE_OF_DAY)
            .appendLiteral(" minute(s)").toFormatter();

    private static final DateTimeFormatter SECONDS = new DateTimeFormatterBuilder().appendText(ChronoField.SECOND_OF_DAY)
            .appendLiteral(" second(s)").toFormatter();

    private static final DateTimeFormatter MILLISECONDS = new DateTimeFormatterBuilder().appendText(ChronoField.MILLI_OF_DAY)
            .appendLiteral(" millisecond(s)").toFormatter();

    private ExceptionFactory() {
    }

    /**
     * Create a {@link RedisCommandTimeoutException} with a detail message given the timeout.
     *
     * @param timeout the timeout value.
     * @return the {@link RedisCommandTimeoutException}.
     */
    public static RedisCommandTimeoutException createTimeoutException(Duration timeout) {
        return new RedisCommandTimeoutException(String.format("Command timed out after %s", formatTimeout(timeout)));
    }

    /**
     * Create a {@link RedisCommandTimeoutException} with a detail message given the message and timeout.
     *
     * @param message the detail message.
     * @param timeout the timeout value.
     * @return the {@link RedisCommandTimeoutException}.
     */
    public static RedisCommandTimeoutException createTimeoutException(String message, Duration timeout) {
        return new RedisCommandTimeoutException(
                String.format("%s. Command timed out after %s", message, formatTimeout(timeout)));
    }

    public static String formatTimeout(Duration duration) {

        if (duration.isZero()) {
            return "no timeout";
        }

        LocalTime time = LocalTime.MIDNIGHT.plus(duration);
        if (isExactMinutes(duration)) {
            return MINUTES.format(time);
        }

        if (duration.toMillis() % (1000) == 0 && duration.getNano() == 0) {
            return SECONDS.format(time);
        }

        if (isExactMillis(duration)) {
            return MILLISECONDS.format(time);
        }

        return String.format("%d ns", duration.toNanos());
    }

    private static boolean isExactMinutes(Duration duration) {
        return duration.toMillis() % (1000 * 60) == 0 && duration.getNano() == 0;
    }

    private static boolean isExactMillis(Duration duration) {
        return duration.toNanos() % (1000 * 1000) == 0;
    }

    /**
     * Create a {@link RedisCommandExecutionException} with a detail message. Specific Redis error messages may create subtypes
     * of {@link RedisCommandExecutionException}.
     *
     * @param message the detail message.
     * @return the {@link RedisCommandExecutionException}.
     */
    public static RedisCommandExecutionException createExecutionException(String message) {
        return createExecutionException(message, null);
    }

    /**
     * Create a {@link RedisCommandExecutionException} with a detail message and optionally a {@link Throwable cause}. Specific
     * Redis error messages may create subtypes of {@link RedisCommandExecutionException}.
     *
     * @param message the detail message.
     * @param cause the nested exception, may be {@code null}.
     * @return the {@link RedisCommandExecutionException}.
     */
    public static RedisCommandExecutionException createExecutionException(String message, Throwable cause) {

        if (message != null) {

            if (message.startsWith("BUSY")) {
                return cause != null ? new RedisBusyException(message, cause) : new RedisBusyException(message);
            }

            if (message.startsWith("NOSCRIPT")) {
                return cause != null ? new RedisNoScriptException(message, cause) : new RedisNoScriptException(message);
            }

            if (message.startsWith("LOADING")) {
                return cause != null ? new RedisLoadingException(message, cause) : new RedisLoadingException(message);
            }

            if (message.startsWith("READONLY")) {
                return cause != null ? new RedisReadOnlyException(message, cause) : new RedisReadOnlyException(message);
            }

            return cause != null ? new RedisCommandExecutionException(message, cause)
                    : new RedisCommandExecutionException(message);
        }

        return new RedisCommandExecutionException(cause);
    }

}
