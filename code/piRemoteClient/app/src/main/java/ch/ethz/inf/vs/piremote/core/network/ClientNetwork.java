package ch.ethz.inf.vs.piremote.core.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ConnectionManagement.Connection;
import MessageObject.Message;
import SharedConstants.CoreCsts;
import ch.ethz.inf.vs.piremote.core.ClientCore;

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
    @NonNull
    private final AtomicBoolean running;
    @Nullable
    private DatagramSocket socket;

    @NonNull
    private final Thread networkThread;
    @NonNull
    private final InetAddress address;
    @NonNull
    private final LinkedBlockingQueue<Message> mainQueue;
    private final int defaultPort;

    /**
     * create a ClientNetwork object that has several threads. This constructor is called
     * from the ClientCore to build its network. This constructor also starts the threads!
     * @param address core needs to provide the address of the server
     * @param port core also needs to provide the port of the server
     * @param clientCore reference to the core provides access to the mainQueue (on which the dispatcher will put the messages for the core)
     */
    public ClientNetwork(@NonNull InetAddress address, int port, @NonNull ClientCore clientCore) {
        this.address = address;
        this.defaultPort = port;
        this.clientCore = clientCore;
        this.mainQueue = clientCore.getMainQueue();

        this.uuid = null;
        this.running = new AtomicBoolean(false);
        this.networkThread = new Thread(this);
        //this.networkThread.start();
    }

    @Override
    public void run() {
        startSocket(defaultPort);
        running.set(true);

        // Initialise the services of the network.
        senderService = new SenderService(this);
        dispatcherService = new DispatcherService(this);
        keepAliveService = new KeepAliveService(this, dispatcherService, senderService);

        // Start the services of the network.
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

    @NonNull
    public ClientCore getClientCore() {
        return clientCore;
    }

    /**
     * Returns direct reference of the dispatcher object.
     * @return Direct reference of dispatcher object.
     */
    @Nullable
    public DispatcherService getDispatcher() {
        return dispatcherService;
    }

    /**
     * Returns direct reference of the sender object.
     * @return Direct reference of sender object.
     */
    @Nullable
    public SenderService getSender() {
        return senderService;
    }

    /**
     * Returns direct reference of the keep alive object.
     * @return Direct reference of keep alive object.
     */
    @Nullable
    public KeepAliveService getKeepAlive() {
        return keepAliveService;
    }

    /**
     * Returns direct reference of the SendingQueue if the SenderThread is running.
     * @return SendingQueue if SenderThread exists, else null.
     */
    @Nullable
    public BlockingQueue<Object> getSendingQueue() {
        if (senderService == null) {
            return null;
        } else {
            return senderService.getQueue();
        }
    }

    /**
     * Returns direct reference to the client's receiving queue.
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
        Message disconnectServer = new Message(uuid, CoreCsts.ServerState.SERVER_DOWN, null, null);
        mainQueue.add(disconnectServer);

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
     * Returns the port the network is communicating.
     * @return Returns the port the network is communicating from if it exists, else returns '-1'
     */
    public int getPort() {
        if(socket == null) {
            return -1;
        }
        return socket.getPort();
    }

    /**
     * Returns the server's address.
     * @return Address of the server the client is attached to.
     */
    @NonNull
    public InetAddress getAddress() {
        return this.address;
    }

    /**
     * Returns the socket of the Network.
     * @return Socket the Network is attached to.
     */
    @Nullable
    public DatagramSocket getSocket() {
        return this.socket;
    }

    /**
     * Returns the Client's UUID.
     * @return UUID of the client.´
     */
    @Nullable
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of the Client.
     * @param uuid new UUID to set the old UUID to.
     */
    public void setUuid(@Nullable UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Method to start the Network part handling all communication.
     */
    public void startNetwork(){
        this.networkThread.start();
    }
}