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

package io.spine.examples.todolist.client.builder;

import io.spine.examples.todolist.tasks.TaskDescription;
import io.spine.examples.todolist.tasks.TaskId;
import io.spine.examples.todolist.tasks.command.CreateBasicTask;
import io.spine.examples.todolist.tasks.command.CreateDraft;

/**
 * Provides task command builders.
 */
public final class TaskBuilder {

    private TaskBuilder() {
    }

    static TaskBuilder newInstance() {
        return new TaskBuilder();
    }

    /**
     * Provides builder for the {@link CreateBasicTask} command.
     *
     * @return the {@link CreateBasicTaskBuilder} instance
     */
    public CreateBasicTaskBuilder createTask() {
        return new CreateBasicTaskBuilder();
    }

    /**
     * Provides builder for the {@link CreateDraft} command.
     *
     * @return the {@link CreateTaskDraftBuilder} instance
     */
    public CreateTaskDraftBuilder createDraft() {
        return new CreateTaskDraftBuilder();
    }

    /**
     * Builder for the {@link CreateBasicTask} command.
     */
    public static final class CreateBasicTaskBuilder {

        private final CreateBasicTask.Builder builder = CreateBasicTask.newBuilder();

        /**
         * Sets the description to the {@link CreateBasicTask.Builder}.
         *
         * @param description
         *         the description of the command
         * @return the {@code CreateBasicTaskBuilder} instance
         */
        public CreateBasicTaskBuilder setDescription(TaskDescription description) {
            builder.setDescription(description);
            return this;
        }

        /**
         * Builds the {@link CreateBasicTask} command.
         *
         * @return the {@code CreateBasicTask} command
         */
        public CreateBasicTask build() {
            builder.setId(TaskId.generate());
            return builder.vBuild();
        }
    }

    /**
     * Builder for the {@link CreateDraft} command.
     */
    public static final class CreateTaskDraftBuilder {

        private final CreateDraft.Builder builder = CreateDraft.newBuilder();

        /**
         * Builds the {@link CreateDraft} command.
         *
         * @return the {@code CreateDraft} command
         */
        public CreateDraft build() {
            TaskId id = TaskId.generate();
            builder.setId(id);
            return builder.vBuild();
        }
    }
}
