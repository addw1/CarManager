package producer;

import org.fusesource.mqtt.client.*;

public class MqttListener {
    BlockingConnection connection;

    MqttListener(String topic) throws Exception{
        //create MQTT object
        MQTT mqtt = new MQTT();
        connection = null;
        // TODO: modify the aliyun ip
        mqtt.setHost("tcp://8.130.97.89:1883");

        // config MQTT
        mqtt.setCleanSession(true);
        mqtt.setReconnectAttemptsMax(6);
        mqtt.setReconnectDelay(2000);
        mqtt.setKeepAlive((short) 30);
        mqtt.setSendBufferSize(2 * 1024 * 1024);

        // connect with th server
        connection = mqtt.blockingConnection();
        connection.connect();
        // create the topic
        Topic[] topics = {new Topic(topic, QoS.AT_LEAST_ONCE)};
        // subscribe the topic
        byte[] qoses = connection.subscribe(topics);

    }

    // to receive data from the topic you subscribe
    String receiveData() throws Exception{
        byte[] payload = null;
        try {
            Message message = connection.receive();
            System.out.println("received...");
            // get the message
            payload = message.getPayload();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(payload == null)
            return "-";
        else
            return new String(payload);
    }
}

