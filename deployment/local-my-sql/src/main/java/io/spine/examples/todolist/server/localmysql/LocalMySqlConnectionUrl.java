/*
 * Copyright 2020, TeamDev. All rights reserved.
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

package io.spine.examples.todolist.server.localmysql;

import io.spine.examples.todolist.rdbms.ConnectionProperties;
import io.spine.examples.todolist.rdbms.ConnectionUrl;
import io.spine.examples.todolist.rdbms.JdbcConnectionProtocol;

import static java.lang.String.format;

/**
 * A URL for connecting to a local MySQL database.
 */
public class LocalMySqlConnectionUrl extends ConnectionUrl {

    LocalMySqlConnectionUrl(ConnectionProperties properties) {
        super(properties);
    }

    @Override
    protected String stringValue(ConnectionProperties properties) {
        JdbcConnectionProtocol protocol = properties.connectionProtocol();
        String result = format("%s/%s?useSSL=false", protocol.toString(), properties.dbName());
        return result;
    }
}
