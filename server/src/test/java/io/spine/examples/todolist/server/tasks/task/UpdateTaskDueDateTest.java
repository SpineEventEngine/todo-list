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

import com.google.protobuf.Timestamp;
import io.spine.examples.todolist.tasks.command.CompleteTask;
import io.spine.examples.todolist.tasks.command.CreateBasicTask;
import io.spine.examples.todolist.tasks.command.UpdateTaskDueDate;
import io.spine.examples.todolist.tasks.event.TaskDueDateUpdated;
import io.spine.examples.todolist.tasks.rejection.Rejections;
import io.spine.examples.todolist.tasks.view.TaskView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.base.Time.currentTime;
import static io.spine.examples.todolist.testdata.TestTaskCommandFactory.completeTaskInstance;
import static io.spine.examples.todolist.testdata.TestTaskCommandFactory.createTaskInstance;
import static io.spine.examples.todolist.testdata.TestTaskCommandFactory.updateTaskDueDateInstance;

@DisplayName("UpdateTaskDueDate command should be interpreted by TaskPart and")
class UpdateTaskDueDateTest extends TaskCommandTestBase {

    @Test
    @DisplayName("produce TaskDueDateUpdated event")
    void produceEvent() {
        CreateBasicTask createTask = createTaskInstance(taskId());
        UpdateTaskDueDate updateTaskDueDate = updateTaskDueDateInstance(taskId());

        context().receivesCommand(createTask)
                 .receivesCommand(updateTaskDueDate)
                 .assertEvents()
                 .withType(TaskDueDateUpdated.class)
                 .hasSize(1);
    }

    @Test
    @DisplayName("update the task due date")
    void updateDueDate() {
        CreateBasicTask createTask = createTaskInstance(taskId());
        UpdateTaskDueDate updateTaskDueDate = updateTaskDueDateInstance(taskId());

        TaskView expected = TaskView
                .newBuilder()
                .setId(taskId())
                .setDueDate(updateTaskDueDate.getDueDateChange()
                                             .getNewValue())
                .vBuild();
        isEqualToExpectedAfterReceiving(expected, createTask, updateTaskDueDate);
    }

    @Test
    @DisplayName("throw CannotUpdateTaskDueDate rejection " +
            "upon an attempt to update the due date of the completed task")
    void cannotUpdateCompletedTaskDueDate() {
        CreateBasicTask createTask = createTaskInstance(taskId());
        CompleteTask completeTask = completeTaskInstance(taskId());
        UpdateTaskDueDate updateTaskDueDate = updateTaskDueDateInstance(taskId());

        context().receivesCommand(createTask)
                 .receivesCommand(completeTask)
                 .receivesCommand(updateTaskDueDate)
                 .assertEvents()
                 .withType(Rejections.CannotUpdateTaskDueDate.class)
                 .hasSize(1);
    }

    @Test
    @DisplayName("throw CannotUpdateTaskDueDate rejection " +
            "upon an attempt to update the due date of the deleted task")
    void cannotUpdateDeletedTaskDueDate() {
        CreateBasicTask createTask = createTaskInstance(taskId());
        CompleteTask completeTask = completeTaskInstance(taskId());
        UpdateTaskDueDate updateTaskDueDate = updateTaskDueDateInstance(taskId());

        context().receivesCommand(createTask)
                 .receivesCommand(completeTask)
                 .receivesCommand(updateTaskDueDate)
                 .assertEvents()
                 .withType(Rejections.CannotUpdateTaskDueDate.class)
                 .hasSize(1);
    }

    @Test
    @DisplayName("produce CannotUpdateTaskDueDate rejection")
    void produceRejection() {
        CreateBasicTask createTask = createTaskInstance();
        Timestamp expectedDueDate = currentTime();
        Timestamp newDueDate = currentTime();

        UpdateTaskDueDate updateTaskDueDate =
                updateTaskDueDateInstance(taskId(), expectedDueDate, newDueDate);

        context()
                .receivesCommand(createTask)
                .receivesCommand(updateTaskDueDate)
                .assertEvents()
                .withType(Rejections.CannotUpdateTaskDueDate.class)
                .hasSize(1);
    }

}
