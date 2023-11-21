package factory;

import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.stream.*;
import io.pravega.client.stream.impl.UTF8StringSerializer;

import java.net.URI;

// @brief: use to create ReaderGroupManager and Reader
public class ReaderFactory {

    public static ReaderGroupManager createReaderGroup(String scope,String stream, String url,String groupName)throws Exception{
        URI uri=URI.create(url);
        ReaderGroupManager readerGroupManager = ReaderGroupManager.withScope(scope,uri);
        ReaderGroupConfig readerGroupConfig = ReaderGroupConfig.builder()
                .stream(scope+"/"+stream)
                .build();
        readerGroupManager.createReaderGroup(groupName,readerGroupConfig);
        return readerGroupManager;
    }

    public static EventStreamReader<String> createReader(String url,String scope,String readerID,String groupName)throws Exception{
        URI uri=URI.create(url);
        ClientConfig clientConfig=ClientConfig.builder().controllerURI(uri).build();
        EventStreamClientFactory streamClientFactory=EventStreamClientFactory.withScope(scope,clientConfig);
        ReaderConfig readerConfig = ReaderConfig.builder().build();
        EventStreamReader<String> reader=streamClientFactory.createReader(readerID
                ,groupName
                ,new UTF8StringSerializer()
                ,readerConfig);
        return reader;
    }
}

