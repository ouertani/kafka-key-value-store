package com.bakdata.streams_store;

import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.KafkaClientSupplier;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.QueryableStoreType;

import java.util.Properties;
import java.util.concurrent.Flow;

public class ReactiveKafkaStreams  {

    private final KafkaStreams kafkaStreams;

    public ReactiveKafkaStreams(KafkaStreams kafkaStreams) {
      this.kafkaStreams=kafkaStreams;
    }


    public <T> Flow.Subscriber<T> reactiveStore(String storeName, QueryableStoreType<T> queryableStoreType) {
        return null;
    }
}
