package core.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Fabian on 13.11.15.
 */
public class ServerNetwork {

    private ServerDispatcherThread dispatcherThread;
    private ServerKeepAliveThread keepAliveThread;
    private ServerSenderThread senderThread;
    private HashMap<UUID, NetworkInfo> sessionTable = new HashMap<>();

    private ServerSocket socket;

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

        senderThread = new ServerSenderThread(socket, sessionTable);
        dispatcherThread = new ServerDispatcherThread(socket, sessionTable, senderThread);
        keepAliveThread = new ServerKeepAliveThread(dispatcherThread, sessionTable);
    }



    /**
     * use these getter-functions to get the fields you need.
     */

    public ServerSenderThread getSenderThread() {
        return senderThread;
    }

    public HashMap<UUID, NetworkInfo> getSessionTable() {
        return sessionTable;
    }

    public ServerKeepAliveThread getKeepAliveThread() {
        return keepAliveThread;
    }
}
