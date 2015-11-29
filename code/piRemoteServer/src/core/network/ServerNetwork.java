package core.network;

import MessageObject.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerNetwork implements Runnable {

    @Nullable
    private DispatcherService dispatcherService;
    @Nullable
    private KeepAliveService keepAliveService;
    @Nullable
    private SenderService senderService;

    @NotNull
    private final AtomicBoolean running;

    private final int defaultPort;
    @NotNull
    private final HashMap<UUID, NetworkInfo> sessionTable;
    @NotNull
    private final Thread networkThread;

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
     * @return Direct reference of dispatcher object if it exists, else null.
     */
    @Nullable
    public DispatcherService getDispatcher() {
        return dispatcherService;
    }

    /**
     * Returns direct reference of the sender object.
     * @return Direct reference of sender object if it exists, else null.
     */
    @Nullable
    public SenderService getSender() {
        return senderService;
    }

    /**
     * Returns direct reference of the keep alive object.
     * @return Direct reference of keep alive object if it exists, else null.
     */
    @Nullable
    public KeepAliveService getKeepAlive() {
        return keepAliveService;
    }

    /**
     * Returns direct reference of the SendingQueue of SenderService.
     * @return SendingQueue of SenderService if it exists, else null.
     */
    @Nullable
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
    @NotNull
    public HashMap<UUID, NetworkInfo> getSessionTable() {
        return sessionTable;
    }


    /**
     * Returns direct reference of queue of sessions to be terminated by the server.
     * @return MorgueQueue of DispatcherService if it exists, else null.
     */
    @Nullable
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
     * @return Returns the port the network is receiving from if it exists, else returns '-1'
     */
    public int getPort() {
        if(dispatcherService == null) {
            return -1;
        }
        return dispatcherService.getPort();
    }

    /**
     * Method to start the Network part handling all communication.
     */
    public void startNetwork(){
        this.networkThread.start();
    }
}