package Dispatcher;

import KeepAlive.ClientKeepAliveThread;

import java.io.IOException;
import java.net.Socket;

/**
 * created by fabian on 13.11.15
 */

public class ClientDispatcherThread extends AbstractDispatcherThread {

    private ClientKeepAliveThread keepAliveThread;
    private Thread clientDispatcher;
    private Socket socket;

    // constructor
    public ClientDispatcherThread(){

        // set keep-alive thread
        keepAliveThread = new ClientKeepAliveThread();

        // set the socket for the client/server connection
        try {
            // TODO: where get the server info?
            socket = new Socket("server", 1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

    }
}