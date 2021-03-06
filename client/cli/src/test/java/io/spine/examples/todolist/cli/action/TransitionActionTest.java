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

package io.spine.examples.todolist.cli.action;

import io.spine.examples.todolist.cli.Application;
import io.spine.examples.todolist.cli.Screen;
import io.spine.examples.todolist.cli.TestScreen;
import io.spine.examples.todolist.cli.view.NoOpView;
import io.spine.examples.todolist.cli.view.View;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TransitionAction should")
class TransitionActionTest {

    private static final String ACTION_NAME = "static transition action";
    private static final Shortcut SHORTCUT = new Shortcut("s");

    @BeforeAll
    static void setUp() {
        Application application = Application.instance();
        if (application.screen() == null) {
            application.init(new TestScreen());
        }
    }

    @Test
    @DisplayName("render of a destination view")
    void renderDestinationView() {
        View source = new NoOpView();
        DisplayCounterView destination = new DisplayCounterView();
        Action<View, DisplayCounterView> action =
                new TransitionAction<>(ACTION_NAME, SHORTCUT, source, destination);

        assertEquals(0, destination.displayedTimes);
        action.execute();
        assertEquals(1, destination.displayedTimes);
    }

    static class DisplayCounterView implements View {

        private int displayedTimes = 0;

        @Override
        public void render(Screen screen) {
            displayedTimes++;
        }
    }
}
