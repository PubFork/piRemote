package core.network;

import MessageObject.Message;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Fabian on 13.11.15.
 */
public class ServerNetwork implements Runnable {

    private ServerDispatcherThread dispatcherThread;
    private ServerKeepAliveThread keepAliveThread;
    private ServerSenderThread senderThread;

    private static AtomicBoolean running;
    private DatagramSocket socket;

    private final Thread networkThread;
    private final int port;
    private final HashMap<UUID, NetworkInfo> sessionTable;

    /**
     * Default constructor for creating the ServerNetwork and all its components.
     * @param port Port on which the server communicates.
     */
    public ServerNetwork(int port) {
        this.port = port;

        this.sessionTable = new HashMap<>();
        this.running = new AtomicBoolean(false);
        this.networkThread = new Thread(this);
        this.networkThread.start();
    }


    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        running.set(true);

        // Initialise the threads of the network.
        senderThread = new ServerSenderThread(sessionTable);
        dispatcherThread = new ServerDispatcherThread(port, sessionTable, senderThread);
        keepAliveThread = new ServerKeepAliveThread(dispatcherThread, senderThread, sessionTable);

        // Start the threads of the network.
        senderThread.getThread().start();
        dispatcherThread.getThread().start();
        keepAliveThread.getThread().start();
    }


    /**
     * Returns direct reference of the SenderThread.
     * @return Direct reference of SenderThread.
     */
    public ServerSenderThread getSenderThread() {
        return senderThread;
    }

    /**
     * Returns direct reference of the SendingQueue if the SenderThread is running.
     * @return SendingQueue if SenderThread exists, else null.
     */
    public BlockingQueue<Message> getSendingQueue(){
        if(senderThread == null) {
            return null;
        }
        return senderThread.getQueue();
    }

    /**
     * Returns direct reference of the SessionTable.
     * @return Direct reference of SessionTable.
     */
    public HashMap<UUID, NetworkInfo> getSessionTable() {
        return sessionTable;
    }

    /**
     * Return direct reference to queue of sessions to be terminated by the server.
     * @return MorgueQueue if DispatcherThread exists, else null.
     */
    public BlockingQueue<Session> getMorgueQueue(){
        if(dispatcherThread == null) {
            return null;
        }
        return dispatcherThread.getQueue();
    }

    /**
     * Returns direct reference of the KeepAliveThread.
     * @return Direct reference of KeepAliveThread.
     */
    public ServerKeepAliveThread getKeepAliveThread() {
        return keepAliveThread;
    }

    /**
     * Return whether ServerNetwork is running or not.
     * @return Returns true if the ServerNetwork is running, else false.
     */
    public static boolean isRunning () {
        return running.get();
    }
}