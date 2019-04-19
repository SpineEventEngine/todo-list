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

package io.spine.examples.todolist.server;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;

import static com.google.api.client.util.Preconditions.checkNotNull;
import static io.spine.server.DeploymentType.APPENGINE_CLOUD;
import static io.spine.server.ServerEnvironment.getDeploymentType;
import static io.spine.util.Exceptions.illegalStateWithCauseOf;

/**
 * A factory of various forms of Google API credentials.
 */
final class GoogleAuth {

    private static final String SERVICE_ACCOUNT_KEY = "spine-dev.json";

    /**
     * Prevents the utility class instantiation.
     */
    private GoogleAuth() {
    }

    /**
     * Obtains the service account credential.
     *
     * <p>When running under AppEngine, returns the default service account of this application.
     * Otherwise, reads and returns the service account credential from
     * the {@code spine-dev.json} resource.
     *
     * @see #serviceAccountCredentials() for an alternative API for the same credential
     */
    static GoogleCredential serviceAccountCredential() {
        if (getDeploymentType() == APPENGINE_CLOUD) {
            return propagateIoErrors(GoogleCredential::getApplicationDefault);
        } else {
            InputStream inputStream = readResource(SERVICE_ACCOUNT_KEY);
            GoogleCredential credential = propagateIoErrors(
                    () -> GoogleCredential.fromStream(inputStream)
            );
            return credential;
        }
    }

    /**
     * Obtains the service account credential.
     *
     * <p>When running under AppEngine, returns the default service account of this application.
     * Otherwise, reads and returns the service account credential from
     * the {@code spine-dev.json} resource.
     *
     * <p>This credential is used for accessing the GCP APIs, such as the Could Datastore API.
     *
     * @see #serviceAccountCredential() for an alternative API for the same credential
     */
    static GoogleCredentials serviceAccountCredentials() {
        if (getDeploymentType() == APPENGINE_CLOUD) {
            return propagateIoErrors(GoogleCredentials::getApplicationDefault);
        } else {
            InputStream inputStream = readResource(SERVICE_ACCOUNT_KEY);
            GoogleCredentials credential = propagateIoErrors(
                    () -> GoogleCredentials.fromStream(inputStream)
            );
            return credential;
        }
    }

    private static InputStream readResource(String name) {
        InputStream secret = GoogleAuth.class.getClassLoader()
                                             .getResourceAsStream(name);
        checkNotNull(secret, "%s resource is missing.", name);
        return secret;
    }

    private static <T> T propagateIoErrors(IoOperation<T> operation) {
        try {
            return operation.perform();
        } catch (IOException e) {
            throw illegalStateWithCauseOf(e);
        }
    }

    /**
     * An I/O operation.
     *
     * <p>This operation may produce an {@link IOException} or return a result. An example is
     * reading a file from classpath.
     *
     * @param <T>
     *         the type of the operation result
     */
    @FunctionalInterface
    private interface IoOperation<T> {

        /**
         * Performs the operation.
         *
         * @return result of the operation
         * @throws IOException
         *         if an I/O error occurs
         */
        T perform() throws IOException;
    }
}