package ch.ethz.inf.vs.piremote.core.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import ConnectionManagement.Connection;
import MessageObject.Message;
import SharedConstants.CoreCsts;
import ch.ethz.inf.vs.piremote.core.ClientCore;

public class ClientNetwork implements Runnable {

    @NonNull
    private final ClientCore clientCore;

    @Nullable
    private DispatcherService dispatcherService;
    @Nullable
    private KeepAliveService keepAliveService;
    @Nullable
    private SenderService senderService;

    @Nullable
    private UUID uuid;
    @Nullable
    private DatagramSocket socket;
    private Thread networkThread;

    @NonNull
    private final InetAddress address;
    @NonNull
    private final LinkedBlockingQueue<Message> mainQueue;
    private final int defaultPort;

    @NonNull
    private final AtomicBoolean running = new AtomicBoolean(false);
    @NonNull
    private final AtomicBoolean senderConstructed = new AtomicBoolean(false);
    @NonNull
    private final AtomicBoolean dispatcherConstructed = new AtomicBoolean(false);
    @NonNull
    private final AtomicBoolean keepAliveConstructed = new AtomicBoolean(false);

    private final String INFO_TAG = "# Network #";
    private final String DEBUG_TAG = "# Network DEBUG #";
    private final String ERROR_TAG = "# Network ERROR #";
    private final String WTF_TAG = "# Network WTF #";
    private final String WARN_TAG = "# Network WARN #";
    private final String VERBOSE_TAG = "# Network VERBOSE #";

    /**
     * create a ClientNetwork object that has several threads. This constructor is called
     * from the ClientCore to build its network. This constructor also starts the threads!
     *
     * @param serverAddress core needs to provide the address of the server
     * @param port          core also needs to provide the port of the server
     * @param core          reference to the core provides access to the mainQueue (on which the dispatcher will put the messages for the core)
     */
    public ClientNetwork(@NonNull InetAddress serverAddress, int port, @NonNull ClientCore core) {
        address = serverAddress;
        defaultPort = port;
        clientCore = core;
        mainQueue = clientCore.getMainQueue();
        Log.i(INFO_TAG, "Construction complete.");
    }

    @Override
    public void run() {
        Log.v(VERBOSE_TAG, "Starting network. Finding port.");
        startSocket(defaultPort);
        Log.v(VERBOSE_TAG, "Set up a port.");
        running.set(true);

        // Initialise the services of the network.
        Log.v(VERBOSE_TAG, "Constructing services.");
        senderService = new SenderService(this);
        dispatcherService = new DispatcherService(this);
        keepAliveService = new KeepAliveService(this, dispatcherService, senderService);

        // Start the services of the network.
        Log.v(VERBOSE_TAG, "Starting services.");
        senderService.startThread();
        keepAliveService.startThread();
        dispatcherService.startThread();
        Log.i(INFO_TAG, "Startup completed.");
    }

    /**
     * Creates a socket bound to port 'number' if possible, else gets any open port.
     *
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

    @NonNull
    public ClientCore getClientCore() {
        return clientCore;
    }

    /**
     * Returns direct reference of the dispatcher object.
     *
     * @return Direct reference of dispatcher object.
     */
    @Nullable
    public DispatcherService getDispatcherService() {
        return dispatcherService;
    }

    /**
     * Returns direct reference of the sender object.
     *
     * @return Direct reference of sender object.
     */
    @Nullable
    public SenderService getSenderService() {
        return senderService;
    }

    /**
     * Returns direct reference of the keep alive object.
     *
     * @return Direct reference of keep alive object.
     */
    @Nullable
    public KeepAliveService getKeepAliveService() {
        return keepAliveService;
    }

    /**
     * Returns direct reference of the SendingQueue if the SenderThread is running.
     *
     * @return SendingQueue if SenderThread exists, else null.
     */
    @Nullable
    public BlockingQueue<Object> getSendingQueue() {
        if (senderService == null) {
            return null;
        }
        return senderService.getQueue();
    }

    /**
     * Returns direct reference to the client's receiving queue.
     *
     * @return Direct reference to receiving queue.
     */
    @NonNull
    public BlockingQueue<Message> getMainQueue() {
        return mainQueue;
    }

    /**
     * Connect to server.
     */
    public void connectToServer() {
        Connection request = new Connection();
        request.requestConnection();

        // put connection request on sendingQueue
        putOnSendingQueue(request);
    }

    /**
     * Internal method to try adding to sendingQueue.
     *
     * @param request Object to put on the queue.
     * @return 0 on success, -1 if queue doesn't exist, -2 is sender not initialised.
     */
    private int addToSendingQueue(Object request) {
        if (senderConstructed.get()) {
            BlockingQueue<Object> queue = getSendingQueue();
            if (queue != null) {
                queue.add(request);
                return 0;
            }
            return -1;
        }
        return -2;
    }

    /**
     * Try adding an object to the sendingQueue until it succeeds.
     *
     * @param obj Object to put onto the queue.
     */
    public void putOnSendingQueue(Object obj) {
        int putOnQueue = 1;
        while (putOnQueue != 0) {
            putOnQueue = addToSendingQueue(obj);
        }
    }

    /**
     * Try adding an object to the mainQueue.
     *
     * @param msg Message to add to mainQueue.
     * @throws InterruptedException
     */
    void putOnMainQueue(Message msg) throws InterruptedException {
        mainQueue.put(msg);
    }

    /**
     * Disconnect from server.
     */
    public void disconnectFromServer() {
        Connection disconnectRequest = new Connection();
        disconnectRequest.disconnect(uuid);

        // Send disconnection request to the server.
        putOnSendingQueue(disconnectRequest);
        // Send a poison pill to the server.
        putOnSendingQueue(getSenderService());

        running.set(false);

        // Notify ClientCore that the connection to the server has been terminated. Set the status
        // of the client appropriately.
        Message disconnectServer = new Message(uuid, CoreCsts.ServerState.SERVER_DOWN, null, null);
        mainQueue.add(disconnectServer);
        dispatcherService.interruptThread();
        while (!getSendingQueue().isEmpty()) { }
        socket.close();
        uuid = null;
    }

    /**
     * Return whether ClientNetwork is running or not.
     *
     * @return Returns true if the ClientNetwork is running, else false.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Returns the port the network is communicating.
     *
     * @return Returns the port the network is communicating from if it exists, else returns '-2'
     */
    public int getPort() {
        if (socket == null) {
            return -2;
        }
        return socket.getLocalPort();
    }

    /**
     * Returns the server's address.
     *
     * @return Address of the server the client is attached to.
     */
    @NonNull
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Returns the socket instance of the Network once the network-thread has been started.
     *
     * @return Socket the Network is attached to.
     */
    @Nullable
    public DatagramSocket getSocket() {
        return socket;
    }

    /**
     * Returns the Client's UUID.
     *
     * @return UUID of the client.´
     */
    @Nullable
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of the Client.
     *
     * @param newUUID new UUID to set the old UUID to.
     */
    public void setUuid(@Nullable UUID newUUID) {
        uuid = newUUID;
    }

    /**
     * Method to start the Network part handling all communication.
     */
    public void startNetwork() {
        networkThread = new Thread(this);
        networkThread.start();
    }

    /**
     * Internal method called by the senderService, used for synchronisation.
     */
    void setSenderConstructed() {
        senderConstructed.set(true);
    }

    /**
     * Internal method called by the dispatcherService, used for synchronisation.
     */
    void setDispatcherConstructed() {
        dispatcherConstructed.set(true);
    }

    /**
     * Internal method called by the keepAliveService, used for synchronisation.
     */
    void setKeepAliveConstructed() {
            keepAliveConstructed.set(true);
    }
}