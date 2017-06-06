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

package io.spine.examples.todolist.c.aggregate;

import com.google.protobuf.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import io.spine.base.Command;
import io.spine.envelope.CommandEnvelope;
import io.spine.examples.todolist.LabelId;
import io.spine.examples.todolist.TaskId;
import io.spine.examples.todolist.c.commands.AssignLabelToTask;
import io.spine.examples.todolist.c.commands.CompleteTask;
import io.spine.examples.todolist.c.commands.CreateBasicLabel;
import io.spine.examples.todolist.c.commands.CreateBasicTask;
import io.spine.examples.todolist.c.commands.DeleteTask;
import io.spine.examples.todolist.c.commands.RemoveLabelFromTask;
import io.spine.examples.todolist.c.events.LabelAssignedToTask;
import io.spine.examples.todolist.c.events.LabelRemovedFromTask;
import io.spine.examples.todolist.c.failures.CannotAssignLabelToTask;
import io.spine.examples.todolist.c.failures.CannotRemoveLabelFromTask;
import io.spine.examples.todolist.context.TodoListBoundedContext;
import io.spine.examples.todolist.testdata.TestResponseObserver;
import io.spine.server.BoundedContext;
import io.spine.server.commandbus.CommandBus;
import io.spine.test.AggregatePartCommandTest;

import java.util.List;

import static io.spine.server.aggregate.AggregateCommandDispatcher.dispatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static io.spine.base.Identifiers.newUuid;
import static io.spine.examples.todolist.testdata.TestLabelCommandFactory.createLabelInstance;
import static io.spine.examples.todolist.testdata.TestTaskCommandFactory.DESCRIPTION;
import static io.spine.examples.todolist.testdata.TestTaskCommandFactory.completeTaskInstance;
import static io.spine.examples.todolist.testdata.TestTaskCommandFactory.createTaskInstance;
import static io.spine.examples.todolist.testdata.TestTaskCommandFactory.deleteTaskInstance;
import static io.spine.examples.todolist.testdata.TestTaskLabelsCommandFactory.assignLabelToTaskInstance;
import static io.spine.examples.todolist.testdata.TestTaskLabelsCommandFactory.removeLabelFromTaskInstance;

/**
 * @author Illia Shepilov
 */
class TaskLabelsPartTest {

    @Nested
    @DisplayName("AssignLabelToTask command should be interpreted by TaskLabelsPart and")
    class AssignLabelToTaskCommand extends TaskLabelsCommandTest<AssignLabelToTask> {

        @BeforeEach
        @Override
        protected void setUp() {
            super.setUp();
            final AssignLabelToTask assignLabelToTask = assignLabelToTaskInstance(taskId, labelId);
            createCommand(assignLabelToTask);
        }

        @Test
        @DisplayName("produce LabelAssignedToTask event")
        void produceEvent() {
            final List<? extends Message> messageList =
                    dispatch(taskLabelsPart, CommandEnvelope.of(command().get()));

            assertEquals(1, messageList.size());
            assertEquals(LabelAssignedToTask.class, messageList.get(0)
                                                               .getClass());
            final LabelAssignedToTask labelAssignedToTask =
                    (LabelAssignedToTask) messageList.get(0);

            assertEquals(taskId, labelAssignedToTask.getTaskId());
            assertEquals(labelId, labelAssignedToTask.getLabelId());
        }

        @Test
        @DisplayName("throw CannotAssignLabelToTask failure " +
                "upon an attempt to assign the label to the deleted task")
        void cannotAssignLabelToDeletedTask() {
            createBasicTask();
            deleteTask();

            assertThrows(CannotAssignLabelToTask.class,
                         () -> taskLabelsPart.handle(commandMessage().get(),
                                                     commandContext().get()));
        }

        @Test
        @DisplayName("throw CannotAssignLabelToTask failure " +
                "upon an attempt to assign the label to the completed task")
        void cannotAssignLabelToCompletedTask() {
            createBasicTask();
            completeTask();

            assertThrows(CannotAssignLabelToTask.class,
                         () -> taskLabelsPart.handle(commandMessage().get(),
                                                     commandContext().get()));
        }
    }

    @Nested
    @DisplayName("RemoveLabelFromTask command should be interpreted by TaskLabelsPart and")
    class RemoveLabelFromTaskCommand extends TaskLabelsCommandTest<RemoveLabelFromTask> {

        @BeforeEach
        @Override
        protected void setUp() {
            super.setUp();
            final RemoveLabelFromTask removeLabelFromTask = removeLabelFromTaskInstance(taskId,
                                                                                        labelId);
            createCommand(removeLabelFromTask);
        }

        @Test
        @DisplayName("produce LabelRemovedFromTask event")
        void produceEvent() throws CannotRemoveLabelFromTask {
            createBasicTask();
            dispatchAssignLabelToTask();

            final List<? extends Message> messageList =
                    taskLabelsPart.handle(commandMessage().get(), commandContext().get());

            assertEquals(1, messageList.size());
            assertEquals(LabelRemovedFromTask.class, messageList.get(0)
                                                                .getClass());
            final LabelRemovedFromTask labelRemovedFromTask =
                    (LabelRemovedFromTask) messageList.get(0);

            assertEquals(taskId, labelRemovedFromTask.getTaskId());
            assertEquals(labelId, labelRemovedFromTask.getLabelId());
        }

        @Test
        @DisplayName("throw CannotRemoveLabelFromTask failure " +
                "upon an attempt to remove the not assigned label")
        void cannotRemoveNotExistingLabel() {
            assertThrows(CannotRemoveLabelFromTask.class,
                         () -> taskLabelsPart.handle(commandMessage().get(),
                                                     commandContext().get()));
        }

        @Test
        @DisplayName("throw CannotRemoveLabelFromTask failure " +
                "upon an attempt to remove the label from the completed task")
        void cannotRemoveLabelFromCompletedTask() {
            createBasicTask();
            completeTask();

            assertThrows(CannotRemoveLabelFromTask.class,
                         () -> taskLabelsPart.handle(commandMessage().get(),
                                                     commandContext().get()));
        }

        @Test
        @DisplayName("throw CannotRemoveLabelFromTask failure " +
                "upon an attempt to remove the label from the deleted task")
        void cannotRemoveLabelFromDeletedTask() {
            createBasicTask();
            deleteTask();

            assertThrows(CannotRemoveLabelFromTask.class,
                         () -> taskLabelsPart.handle(commandMessage().get(),
                                                     commandContext().get()));
        }

        private void dispatchAssignLabelToTask() {
            final AssignLabelToTask assignLabelToTask = assignLabelToTaskInstance(taskId, labelId);
            final Command assignLabelToTaskCmd = createDifferentCommand(assignLabelToTask);
            dispatch(taskLabelsPart, CommandEnvelope.of(assignLabelToTaskCmd));
        }
    }

    private abstract static class TaskLabelsCommandTest<C extends Message>
            extends AggregatePartCommandTest<C, TaskLabelsPart> {

        TestResponseObserver responseObserver;
        CommandBus commandBus;
        TaskLabelsPart taskLabelsPart;
        TaskId taskId;
        LabelId labelId;

        @Override
        protected void setUp() {
            super.setUp();
            taskLabelsPart = aggregatePart().get();
            createLabel();
        }

        @Override
        protected TaskLabelsPart createAggregatePart() {
            final BoundedContext boundedContext = TodoListBoundedContext.createTestInstance();
            commandBus = boundedContext.getCommandBus();
            responseObserver = new TestResponseObserver();
            taskId = createTaskId();
            labelId = createLabelId();
            final TaskAggregateRoot root = new TaskAggregateRoot(boundedContext, taskId);
            return new TaskLabelsPart(root);
        }

        private void createLabel() {
            final CreateBasicLabel createLabel = createLabelInstance(labelId);
            final Command createLabelCmd = createDifferentCommand(createLabel);
            commandBus.post(createLabelCmd, responseObserver);
        }

        void createBasicTask() {
            final CreateBasicTask createTask = createTaskInstance(taskId, DESCRIPTION);
            final Command createTaskCmd = createDifferentCommand(createTask);
            commandBus.post(createTaskCmd, responseObserver);
        }

        void deleteTask() {
            final DeleteTask deleteTask = deleteTaskInstance(taskId);
            final Command deleteTaskCmd = createDifferentCommand(deleteTask);
            commandBus.post(deleteTaskCmd, responseObserver);
        }

        void completeTask() {
            final CompleteTask completeTask = completeTaskInstance(taskId);
            final Command completeTaskCmd = createDifferentCommand(completeTask);
            commandBus.post(completeTaskCmd, responseObserver);
        }

        static LabelId createLabelId() {
            return LabelId.newBuilder()
                          .setValue(newUuid())
                          .build();
        }

        private static TaskId createTaskId() {
            return TaskId.newBuilder()
                         .setValue(newUuid())
                         .build();
        }
    }
}