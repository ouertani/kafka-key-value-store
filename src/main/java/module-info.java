module kafka.key.value.store {
    requires kafka.streams;
    requires lombok;
    requires kafka.clients;
    //requires jetty.servlet;
    requires jersey.common;
    requires jersey.server;
    requires jersey.media.json.jackson;
    requires jersey.container.servlet.core;
   // requires jetty.server;
    requires argparse4j;
    requires java.ws.rs;
    requires javax.websocket.api;
    //requires websocket.api;
   // requires websocket.server;
    //requires websocket.servlet;
    requires javax.servlet.api;
    requires org.eclipse.jetty.websocket.javax.websocket.server;
    requires org.eclipse.jetty.websocket.common;
    requires org.eclipse.jetty.websocket.javax.websocket;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.websocket.servlet;
    requires websocket.api;
}