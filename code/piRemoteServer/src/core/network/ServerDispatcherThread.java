package core.network;

import MessageObject.Message;
import MessageObject.PayloadObject.Payload;
import SharedConstants.ApplicationCsts;
import SharedConstants.CoreCsts;
import com.sun.corba.se.spi.activation.Server;
import core.ServerCore;

import java.net.ServerSocket;
import java.util.*;

/**
 * created by fabian on 13.11.15
 */

public class ServerDispatcherThread implements Runnable {

    // private ServerKeepAliveThread keepAliveThread;
    private List<NetworkInfo> morgueQueue = new ArrayList<>();
    private Thread serverDispatcher;
    private ServerSocket serverSocket;
    private HashMap<UUID, NetworkInfo> sessionTable;

    public ServerDispatcherThread(ServerSocket socket, HashMap sTable) {

        serverSocket = socket;
        sessionTable = sTable;

        serverDispatcher = new Thread(this);
        serverDispatcher.start();
    }

    @Override
    public void run() {

        // TODO: How to read from ServerSocket?
        Message receivedMessage = null; // need to be the received Message!
        UUID uuid = receivedMessage.getUuid();

        // TODO: check morgeQueue

        // check the sessionTable
        if (!sessionTable.containsKey(uuid)) {
            // TODO: how to get ip and port from client?
            NetworkInfo clientInfo = new NetworkInfo(1234, 1234, System.currentTimeMillis());
            sessionTable.put(uuid, clientInfo);
        } else {
            // update lastSeen
            sessionTable.get(uuid).lastSeen = System.currentTimeMillis();
        }


        try {
            // TODO: first check if it is a FilePickerRequest

            // put message on mainQueue from Core
            ServerCore.mainQueue.put(receivedMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}