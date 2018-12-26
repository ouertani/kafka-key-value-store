package com.bakdata.streams_store;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import javax.websocket.server.ServerContainer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.jersey.jackson.JacksonFeature;

import static net.sourceforge.argparse4j.impl.Arguments.store;


public class App {

    public static void main(String[] args) throws Exception {
        ArgumentParser parser = argParser();
        Properties props = new Properties();

        String topicName = null;
        String hostName = null;
        Integer port = null;
        Integer wssPort = null;
        String storeName = "key-value-store";

        try {
            Namespace res = parser.parseArgs(args);

            topicName = res.getString("topic");
            hostName = res.getString("hostname");
            port = res.getInt("port");
            wssPort = res.getInt("wssPort");
            String applicationId = res.getString("applicationId");
            List<String> streamsProps = res.getList("streamsConfig");
            String streamsConfig = res.getString("streamsConfigFile");

            if (streamsProps == null && streamsConfig == null) {
                throw new ArgumentParserException("Either --streams-props or --streams.config must be specified.", parser);
            }

            if (streamsConfig != null) {
                try (InputStream propStream = Files.newInputStream(Paths.get(streamsConfig))) {
                    props.load(propStream);
                }
            }

            if (streamsProps != null) {
                for (String prop : streamsProps) {
                    String[] pieces = prop.split("=");
                    if (pieces.length != 2)
                        throw new IllegalArgumentException("Invalid property: " + prop);
                    props.put(pieces[0], pieces[1]);
                }
            }

            props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
            props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, hostName + ":" + port);
            props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
            props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        } catch (ArgumentParserException e) {
            if (args.length == 0) {
                parser.printHelp();
                System.exit(0);
            } else {
                parser.handleError(e);
                System.exit(1);
            }
        }

        final StreamsBuilder builder = new StreamsBuilder();
        KeyValueBytesStoreSupplier stateStore = Stores.inMemoryKeyValueStore(storeName);

        KTable<String, String> table = builder.table(
            topicName,
            Materialized.<String, String>as(stateStore)
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.String())
        );

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

        //final RestService restService = new RestService(streams, storeName, hostName, port);
        //restService.start();
        //final MyWebSocketHandler wssService = new MyWebSocketHandler();
        //wssService.startServer();
        //startWSS();

        streams.start();
        Config.init(args,streams);
        startWSS2();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> streams.close()));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
               // restService.stop();
                //wssService.stopServer();
            } catch (Exception e) {}
        }));


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


    public static void startWSS()
    {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8888);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        try
        {
            // Initialize javax.websocket layer
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);

            // Add WebSocket endpoint to javax.websocket layer
            wscontainer.addEndpoint(EventSocket.class);

            server.start();
            server.dump(System.err);
            server.join();
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }

    public static void startWSS2() {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");

        try
        {
            server.start();
            server.dump(System.err);
            server.join();
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }


}
