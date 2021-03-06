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

package io.spine.examples.todolist.server.tasks.task;

import io.spine.base.CommandMessage;
import io.spine.examples.todolist.server.tasks.TasksContextFactory;
import io.spine.examples.todolist.tasks.TaskId;
import io.spine.examples.todolist.tasks.view.TaskView;
import io.spine.server.BoundedContextBuilder;
import io.spine.testing.server.blackbox.ContextAwareTest;
import org.junit.jupiter.api.BeforeEach;

/**
 * An abstract base for all commands that are related to tasks.
 *
 * <p>Contains a task ID, which identifies a task under tests.
 *
 * <p>Provides a way to send commands and check whether the entity state is equal to the
 * expected result.
 */
class TaskCommandTestBase extends ContextAwareTest {

    private TaskId taskId;

    @Override
    protected BoundedContextBuilder contextBuilder() {
        return TasksContextFactory.builder();
    }

    @BeforeEach
    void setUp() {
        taskId = TaskId.generate();
    }

    /**
     * Obtains the ID of the task that is used during the test. The returned ID changes before
     * every test method.
     */
    public TaskId taskId() {
        return taskId;
    }

    /**
     * Posts all of the specified events into the bounded context and checks whether the actual
     * {@code TaskView} entity state is equal to the specified one.
     *
     * @param expected
     *         task view against which the actual entity is matched for equality
     * @param commandMessages
     *         commands to post to the bounded context
     */
    void isEqualToExpectedAfterReceiving(TaskView expected, CommandMessage... commandMessages) {
        for (CommandMessage command : commandMessages) {
            context().receivesCommand(command);
        }
        context().assertEntity(taskId, TaskViewProjection.class)
                 .hasStateThat()
                 .comparingExpectedFieldsOnly()
                 .isEqualTo(expected);
    }
}
