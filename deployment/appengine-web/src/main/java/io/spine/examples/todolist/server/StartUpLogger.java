/*
 * Copyright 2019, TeamDev. All rights reserved.
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

package io.spine.examples.todolist.server;

import io.spine.logging.Logging;
import org.slf4j.Logger;

import static java.lang.String.format;

/**
 * A logger facade to be used when logging the application start up process.
 */
final class StartUpLogger {

    private static final Logger logger = Logging.get(Application.class);
    private static final StartUpLogger instance = new StartUpLogger();

    /**
     * Prevents direct instantiation.
     */
    private StartUpLogger() {
    }

    static StartUpLogger instance() {
        return instance;
    }

    /**
     * Logs the given message on the {@code INFO} level.
     *
     * @param template
     *         the logged message template
     * @param arguments
     *         the message template arguments
     */
    void log(String template, Object... arguments) {
        String messageTemplate = format("Start up: %s", template);
        logger.debug(messageTemplate, arguments);
    }
}