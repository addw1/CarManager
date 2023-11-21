package producer;

//TODO: add login function let user input their carId
//@brief: start each car thread, read data from mqtt and write in pravega
public class CarManager {
    public static void main(String[] args) {
        // simulate four cars
        Car carA = new Car("A");
        Car carB = new Car("B");
        Car carC = new Car("C");
        Car carD = new Car("D");

        //TCP server
        TCPServer tcpServer = new TCPServer();

        // run each thread
        Thread carAThread =new Thread(carA);
        Thread carBThread =new Thread(carB);
        Thread carCThread =new Thread(carC);
        Thread carDThread = new Thread(carD);
        carAThread.start();
        carBThread.start();
        carCThread.start();
        carDThread.start();

        //run the tcp server
        tcpServer.run();
    }
}


