package core.network;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;

/**
 * created by fabian on 13.11.15
 */

public class ServerDispatcherThread implements Runnable {

    private ServerKeepAliveThread keepAliveThread;
    private Thread serverDispatcher;
    private ServerSocket serverSocket;
    private HashMap<UUID, NetworkInfo> sessionTable;

    public ServerDispatcherThread(ServerSocket socket, ServerKeepAliveThread keepAlive, HashMap sTable) {

        keepAliveThread = keepAlive;
        serverSocket = socket;
        sessionTable = sTable;

        serverDispatcher = new Thread(this);
        serverDispatcher.start();
    }

    @Override
    public void run() {

    }
}