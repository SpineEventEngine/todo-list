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

package io.spine.examples.todolist.context;

import io.spine.core.EventContext;
import io.spine.examples.todolist.LabelDetails;
import io.spine.examples.todolist.LabelDetailsVBuilder;
import io.spine.examples.todolist.LabelId;
import io.spine.examples.todolist.LabelIdsList;
import io.spine.examples.todolist.Task;
import io.spine.examples.todolist.TaskDetails;
import io.spine.examples.todolist.TaskDetailsVBuilder;
import io.spine.examples.todolist.TaskId;
import io.spine.examples.todolist.TaskLabel;
import io.spine.examples.todolist.c.aggregate.LabelAggregate;
import io.spine.examples.todolist.c.aggregate.TaskLabelsPart;
import io.spine.examples.todolist.c.aggregate.TaskPart;
import io.spine.examples.todolist.repository.LabelAggregateRepository;
import io.spine.examples.todolist.repository.TaskLabelsRepository;
import io.spine.examples.todolist.repository.TaskRepository;
import io.spine.server.enrich.Enricher;
import io.spine.server.event.EventBus;

import java.util.Optional;
import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Serves as class which adds enrichment fields to the {@link EventBus}.
 */
public final class TodoListEnrichments {

    private final TaskRepository taskRepo;
    private final TaskLabelsRepository taskLabelsRepo;
    private final LabelAggregateRepository labelRepository;

    private TodoListEnrichments(Builder builder) {
        this.taskRepo = builder.taskRepo;
        this.taskLabelsRepo = builder.taskLabelsRepo;
        this.labelRepository = builder.labelRepository;
    }

    Enricher createEnricher() {
        Enricher enricher = Enricher
                .newBuilder()
                .add(LabelId.class, LabelDetails.class, labelIdToLabelDetails())
                .add(TaskId.class, TaskDetails.class, taskIdToTaskDetails())
                .add(TaskId.class, LabelIdsList.class, taskIdToLabelList())
                .add(TaskId.class, Task.class, taskIdToTask())
                .build();
        return enricher;
    }

    private BiFunction<TaskId, EventContext, Task> taskIdToTask() {
        BiFunction<TaskId, EventContext, Task> result = (taskId, eventContext) -> {
            if (taskId == null) {
                return Task.getDefaultInstance();
            }
            Optional<TaskPart> aggregate = taskRepo.find(taskId);
            if (!aggregate.isPresent()) {
                return Task.getDefaultInstance();
            }
            Task task = aggregate.get()
                                 .state();
            return task;
        };
        return result;
    }

    private BiFunction<TaskId, EventContext, TaskDetails> taskIdToTaskDetails() {
        BiFunction<TaskId, EventContext, TaskDetails> result = (taskId, eventContext) -> {
            if (taskId == null) {
                return TaskDetails.getDefaultInstance();
            }
            Optional<TaskPart> aggregate = taskRepo.find(taskId);
            if (!aggregate.isPresent()) {
                return TaskDetails.getDefaultInstance();
            }
            Task state = aggregate.get()
                                  .state();
            TaskDetails details = TaskDetailsVBuilder
                    .newBuilder()
                    .setDescription(state.getDescription())
                    .setPriority(state.getPriority())
                    .setStatus(state.getTaskStatus())
                    .build();
            return details;
        };

        return result;
    }

    private BiFunction<TaskId, EventContext, LabelIdsList> taskIdToLabelList() {
        BiFunction<TaskId, EventContext, LabelIdsList> result = (taskId, eventContext) -> {
            if (taskId == null) {
                return LabelIdsList.getDefaultInstance();
            }
            Optional<TaskLabelsPart> aggregate = taskLabelsRepo.find(taskId);
            if (!aggregate.isPresent()) {
                return LabelIdsList.getDefaultInstance();
            }
            LabelIdsList state = aggregate.get()
                                          .state()
                                          .getLabelIdsList();
            return state;
        };
        return result;
    }

    private BiFunction<LabelId, EventContext, LabelDetails> labelIdToLabelDetails() {
        BiFunction<LabelId, EventContext, LabelDetails> result = (labelId, eventContext) -> {
            if (labelId == null) {
                return LabelDetails.getDefaultInstance();
            }
            Optional<LabelAggregate> aggregate = labelRepository.find(labelId);
            if (!aggregate.isPresent()) {
                return LabelDetails.getDefaultInstance();
            }
            TaskLabel state = aggregate.get()
                                       .state();
            LabelDetails labelDetails = LabelDetailsVBuilder
                    .newBuilder()
                    .setColor(state.getColor())
                    .setTitle(state.getTitle())
                    .build();
            return labelDetails;
        };
        return result;
    }

    /**
     * Creates a new builder for (@code TodoListEnrichments).
     *
     * @return new builder instance
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * A builder for {@code TodoListEnrichments} instances.
     */
    public static class Builder {

        private TaskRepository taskRepo;
        private TaskLabelsRepository taskLabelsRepo;
        private LabelAggregateRepository labelRepository;

        private Builder() {
        }

        public Builder setTaskRepository(TaskRepository definitionRepository) {
            checkNotNull(definitionRepository);
            this.taskRepo = definitionRepository;
            return this;
        }

        public Builder setTaskLabelsRepository(TaskLabelsRepository taskLabelsRepository) {
            checkNotNull(taskLabelsRepository);
            this.taskLabelsRepo = taskLabelsRepository;
            return this;
        }

        public Builder setLabelRepository(LabelAggregateRepository labelRepository) {
            checkNotNull(labelRepository);
            this.labelRepository = labelRepository;
            return this;
        }

        public TodoListEnrichments build() {
            return new TodoListEnrichments(this);
        }
    }
}
