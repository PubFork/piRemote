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

    private DispatcherService dispatcherService;
    private KeepAliveService keepAliveService;
    private SenderService senderService;

    public static UUID uuid;
    public static AtomicBoolean running;
    public static DatagramSocket socket;

    private final Thread networkThread;
    private final InetAddress address;
    private final int defaultPort;
    private final LinkedBlockingQueue mainQueue;

    /**
     * create a ClientNetwork object that has several threads. This constructor is called
     * from the ClientCore to build its network. This constructor also starts the threads!
     * @param address core needs to provide the address of the server
     * @param port core also needs to provide the port of the server
     * @param mainQueue Queue on which the dispatcher will put the messages for the core
     */
    public ClientNetwork(InetAddress address, int port, LinkedBlockingQueue mainQueue) {
        this.address = address;
        this.defaultPort = port;
        this.mainQueue = mainQueue;

        this.uuid = null;
        this.running = new AtomicBoolean(false);
        this.networkThread = new Thread(this);
        //this.networkThread.start();
    }

    @Override
    public void run() {
        startSocket(defaultPort);
        running.set(true);

        // initialize the threads
        senderService = new SenderService(this);
        dispatcherService = new DispatcherService(socket, address, mainQueue);
        keepAliveService = new KeepAliveService(this, dispatcherService, senderService);

        // start threads
        senderService.getThread().start();
        keepAliveService.getThread().start();
        dispatcherService.getThread().start();
    }

    /**
     * Creates a socket bound to port 'number' if possible, else gets any open port.
     * @param number Port on which the socket should bind to.
     */
    private void startSocket(int number) {
        try {
            socket = new DatagramSocket(number);
        } catch (SocketException e) {
            try {
                socket = new DatagramSocket();
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Returns direct reference of the dispatcher object.
     * @return Direct reference of dispatcher object.
     */
    public DispatcherService getDispatcher() {
        return dispatcherService;
    }

    /**
     * Returns direct reference of the sender object.
     * @return Direct reference of sender object.
     */
    public SenderService getSender() {
        return senderService;
    }

    /**
     * Returns direct reference of the keep alive object.
     * @return Direct reference of keep alive object.
     */
    public KeepAliveService getKeepAlive() {
        return keepAliveService;
    }

    /**
     * Returns direct reference of the SendingQueue if the SenderThread is running.
     * @return SendingQueue if SenderThread exists, else null.
     */
    public BlockingQueue<Object> getSendingQueue() {
        if (senderService == null) {
            return null;
        } else {
            return senderService.getQueue();
        }
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
        Message disconnectServer = new Message(ClientNetwork.uuid, CoreCsts.ServerState.SERVER_DOWN, null, null);
        dispatcherService.getCoreMainQueue().add(disconnectServer);

        running.set(false);
        this.uuid = null;
    }

    /**
     * Return whether ClientNetwork is running or not.
     * @return Returns true if the ClientNetwork is running, else false.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Returns the port the network is attached to.
     * @return Port the network is attached to.
     */
    public int getPort() {
        return socket.getPort();
    }

    /**
     * Returns the server's address.
     * @return InetAddress of the server the client is attached to.
     */
    public InetAddress getInetAddress() {
        return this.address;
    }

    /**
     * Returns the socket of the Network.
     * @return Socket the Network is attached to.
     */
    public DatagramSocket getSocket() {
        return this.socket;
    }
}