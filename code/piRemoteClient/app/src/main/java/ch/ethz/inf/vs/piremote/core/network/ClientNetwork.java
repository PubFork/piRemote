package ch.ethz.inf.vs.piremote.core.network;

import ConnectionManagement.Connection;
import MessageObject.Message;
import SharedConstants.CoreCsts;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * created by fabian on 13.11.15
 */
public class ClientNetwork implements Runnable{
    //TODO(Mickey) Add proper Android logging

    private ClientDispatcherThread dispatcherThread;
    private ClientKeepAliveThread keepAliveThread;
    private ClientSenderThread senderThread;

    public static UUID uuid;
    public static AtomicBoolean running;
    public static DatagramSocket socket;

    private final Thread networkThread;
    private final InetAddress address;
    private final int port;
    private final LinkedBlockingQueue mainQueue;

    /**
     * create a ClientNetwork object that has several threads. This constructor is called
     * from the ClientCore to build its network. This constructor also starts the threads!
     * @param address core needs to provide the address of the server
     * @param port core also needs to provide the port of the server
     * @param mainQueue mainqueue on which the dispatcher will put the messages for the core
     */
    public ClientNetwork(InetAddress address, int port, LinkedBlockingQueue mainQueue) {
        this.address = address;
        this.port = port;
        this.mainQueue = mainQueue;

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
        uuid = null;

        // initialize the threads
        senderThread = new ClientSenderThread(socket, address);
        keepAliveThread = new ClientKeepAliveThread(senderThread);
        dispatcherThread = new ClientDispatcherThread(socket, address, mainQueue);

        // start threads
        senderThread.getThread().start();
        dispatcherThread.getThread().start();
        keepAliveThread.getThread().start();
    }

    /**
     * Returns direct reference of the SenderThread.
     * @return Direct reference of SenderThread.
     */
    public ClientSenderThread getSenderThread() {
        return senderThread;
    }

    /**
     * Returns direct reference of the SendingQueue if the SenderThread is running.
     * @return SendingQueue if SenderThread exists, else null.
     */
    public BlockingQueue<Object> getSendingQueue() {
        if (senderThread == null) {
            return null;
        } else {
            return senderThread.getSendingQueue();
        }
    }

    /**
     * Return whether ClientNetwork is running or not.
     * @return Returns true if the ClientNetwork is running, else false.
     */
    public static boolean isRunning() {
        return running.get();
    }

    /**
     * Connect to server.
     */
    public void connectToServer() {
        Connection request = new Connection();
        request.requestConnection();

        // put connection request on sendingQueue
        getSendingQueue().add(request);
    }

    /**
     * Disconnect from server.
     */
    public void disconnectFromServer() {
        Connection disconnectRequest = new Connection();
        disconnectRequest.disconnect(uuid);

        // Send disconnection request to the server.
        getSendingQueue().add(disconnectRequest);

        // Notify ClientCore that the connection to the server has been terminated. Set the status
        // of the client appropriately.
        Message disconnectServer = new Message(ClientNetwork.uuid, CoreCsts.ServerState.SERVER_DOWN, null);
        ClientDispatcherThread.getCoreMainQueue().add(disconnectServer);

        running.set(false);
        this.uuid = null;
    }
}