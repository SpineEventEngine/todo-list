/*
 * Copyright 2016, TeamDev Ltd. All rights reserved.
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

package org.spine3.examples.todolist.repository;

import com.google.protobuf.Message;
import org.spine3.base.EventContext;
import org.spine3.examples.todolist.TaskLabelId;
import org.spine3.examples.todolist.projection.LabelledTasksViewProjection;
import org.spine3.examples.todolist.view.LabelledTasksView;
import org.spine3.server.BoundedContext;
import org.spine3.server.projection.ProjectionRepository;

/**
 * @author Illia Shepilov
 */
public class LabelledTasksViewRepository
        extends ProjectionRepository<TaskLabelId, LabelledTasksViewProjection, LabelledTasksView> {
    public LabelledTasksViewRepository(BoundedContext boundedContext) {
        super(boundedContext);
    }

    @Override
    protected TaskLabelId getEntityId(Message event, EventContext context) {

        for (Object fieldValue : event.getAllFields()
                                      .values()) {
            if (fieldValue instanceof TaskLabelId) {
                return (TaskLabelId) fieldValue;
            }
        }
        return TaskLabelId.getDefaultInstance();
    }
}
