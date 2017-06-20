/*
 * Copyright 2017, TeamDev Ltd. All rights reserved.
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

package io.spine.examples.todolist.mode.command;

import com.google.common.annotations.VisibleForTesting;
import io.spine.base.FieldPath;
import io.spine.validate.ConstraintViolation;
import io.spine.validate.ValidationException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Utilities for {@code ValidationException} formatting.
 *
 * @author Dmytro Grankin
 */
class ValidationExceptionFormatter {

    @VisibleForTesting
    static final String ERROR_MSG_FORMAT = "Invalid `%s`.";

    private ValidationExceptionFormatter() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Obtains a formatted representation of the specified {@code ValidationException}.
     *
     * <p>The representation tells about a name of an invalid field.
     *
     * <p>If the specified {@code ValidationException} has two
     * or more {@linkplain ConstraintViolation constraint violations},
     * throws {@code IllegalArgumentException}.
     *
     * @param e the {@code ValidationException}
     * @return a formatted string representation
     */
    static String format(ValidationException e) {
        checkArgument(e.getConstraintViolations()
                       .size() == 1);
        final ConstraintViolation violation = e.getConstraintViolations()
                                               .get(0);
        final FieldPath fieldPath = violation.getFieldPath();
        final int fieldPathSize = fieldPath.getFieldNameCount();
        final String unqualifiedName = fieldPath.getFieldName(fieldPathSize - 1);
        return String.format(ERROR_MSG_FORMAT, unqualifiedName);
    }
}