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

package io.spine.examples.todolist.action;

/**
 * An interface for all actions.
 *
 * <p>Actions with same {@link Shortcut} are considered equal.
 *
 * @author Dmytro Grankin
 */
public interface Action {

    /**
     * Executes the action.
     */
    void execute();

    /**
     * Obtains name of the action.
     *
     * @return action name
     */
    String getName();

    /**
     * Obtains {@link Shortcut} of the action.
     *
      * @return action shortcut
     */
    Shortcut getShortcut();

    /**
     * Compares the specified object with this action for equality.
     *
     * <p>Returns {@code} true if the specified object is also an action and
     * both actions have equal {@linkplain Shortcut shortcuts}.
     *
     * @param o the object to compare
     * @return {@code true} if the specified object is equal to this, {@code false} otherwise
     */
    @Override
    boolean equals(Object o);

    /**
     * Obtains the hash code of the action.
     *
     * <p>Implementation should return hash code for {@link Shortcut}.
     *
     * @return the hash code value
     */
    @Override
    int hashCode();
}
