package ch.ethz.inf.vs.piremote.core.network;

import ch.ethz.inf.vs.piremote.core.network.ClientKeepAliveThread;
import MessageObject.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * created by fabian on 13.11.15
 */

public class ClientDispatcherThread implements Runnable {

    private ClientKeepAliveThread keepAliveThread;
    private Thread clientDispatcher;
    private Socket socket;
    private ObjectInputStream inputStream;
    private BlockingQueue coreMainQueue;

    // constructor
    public ClientDispatcherThread(Socket socket, ClientKeepAliveThread keepAlive, LinkedBlockingQueue queue){

        // set keep-alive thread
        keepAliveThread = keepAlive;

        // set mainQueue from the core
        coreMainQueue = queue;

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

        // TODO: check keepaliveThread

        try {
            Message readMessage = (Message) inputStream.readObject();
            coreMainQueue.put(readMessage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}