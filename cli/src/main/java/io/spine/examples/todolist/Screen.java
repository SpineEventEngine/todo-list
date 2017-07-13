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

package io.spine.examples.todolist;

import io.spine.examples.todolist.view.View;

import java.util.Optional;

/**
 * A screen of an application.
 *
 * @author Dmytro Grankin
 */
public interface Screen {

    /**
     * Renders specified view.
     *
     * @param view the view to renderBody
     */
    void renderView(View view);

    /**
     * Obtains previous view for the specified view.
     *
     * @param view the view that was rendered after the previous view
     * @return {@code Optional} of previous view,
     *         or {@code Optional.empty} if the specified view is the first view that was rendered
     */
    Optional<View> getPreviousView(View view);

    /**
     * Prompts a user for an input and receives the input value.
     *
     * @param prompt the prompt to display
     * @return the input value
     */
    String promptUser(String prompt);

    /**
     * Prints the message and a new line after it.
     *
     * @param message the message to print
     */
    void println(String message);
}