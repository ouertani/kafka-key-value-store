module kafka.key.value.store {
    requires kafka.streams;
    requires lombok;
    requires kafka.clients;
    requires jetty.servlet;
    requires jersey.common;
    requires jersey.server;
    requires jersey.media.json.jackson;
    requires jersey.container.servlet.core;
    requires jetty.server;
    requires argparse4j;
    requires java.ws.rs;
}