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

    private DispatcherService dispatcherService;
    private KeepAliveService keepAliveService;
    private SenderService senderService;

    private AtomicBoolean running;
    private DatagramSocket socket;

    private final Thread networkThread;
    private final int defaultPort;
    private final HashMap<UUID, NetworkInfo> sessionTable;

    /**
     * Default constructor for creating the ServerNetwork and all its components.
     * @param port Port on which the server communicates.
     */
    public ServerNetwork(int port) {
        this.defaultPort = port;

        this.sessionTable = new HashMap<>();
        this.running = new AtomicBoolean(false);
        this.networkThread = new Thread(this);
        // this.networkThread.start();
    }


    @Override
    public void run() {
        startSocket(defaultPort);
        running.set(true);

        // Initialise the services of the network.
        senderService = new SenderService(this);
        dispatcherService = new DispatcherService(getPort(), sessionTable, senderService);
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
    public BlockingQueue<Message> getSendingQueue(){
        if(senderService == null) {
            return null;
        }
        return senderService.getQueue();
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
        if(dispatcherService == null) {
            return null;
        }
        return dispatcherService.getQueue();
    }

    /**
     * Return ServerNetwork's running status.
     * @return Returns true if the ServerNetwork is running, else false.
     */
    public boolean isRunning () {
        return running.get();
    }

    /**
     * Returns the port the network is receiving from.
     * @return Port the network is receiving from.
     */
    public int getPort() {
        return socket.getPort();
    }
}