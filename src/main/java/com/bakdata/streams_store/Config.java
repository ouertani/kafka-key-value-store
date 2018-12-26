package com.bakdata.streams_store;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.HostInfo;

import static net.sourceforge.argparse4j.impl.Arguments.store;

public class Config {
    public final Namespace res = null;
    public static String topicName = null;
    public static String hostName = null;
    public static Integer port = null;
    public static Integer wssPort = null;
    public static String storeName = "key-value-store";
    public static HostInfo hostInfo;
    public static KafkaStreams streams;

    static Namespace init(String[] args, final KafkaStreams _streams)throws ArgumentParserException {
        var res = argParser().parseArgs(args);
        topicName = res.getString("topic");
        hostName = res.getString("hostname");
        port = res.getInt("port");
        wssPort = res.getInt("wssPort");
        hostInfo = new HostInfo(hostName, port);
        streams = _streams;
        return res;
    }

    private static ArgumentParser argParser() {
        ArgumentParser parser = ArgumentParsers
                .newFor("streams-processor").build()
                .defaultHelp(true)
                .description("This Kafka Streams application is used to interactively query values from Kafka topics");

        parser.addArgument("--topic")
                .action(store())
                .required(true)
                .type(String.class)
                .metavar("TOPIC")
                .help("process messages from this topic");

        parser.addArgument("--streams-props")
                .nargs("+")
                .required(false)
                .metavar("PROP-NAME=PROP-VALUE")
                .type(String.class)
                .dest("streamsConfig")
                .help("kafka streams related configuration properties like bootstrap.servers etc. " +
                        "These configs take precedence over those passed via --streams.config.");

        parser.addArgument("--streams.config")
                .action(store())
                .required(false)
                .type(String.class)
                .metavar("CONFIG-FILE")
                .dest("streamsConfigFile")
                .help("streams config properties file.");

        parser.addArgument("--application-id")
                .action(store())
                .required(false)
                .type(String.class)
                .metavar("APPLICATION-ID")
                .dest("applicationId")
                .setDefault("streams-processor-default-application-id")
                .help("The id of the streams application to use. Useful for monitoring and resetting the streams application state.");

        parser.addArgument("--hostname")
                .action(store())
                .required(false)
                .type(String.class)
                .metavar("HOSTNAME")
                .setDefault("localhost")
                .help("The host name of this machine / pod / container. Used for inter-processor communication.");

        parser.addArgument("--port")
                .action(store())
                .required(false)
                .type(Integer.class)
                .metavar("PORT")
                .setDefault(8080)
                .help("The TCP Port for the HTTP REST Service");

        parser.addArgument("--wssPort")
                .action(store())
                .required(false)
                .type(Integer.class)
                .metavar("WSSPORT")
                .setDefault(9999)
                .help("The TCP WssPort for the HTTP WebSocket Service");

        return parser;
    }

}
