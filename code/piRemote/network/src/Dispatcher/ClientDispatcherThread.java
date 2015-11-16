package Dispatcher;

import KeepAlive.ClientKeepAliveThread;
import MessageObject.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * created by fabian on 13.11.15
 */

public class ClientDispatcherThread extends AbstractDispatcherThread {

    private ClientKeepAliveThread keepAliveThread;
    private Thread clientDispatcher;
    private Socket socket;
    private ObjectInputStream inputStream;
    // private ClientCore clientCore needed for the mainQueue;

    // constructor
    public ClientDispatcherThread(Socket socket, ClientKeepAliveThread keepAlive){

        // set keep-alive thread
        keepAliveThread = keepAlive;

        // set the socket for the client/server connection
        this.socket = socket;

        try {
            // set the inputstream of the socket.
            inputStream = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set up the thread
        clientDispatcher = new Thread(this);
        clientDispatcher.start();
    }


    @Override
    public void run() {

        // check keepaliveThread

        try {
            Message readMessage = (Message) inputStream.readObject();

            // put message on clientcore mainQueue
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}