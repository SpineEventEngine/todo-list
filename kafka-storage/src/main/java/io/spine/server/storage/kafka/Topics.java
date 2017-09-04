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

package io.spine.server.storage.kafka;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import io.spine.string.Stringifiers;

/**
 * @author Dmytro Dashenkov
 */
final class Topics {

    private static final char SEPARATOR = '_';

    private Topics() {
        // Prevent utility class instantiation.
    }

    private abstract static class AbstractTopic implements Topic {

        @Override
        public String toString() {
            return name();
        }

        @Override
        public int hashCode() {
            return name().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Topic)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            final Topic other = (Topic) obj;
            return Objects.equal(name(), other.name());
        }
    }

    enum PrefixedTopicFactory {

        FOR_ENTITY_RECORD("entity"),
        FOR_AGGREGATE_RECORD("agg_record"),
        FOR_LAST_HANDLED_EVENT_TIME("lhet"),
        FOR_LIFECYCLE_FLAGS("lifecycle"),
        FOR_EVENT_COUNT_AFTER_SNAPSHOT("event_count_als");

        private final String prefix;

        PrefixedTopicFactory(String prefix) {
            this.prefix = prefix;
        }

        Topic create(Class<?> forType, Object withId) {
            final String value = Joiner.on(SEPARATOR)
                                       .join(prefix,
                                             forType.getName(),
                                             Stringifiers.toString(withId));
            final Topic result = new ValueTopic(value);
            return result;
        }
    }

    private static final class ValueTopic extends AbstractTopic {

        private final String value;

        private ValueTopic(String value) {
            this.value = value;
        }

        @Override
        public String name() {
            return value;
        }
    }
}