package core.network;

import MessageObject.Message;

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
        running.set(true);

        // Initialise the services of the network.
        senderService = new SenderService(this);
        dispatcherService = new DispatcherService(defaultPort, this, senderService);
        keepAliveService = new KeepAliveService(this, dispatcherService, senderService);

        // Start the services of the network.
        senderService.getThread().start();
        keepAliveService.getThread().start();
        dispatcherService.getThread().start();
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
     * Returns direct reference to queue of sessions to be terminated by the server.
     * @return MorgueQueue if DispatcherThread exists, else null.
     */
    public BlockingQueue<Session> getMorgueQueue(){
        if(dispatcherService == null) {
            return null;
        }
        return dispatcherService.getQueue();
    }

    /**
     * Returns ServerNetwork's running status.
     * @return Returns true if the ServerNetwork is running, else false.
     */
    public boolean isRunning () {
        return running.get();
    }

    /**
     * Returns the port the network is receiving from.
     * @return Returns the port the network is receiving from.
     */
    public int getPort() {
        return dispatcherService.getPort();
    }
}