package core.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Fabian on 13.11.15.
 */
public class ServerNetwork {

    ServerDispatcherThread dispatcherThread;
    ServerKeepAliveThread keepAliveThread;
    HashMap<UUID, NetworkInfo> sessionTable = new HashMap<>();

    ServerSocket socket;

    /**
     * Need to be called by the ServerCore to create the network part
     * @param port is needed to create the ServerSocket
     */
    public ServerNetwork(int port) {

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        keepAliveThread = new ServerKeepAliveThread();
        dispatcherThread = new ServerDispatcherThread(socket, keepAliveThread, sessionTable);
    }

}
