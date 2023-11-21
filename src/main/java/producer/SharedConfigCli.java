package producer;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import io.pravega.client.ClientConfig;
import io.pravega.client.SynchronizerClientFactory;
import io.pravega.client.admin.StreamManager;


public class SharedConfigCli implements AutoCloseable{
    private static final String DEFAULT_SCOPE = "car_states_map";
    private static final String DEFAULT_CONFIG_NAME = "Config";
    private static final String DEFAULT_CONTROLLER_URI = "tcp://8.130.97.89:9090";
    private final String configName; //corresponds to the stream name used by the synchronizer behind the shared config

    private final SynchronizerClientFactory clientFactory;
    private final StreamManager streamManager;
    private final SharedConfig<String, String> config;

    public SharedConfigCli(String scope, String configName, URI controllerURI) {
        this.configName = configName;

        this.clientFactory = SynchronizerClientFactory.withScope(scope, ClientConfig.builder().controllerURI(controllerURI).build());
        this.streamManager = StreamManager.create(controllerURI);

        this.config = new SharedConfig<>(clientFactory, streamManager, scope, configName);
    }

    private String getPrompt() {
        return configName;
    }

    // get the value based on the key
    public String doGet(String key) {
        String value = null;
        try {
            value = config.getProperty(key);
            if (value == null ) {
                warn("Property '%s' is undefined%n", key);
            } else {
                output("The value of property '%s' is '%s'%n", key, value);
            }

        } catch (IndexOutOfBoundsException e) {
            warn("Please enter a key to retrieve%n");
        }
        if(value == null)
            return "";
        return value;
    }

    public void doPut(String key, String value) {
        try {
            final String oldValue = config.putProperty(key, value);
            if (oldValue == null) {
                output("Property '%s' added with value '%s'%n", key, value);
            } else {
                output("Property '%s' updated from: '%s' to: '%s'%n", key, oldValue, value);
            }
        } catch (IndexOutOfBoundsException e) {
            warn("Expecting parameters: key , value %n");
        }
    }

    public void doRemove(List<String> parms) {
        try {
            final String key = parms.get(0);
            if (parms.size() > 1) {
                final String currValue = parms.get(1);
                final boolean removed = config.removeProperty(key, currValue);
                if (removed) {
                    output("Property '%s' is removed -- old value was '%s'%n", key, currValue);
                } else {
                    warn("Property '%s' was NOT removed -- current value did not match given value '%s'%n", key, currValue);
                }
            } else {
                final String currValue = config.removeProperty(key);
                if (currValue == null ) {
                    output("Property '%s' is undefined; nothing to remove%n", key);
                } else {
                    output("Property for '%s' is removed -- old value was '%s'%n", key, currValue);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            warn("Expecting parameters: key [, currentValue]%n");
        }

        if (parms.size() > 2) {
            warn("Ignoring parameters: '%s'%n", String.join(",", parms.stream().skip(2).collect(Collectors.toList())));
        }
    }



      private void output(String format, Object... args){
        System.out.format("**** ");
        System.out.format(format, args);
    }

    private void warn(String format, Object... args){
        System.out.format("!!!! ");
        System.out.format(format, args);
    }

    private void output(Exception e) {
        e.printStackTrace();
        output("%n");
    }

    @Override
    public void close(){
        if (config != null) {
            config.close();
        }

        if (streamManager != null) {
            streamManager.close();
        }

        if (clientFactory != null) {
            clientFactory.close();
        }
    }
}