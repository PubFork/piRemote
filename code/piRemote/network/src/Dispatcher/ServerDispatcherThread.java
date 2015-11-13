package Dispatcher;

import KeepAlive.ServerKeepAliveThread;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * created by fabian on 13.11.15
 */

public class ServerDispatcherThread extends AbstractDispatcherThread {

    private ServerKeepAliveThread keepAliveThread;
    private Thread serverDispatcher;
    private ServerSocket serverSocket;

    public ServerDispatcherThread() {

        keepAliveThread = new ServerKeepAliveThread();

        try {
            serverSocket = new ServerSocket(1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}