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
    // private ClientCore clientCore;

    // constructor
    public ClientDispatcherThread(Socket socket, ClientKeepAliveThread keepAlive){

        // set keep-alive thread
        keepAliveThread = keepAlive;

        // set the socket for the client/server connection
        this.socket = socket;
    }


    @Override
    public void run() {

    }
}