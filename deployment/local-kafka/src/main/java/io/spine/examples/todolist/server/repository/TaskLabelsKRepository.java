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

package io.spine.examples.todolist.server.repository;

import io.spine.examples.todolist.TaskId;
import io.spine.examples.todolist.c.aggregate.TaskAggregateRoot;
import io.spine.examples.todolist.c.aggregate.TaskLabelsPart;
import io.spine.server.aggregate.KAggregatePartRepository;
import io.spine.server.storage.kafka.KafkaWrapper;

import java.util.Properties;

/**
 * An implementation of a repository for {@link TaskLabelsPart} derived from
 * {@link KAggregatePartRepository}.
 *
 * @author Dmytro Dashenkov
 */
public class TaskLabelsKRepository
        extends KAggregatePartRepository<TaskId, TaskLabelsPart, TaskAggregateRoot> {

    /**
     * @see KAggregatePartRepository#KAggregatePartRepository(Properties, KafkaWrapper)
     */
    public TaskLabelsKRepository(Properties streamConfig,
                                    KafkaWrapper kafka) {
        super(streamConfig, kafka);
    }
}
