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

package io.spine.examples.todolist.validator;

import javax.annotation.Nullable;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Serves as a validator for the user approve answer.
 *
 * <p>Validation will be passed when:
 * <li>input is `y`;
 * <li>input is `n`.
 * <p>In other cases validation will be failed.
 *
 * @author Illia Shepilov
 */
public class ApproveAnswerValidator implements Validator<String> {

    static final String NEGATIVE_ANSWER = "n";
    static final String POSITIVE_ANSWER = "y";
    static final String INVALID_INPUT_MSG =
            "Invalid input. Valid values: '" + POSITIVE_ANSWER + "' or '" + NEGATIVE_ANSWER + "'.";

    private String message;

    @Override
    public boolean validate(String input) {
        if (isNullOrEmpty(input)) {
            return false;
        }

        final boolean isNegativeOrPositiveAns =
                NEGATIVE_ANSWER.equals(input) || POSITIVE_ANSWER.equals(input);
        if (!isNegativeOrPositiveAns) {
            this.message = INVALID_INPUT_MSG;
            return false;
        }

        return true;
    }

    @Override
    @Nullable
    public String getMessage() {
        return message;
    }
}
