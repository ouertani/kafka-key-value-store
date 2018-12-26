package com.bakdata.streams_store;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import static com.bakdata.streams_store.Config.*;

public class EventSocket extends WebSocketAdapter {

    public EventSocket() {
    }


    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
    }

    @Override
    public void onWebSocketText(String message)
    {
        try
        {
            super.onWebSocketText(message);
            System.out.println("Received TEXT message: " + message);
            getRemote().sendString(valueByKey(message).toString());
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    public KeyValueBean valueByKey(final String key) {

        final StreamsMetadata metadata = streams.metadataForKey(storeName, key, Serdes.String().serializer());
        if (metadata == null) {
            throw new NotFoundException();
        }

        final ReadOnlyKeyValueStore<String, String> store = streams.store(storeName, QueryableStoreTypes.keyValueStore());
        if (store == null) {
            throw new NotFoundException();
        }

        final String value = store.get(key);
        if (value == null) {
            throw new NotFoundException();
        }

        return new KeyValueBean(key, value);
    }


}
