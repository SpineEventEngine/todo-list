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

import io.spine.examples.todolist.cli.view.View;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Abstract base class for {@link Action}.
 */
abstract class AbstractAction<S extends View, D extends View> implements Action<S, D> {

    /**
     * A name of the action to be displayed.
     */
    private final String name;

    /**
     * A {@link Shortcut} that corresponds to the action.
     */
    private final Shortcut shortcut;

    /**
     * A source {@code View} of the action.
     */
    private final S source;

    /**
     * A destination {@code View} of the action.
     */
    private final D destination;

    AbstractAction(String name, Shortcut shortcut, S source, D destination) {
        checkArgument(!isNullOrEmpty(name));
        checkNotNull(shortcut);
        checkNotNull(source);
        checkNotNull(destination);
        this.name = name;
        this.shortcut = shortcut;
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Shortcut getShortcut() {
        return shortcut;
    }

    @Override
    public S getSource() {
        return source;
    }

    @Override
    public D getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Action)) {
            return false;
        }

        Action other = (Action) o;

        return Objects.equals(shortcut, other.getShortcut());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(shortcut);
    }

    @Override
    public String toString() {
        return ActionFormatter.format(this);
    }
}
