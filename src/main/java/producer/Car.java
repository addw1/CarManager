package producer;
import factory.WriterFactory;
import io.pravega.client.stream.EventStreamWriter;

import java.net.URI;

public class Car implements Runnable {

    String carID;
    String URL;
    // receive the data from mqtt
    private MqttListener velListener;
    private MqttListener posListener;
    private MqttListener batteryListener;

    // write stream to the pravega
    private EventStreamWriter<String> velWriter;
    private EventStreamWriter<String> posWriter;
    private EventStreamWriter<String> batteryWriter;

    //for car state map
    private static final String SHARED_SCOPE = "car_states_map";
    private static final String SHARED_CONFIG_NAME = "Config";
    private static final String SHARED_CONTROLLER_URI = "tcp://8.130.97.89:9090";
    final URI controllerURI = URI.create(SHARED_CONTROLLER_URI);
    SharedConfigCli carStateMap;

    // for the store of each run
    private static final String RUNID_SCOPE = "car_states_map";
    private static final String RUNID_CONFIG_NAME = "Config";
    private static final String RUNID_CONTROLLER_URI = "tcp://8.130.97.89:9090";
    final URI runControllerURI = URI.create(RUNID_CONTROLLER_URI);
    SharedConfigCli runStateMap;

    //last state
    String lastState = "close";
    int runTime = 0;
    int eventIndex = 0;

    Car(String _carID) {
        try {
            URL = "TCP://8.130.97.89:9090";
            this.carID = _carID;
            velListener = new MqttListener(this.carID + "/velocity");
            posListener = new MqttListener(this.carID + "/position");
            batteryListener = new MqttListener(this.carID + "/battery");
            velWriter = WriterFactory.createWriter(URL, this.carID, "velocity");
            posWriter = WriterFactory.createWriter(URL, this.carID, "position");
            batteryWriter = WriterFactory.createWriter(URL, this.carID, "battery");
            carStateMap =  new SharedConfigCli(SHARED_SCOPE, SHARED_CONFIG_NAME, controllerURI);
            runStateMap = new SharedConfigCli(RUNID_SCOPE, RUNID_CONFIG_NAME, runControllerURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void pushToPravega() throws Exception{
        // get the data from mqtt
        String velocityMes = velListener.receiveData();
        String positionMes = posListener.receiveData();
        String batteryMes = batteryListener.receiveData();

        //write to the prevega
        velWriter.writeEvent(velocityMes);
        posWriter.writeEvent(positionMes);
        batteryWriter.writeEvent(batteryMes);
        eventIndex++;
    }

    public void run() {
        while (true) {
            try {
                String state = carStateMap.doGet(carID);
                if(!state.equals(lastState)){
                    runStateMap.doPut(carID + Integer.toString(runTime), Integer.toString(eventIndex));
                    lastState = state;
                }
                if(state.equals("OPEN"))
                    pushToPravega();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}