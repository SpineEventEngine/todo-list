/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.examples.todolist.view;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.google.protobuf.util.Timestamps.toMillis;

/**
 * Formats a date into a user-friendly representation.
 *
 * @author Illia Shepilov
 */
final class DateFormatter {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @VisibleForTesting
    static final String DEFAULT_TIMESTAMP_VALUE = "default";

    /**
     * The {@code private} constructor prevents the utility class instantiation.
     */
    private DateFormatter() {
    }

    static String format(Timestamp timestamp) {
        final long millis = toMillis(timestamp);
        return millis == 0
               ? DEFAULT_TIMESTAMP_VALUE
               : getDateFormat().format(new Date(millis));
    }

    @VisibleForTesting
    static SimpleDateFormat getDateFormat() {
        final SimpleDateFormat result = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return result;
    }
}
