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
    private static long lastSeen;

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
        // start it when connecting
        // clientDispatcher.start();
    }


    @Override
    public void run() {

        while (ClientNetwork.running) {
            try {

                /**
                 * read a message from the inputstream an forward it to the
                 * main Queue of the ClientCore
                 */
                Message readMessage = (Message) inputStream.readObject();
                lastSeen = System.currentTimeMillis();

                if (ClientNetwork.uuid == null) {
                    // set uuid when connected
                    ClientNetwork.uuid = readMessage.getUuid();
                }

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

    public static long getLastSeen() {
        return  lastSeen;
    }

    public Thread getThread() {
        return clientDispatcher;
    }
}