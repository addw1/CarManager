package factory;

import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.stream.*;
import io.pravega.client.stream.impl.UTF8StringSerializer;

import java.net.URI;

// @brief: use to create a EventStreamWriter
public class WriterFactory {

    public static EventStreamWriter<String> createWriter(String url,String scope,String stream)throws Exception{
        URI uri=URI.create(url);
        ClientConfig build = ClientConfig.builder().controllerURI(uri).build();
        EventStreamClientFactory eventStreamClientFactory = EventStreamClientFactory.withScope(scope,build);
        EventWriterConfig writeConfig = EventWriterConfig.builder().build();
        return eventStreamClientFactory.createEventWriter(stream
                ,new UTF8StringSerializer()
                ,writeConfig);
    }
}

