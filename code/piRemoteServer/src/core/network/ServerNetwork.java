package core.network;

import MessageObject.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Fabian on 13.11.15.
 */
public class ServerNetwork {

    private ServerDispatcherThread dispatcherThread;
    private ServerKeepAliveThread keepAliveThread;
    private ServerSenderThread senderThread;
    private HashMap<UUID, NetworkInfo> sessionTable = new HashMap<>();

    private ServerSocket socket;

    private static boolean serverRunning;

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

        serverRunning = true;
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

    public BlockingQueue<Message> getSendingQueue(){
        if(senderThread==null) {
            return null;
        }
        return senderThread.getSendingQueue();
    }

    public List<Session> getmorgueQueue(){
        if(dispatcherThread==null) {
            return null;
        }
        return dispatcherThread.getmorgueQueue();
    }


        public static boolean isRunning () {
        return serverRunning;
    }
}
