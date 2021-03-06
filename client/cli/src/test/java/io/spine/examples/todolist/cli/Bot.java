/*
 * Copyright 2021, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

package io.spine.examples.todolist.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * A bot for user input imitation.
 *
 * <p>Handles {@link Application} configuration and provides I/O related assertions.
 */
public final class Bot {

    private final TestScreen screen;

    /**
     * Creates a new bot, that uses {@link TestScreen}
     * {@linkplain Application#setScreen(Screen) injected} to the application.
     */
    public Bot() {
        this.screen = new TestScreen();
        Application.instance()
                   .setScreen(screen);
    }

    /**
     * Adds the specified answer to the {@link TestScreen}.
     *
     * @param answer
     *         the answer to add
     */
    public void addAnswer(String answer) {
        screen.addAnswer(answer);
    }

    /**
     * Asserts that expected and actual {@link TestScreen#getOutput() output} are equal.
     *
     * @param expected
     *         the expected output
     */
    public void assertOutput(String expected) {
        assertEquals(expected, screen.getOutput());
    }

    /**
     * Asserts that answers is empty.
     *
     * <p>Should be used to make sure that a user was
     * {@link Screen#promptUser(String) prompted} certain number of times.
     */
    public void assertAllAnswersWereGiven() {
        assertFalse(screen.hasAnswers());
    }

    /**
     * Obtains screen used by this bot.
     *
     * @return used screen
     */
    public Screen screen() {
        return screen;
    }
}
