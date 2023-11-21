package producer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;


public class TCPServer implements Runnable{
    private ServerSocket serverSocket;
    private DataInputStream dataInputStream;
    //for the shared map
    private static final String SHARED_SCOPE = "car_states_map";
    private static final String SHARED_CONFIG_NAME = "Config";
    private static final String SHARED_CONTROLLER_URI = "tcp://8.130.97.89:9090";
    final URI controllerURI = URI.create(SHARED_CONTROLLER_URI);
    SharedConfigCli shareMap;

    public void startServer(){
        shareMap = new SharedConfigCli(SHARED_SCOPE,SHARED_CONFIG_NAME, controllerURI);
        Socket socket =null;
        try {
            serverSocket = new ServerSocket(10003);
            socket = serverSocket.accept();
            dataInputStream = new DataInputStream(socket.getInputStream());
            GetMessageFromClient();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(socket!=null){
                try{
                    socket.close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    private String parseCarId(String message){
        // TODO: split the string based on "-"
        return "";
    }

    private String parseCommand(String message){
        // TODO: split the string based on "-"
        return "";
    }
    private void GetMessageFromClient(){
        try {
            // get the message
            int length = dataInputStream.read();
            byte[] body = new byte[length];
            dataInputStream.read(body);
            String message = new String(body);
            String key = parseCarId(message);
            String value = parseCommand(message);
            //update the map
            shareMap.doPut(key, value);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run(){
        // TODO Auto-generated method stub
        TCPServer server = new TCPServer();
        server.startServer();
    }

}