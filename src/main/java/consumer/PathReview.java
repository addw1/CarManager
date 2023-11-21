package consumer;

import io.pravega.client.admin.ReaderGroupManager;
import factory.ReaderFactory;
import io.pravega.client.stream.EventStreamReader;
import io.pravega.client.stream.EventStreamWriter;
import producer.SharedConfigCli;

import java.net.URI;

public class PathReview implements Runnable {
    private static final String RUNID_SCOPE = "car_states_map";
    private static final String RUNID_CONFIG_NAME = "Config";
    private static final String RUNID_CONTROLLER_URI = "tcp://8.130.97.89:9090";
    final URI runControllerURI = URI.create(RUNID_CONTROLLER_URI);
    SharedConfigCli runStateMap;

    protected EventStreamReader<String> clientReader;
    protected ReaderGroupManager clientReaderGroupManager;

    PathReview() {
        try {
            clientReaderGroupManager = ReaderFactory.createReaderGroup("carA", "velocity", RUNID_CONTROLLER_URI, "VelocityGroup");
            clientReader = ReaderFactory.createReader(RUNID_CONTROLLER_URI, "carA", "A", "A" + "Group");
            runStateMap = new SharedConfigCli(RUNID_SCOPE, RUNID_CONFIG_NAME, runControllerURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void run(){
        //TODO:
    }

}

