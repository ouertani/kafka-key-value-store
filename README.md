# Queryable Kafka Topics with Kafka Streams

This repository provides the source code for our blog post on [Medium](https://medium.com/bakdata/queryable-kafka-topics-with-kafka-streams-8d2cca9de33f).

It includes an example implementation of a Kafka Streams application that provides a key-value query interface to the messages of a key-partitioned Kafka topic.

Additionally, a Dockerfile and a Kubernetes deployment specification demonstrate how the application can be easily deployed to a Kubernetes Cluster.

## Quick Start

Compile the project using Maven:
```
mvn package
```

Create a topic one partition:
```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic  messages
```

Send message to topic:
```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic messages --property "parse.key=true" --property "key.separator=#"
key1#{ "name":"Slim", "age":38 }
key2#{ "name":"Chams", "age":28 }
key1#{ "name":"Ouertani", "age":39 }
```
Start one or more instances of the Kafka Streams application:

```
./streams-processor                         \
    --topic messages                        \
    --streams-props                         \
        bootstrap.servers=localhost:9092    \
        num.standby.replicas=1              \
    --application-id my-streams-processor   \
    --hostname localhost
```
