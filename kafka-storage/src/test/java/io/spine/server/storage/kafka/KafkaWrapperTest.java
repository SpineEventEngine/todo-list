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

import com.google.protobuf.Int32Value;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt64Value;
import io.spine.server.entity.AbstractEntity;
import io.spine.server.entity.AbstractVersionableEntity;
import io.spine.server.entity.Entity;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.collect.Lists.newArrayList;
import static io.spine.server.storage.kafka.Consistency.STRONG;
import static io.spine.server.storage.kafka.MessageSerializer.deserializer;
import static io.spine.server.storage.kafka.MessageSerializer.serializer;
import static io.spine.server.storage.kafka.given.KafkaStorageTestEnv.getConsumerConfig;
import static io.spine.server.storage.kafka.given.KafkaStorageTestEnv.getPollAwait;
import static io.spine.server.storage.kafka.given.KafkaStorageTestEnv.getProducerConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Dmytro Dashenkov
 */
@DisplayName("KafkaWrapper should")
class KafkaWrapperTest {

    private KafkaWrapper wrapper;

    @BeforeEach
    void setUp() {
        TestRoutines.beforeEach();
        final Properties consumerConfig = getConsumerConfig();
        final Properties producerConfig = getProducerConfig();
        final KafkaConsumer<Message, Message> consumer = new KafkaConsumer<>(consumerConfig,
                                                                             deserializer(),
                                                                             deserializer());
        final KafkaProducer<Message, Message> producer = new KafkaProducer<>(producerConfig,
                                                                             serializer(),
                                                                             serializer());
        wrapper = new KafkaWrapper(producer, consumer, STRONG, getPollAwait());
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        TestRoutines.afterEach();
//        final Collection<String> topicNames = wrapper.getTopicNames();
//        final Runtime runtime = getRuntime();
//        final Collection<Process> processes = new ArrayList<>(topicNames.size());
//        for (String topic : topicNames) {
//            final Process process = runtime.exec(new String[] {
//                    "bin/kafka-topics.sh",
//                    "--zookeeper", "localhost:2181",
//                    "--delete", "--topic", topic});
//            processes.add(process);
//        }
//        for (Process process : processes) {
//            process.waitFor();
//        }
    }

    @Test
    @DisplayName("write and read data")
    void writeAndReadData() {
        final Message value = Int32Value.newBuilder()
                                        .setValue(42)
                                        .build();
        final Topic topic = Topic.ofValue("KafkaWrapperTest-writeAndReadData");
        wrapper.write(TestEntity.class, topic, value, value);
        final Optional<Int32Value> readResult = wrapper.readLast(topic);
        assertTrue(readResult.isPresent());
        final Int32Value readMessage = readResult.get();
        assertEquals(value, readMessage);
    }

    @Test
    @DisplayName("read records in bulk")
    void writeAndReadBulk() {
        final int count = 5;
        final Collection<Int32Value> values = createValues(count);
        final Topic topic = Topic.ofValue("KafkaWrapperTest-writeAndReadBulk");
        values.forEach(value -> wrapper.write(TestEntity.class, topic, value, value));
        final Iterator<Int32Value> readValues = wrapper.read(topic);
        final Collection<Int32Value> readList = newArrayList(readValues);
        assertEquals(values.size(), readList.size());
        assertTrue(values.containsAll(readList));
    }

    @Test
    @DisplayName("read all records of a certain Entity type")
    void readAllByEntityType() {
        /**
         * A test {@link io.spine.server.entity.Entity Entity} type with a explicit class name,
         * non-accessible from the outer context.
         */
        class UniqueEntity extends AbstractVersionableEntity<String, UInt64Value> {
            protected UniqueEntity(String id) {
                super(id);
            }
        }
        final Class<? extends Entity> cls = UniqueEntity.class;
        final Topic topic1 = Topic.ofValue("KafkaWrapperTest-readAllByEntityType1");
        final int count1 = 5;
        final Collection<Int32Value> values1 = createValues(count1);
        final Topic topic2 = Topic.ofValue("KafkaWrapperTest-readAllByEntityType2");
        final int count2 = 4;
        final Collection<Int32Value> values2 = createValues(count2, count1);

        values1.forEach(value -> wrapper.write(cls, topic1, value, value));
        values2.forEach(value -> wrapper.write(cls, topic2, value, value));

        final Iterator<Int32Value> readValues = wrapper.readAll(cls);
        final Collection<Int32Value> readList = newArrayList(readValues);
        assertEquals(count1 + count2, readList.size());
        assertTrue(readList.containsAll(values1));
        assertTrue(readList.containsAll(values2));
    }

    @Test
    @DisplayName("read last record with given ID")
    void readLastByKey() {
        final Topic topic = Topic.ofValue("KafkaWrapperTest-readLastByKey");
        final Int32Value first = Int32Value.newBuilder()
                                           .setValue(314)
                                           .build();
        final Int32Value last = Int32Value.newBuilder()
                                          .setValue(271)
                                          .build();
        final String id = "Hidden_Math_Constant";
        wrapper.write(TestEntity.class, topic, id, first);
        wrapper.write(TestEntity.class, topic, id, last);

        @SuppressWarnings("ConstantConditions") // Call `get()` without `isPresent()` check:
                                                // OK for test as we expect the value to be present
        final Int32Value read = wrapper.<Int32Value>read(topic, id).get();
        assertEquals(last, read);
    }

    @Disabled // The test case ensures that the system is able to deal with comparatively big
              // number of partitions without any serious performance restrictions.
              // When running this test, set num.partitions=1000 or more to make it serve
              // the purpose of the test.
    @Test
    @DisplayName("handle thousands of partitions")
    @SuppressWarnings("MethodWithMultipleLoops")
        // Required by the test logic. First we create all the topics and then read messages from
        // them. The readability does not suffer in this case.
    void handleBigNumberOfPartitions() {
        final int count = 100;
        final Topic topic = Topic.ofValue("KafkaWrapperTest-handleBigNumberOfPartitions");
        final Message[] keys = new Message[count];
        final StringValue[] values = new StringValue[count];
        for (int i = 0; i < count; i++) {
            final Message key = StringValue.newBuilder()
                                           .setValue("big-data-key" + i)
                                           .build();
            keys[i] = key;
            final StringValue value = StringValue.newBuilder()
                                             .setValue("big-data-value" + i)
                                             .build();
            values[i] = value;
            wrapper.write(topic, key, value);
        }
        for (int i = 0; i < count; i++) {
            final Message key = keys[i];
            final StringValue msg = wrapper.<StringValue>read(topic, key)
                                           .orElseThrow(AssertionError::new);
            assertEquals(values[i].getValue(), msg.getValue());
        }
    }

    @SuppressWarnings("MethodWithMultipleLoops")
        // Required to first launch and then await the Futures
    @Test
    @DisplayName("synchronize the consumer access")
    void syncConsumerAccess() throws ExecutionException, InterruptedException {
        final int threadCount = 10;
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final Topic topic = Topic.ofValue("KafkaWrapperTest-syncConsumerAccess");
        final long id = 314L;
        final Message msg = StringValue.newBuilder()
                                       .setValue("arbitrary")
                                       .build();
        wrapper.write(topic, id, msg);
        final Collection<Future<Message>> results = new ArrayList<>(threadCount);
        final Callable<Message> readOp = () -> wrapper.read(topic, id)
                                                      .orElseThrow(AssertionError::new);
        for (int i = 0; i < threadCount; i++) {
            results.add(executor.submit(readOp));
        }
        for (Future<Message> result : results) {
            assertEquals(msg, result.get());
        }
    }

    private static Collection<Int32Value> createValues(int count) {
        return createValues(count, 0);
    }

    private static Collection<Int32Value> createValues(int count, int offset) {
        final Collection<Int32Value> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            values.add(Int32Value.newBuilder()
                                 .setValue(i + offset)
                                 .build());
        }
        return values;
    }

    private static class TestEntity extends AbstractEntity<String, Int32Value> {
        protected TestEntity(String id) {
            super(id);
        }
    }
}
