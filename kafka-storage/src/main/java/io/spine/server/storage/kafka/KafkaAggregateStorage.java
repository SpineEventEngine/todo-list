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

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.protobuf.UInt32Value;
import io.spine.Identifier;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.AggregateEventRecord;
import io.spine.server.aggregate.AggregateStorage;
import io.spine.server.entity.LifecycleFlags;

import java.util.Iterator;

import static com.google.common.base.Optional.fromNullable;

/**
 * @author Dmytro Dashenkov
 */
public class KafkaAggregateStorage<I> extends AggregateStorage<I> {

    private final Class<? extends Aggregate<I, ?, ?>> aggregateClass;
    private final KafkaWrapper storage;

    protected KafkaAggregateStorage(Class<? extends Aggregate<I, ?, ?>> cls,
                                    KafkaWrapper storage,
                                    boolean multitenant) {
        super(multitenant);
        this.aggregateClass = cls;
        this.storage = storage;
    }

    @Override
    protected int readEventCountAfterLastSnapshot(I id) {
        final Topic topic = new EventCountTopic(id, aggregateClass);
        final int eventCount = storage.<UInt32Value>readLast(topic)
                                      .map(UInt32Value::getValue)
                                      .orElse(0);
        return eventCount;
    }

    @Override
    protected void writeEventCountAfterLastSnapshot(I id, int eventCount) {
        final Topic topic = new EventCountTopic(id, aggregateClass);
        final UInt32Value msg = UInt32Value.newBuilder()
                                           .setValue(eventCount)
                                           .build();
        storage.write(topic, id, msg);
    }

    @Override
    protected void writeRecord(I id, AggregateEventRecord record) {
        final Topic topic = new AggregateRecordTopic(id, aggregateClass);
        storage.write(topic, id, record);
    }

    @Override
    protected Iterator<AggregateEventRecord> historyBackward(I id) {
        final Topic topic = new AggregateRecordTopic(id, aggregateClass);
        return storage.read(topic);
    }

    @SuppressWarnings("Guava") // Spine API for Java 7.
    @Override
    public Optional<LifecycleFlags> readLifecycleFlags(I id) {
        final Topic topic = new LifecycleFlagsTopic(id, aggregateClass);
        final LifecycleFlags result = storage.<LifecycleFlags>readLast(topic)
                                             .orElse(null);
        return fromNullable(result);
    }

    @Override
    public void writeLifecycleFlags(I id, LifecycleFlags flags) {
        final Topic topic = new LifecycleFlagsTopic(id, aggregateClass);
        storage.write(topic, id, flags);
    }

    @Override
    public Iterator<I> index() {
        return null;
    }

    private static class EventCountTopic implements Topic {

        private static final String PREFIX = "event_count_als_";

        private final String topic;

        private EventCountTopic(Object id, Class<?> type) {
            this.topic = PREFIX
                    + type.getName()
                    + Identifier.toString(id);
        }

        @Override
        public String name() {
            return topic;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            EventCountTopic that = (EventCountTopic) o;
            return Objects.equal(topic, that.topic);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(topic);
        }

        @Override
        public String toString() {
            return name();
        }
    }

    private static class LifecycleFlagsTopic implements Topic {

        private static final String PREFIX = "agg_lifecycle_";

        private final String topic;

        private LifecycleFlagsTopic(Object id, Class<?> type) {
            this.topic = PREFIX
                    + type.getName()
                    + Identifier.toString(id);
        }

        @Override
        public String name() {
            return topic;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            EventCountTopic that = (EventCountTopic) o;
            return Objects.equal(topic, that.topic);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(topic);
        }

        @Override
        public String toString() {
            return name();
        }
    }

    private static class AggregateRecordTopic implements Topic {

        private static final String PREFIX = "aggregate_";

        private final String topic;

        private AggregateRecordTopic(Object id, Class<?> type) {
            this.topic = PREFIX
                    + type.getName()
                    + Identifier.toString(id);
        }

        @Override
        public String name() {
            return topic;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            EventCountTopic that = (EventCountTopic) o;
            return Objects.equal(topic, that.topic);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(topic);
        }

        @Override
        public String toString() {
            return name();
        }
    }
}