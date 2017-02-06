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

package org.spine3.examples.todolist.context;

import org.spine3.examples.todolist.repositories.TaskDefinitionRepository;
import org.spine3.examples.todolist.repositories.TaskLabelsRepository;
import org.spine3.server.BoundedContext;
import org.spine3.server.event.enrich.EventEnricher;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Serves as a repository provider for all the application components
 * that are initialized before the {@link BoundedContext}.
 *
 * <p> As long as the {@code Aggregate} and {@code Projection} repositories
 * require the instance of the {@code BoundedContext} for their own initialization,
 * they are all {@code null} before the {@code BoundedContext} is built.
 *
 * <p> However, some of the app components (such as {@link EventEnricher}) at the same time
 * <ul>
 *     <li> need repository instances for own operation, thus requiring them for instantiation,
 *     <li> are involved into the {@code BoundedContext} initialization.
 * </ul>
 *
 * <p>In order to break such a cyclic dependency,  the instances of {@code TodoListRepositoryProvider}
 * serve as providers of the repository instances in "lazy" mode.
 *
 * @author Illia Shepilov
 */
//TODO:2017-02-06:illiashepilov: is it still needed?
class TodoListRepositoryProvider {

    @Nullable
    private TaskLabelsRepository taskLabelsRepo;
    @Nullable
    private TaskDefinitionRepository taskDefinitionRepo;

    void setTaskLabelsRepository(TaskLabelsRepository taskLabelsRepo) {
        this.taskLabelsRepo = taskLabelsRepo;
    }

    void setTaskDefinitionRepository(TaskDefinitionRepository labelAggregateRepo) {
        this.taskDefinitionRepo = labelAggregateRepo;
    }

    /**
     * Returns the wrapped {@link TaskLabelsRepository} in the {@code Optional}.
     *
     * @return the {@code Optional}
     */
    Optional<TaskLabelsRepository> getTaskLabelsRepo() {
        final Optional<TaskLabelsRepository> result = Optional.ofNullable(this.taskLabelsRepo);
        return result;
    }

    /**
     * Returns the wrapped {@link TaskDefinitionRepository} in the {@code Optional}.
     *
     * @return the {@code Optional}
     */
    Optional<TaskDefinitionRepository> getTaskDefinitionRepo() {
        final Optional<TaskDefinitionRepository> result = Optional.ofNullable(this.taskDefinitionRepo);
        return result;
    }
}
