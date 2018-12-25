package com.bakdata.streams_store;

import java.io.Serializable;
import java.util.List;

public class ProcessorMetadata implements Serializable {
    private String host;
    private int port;
    private List<Integer> topicPartitions;

    public ProcessorMetadata(String host, int port, List<Integer> topicPartitions) {
        this.host = host;
        this.port = port;
        this.topicPartitions = topicPartitions;
    }

    public ProcessorMetadata() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<Integer> getTopicPartitions() {
        return topicPartitions;
    }

    public void setTopicPartitions(List<Integer> topicPartitions) {
        this.topicPartitions = topicPartitions;
    }
}