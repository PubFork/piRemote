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

    @Nullable
    private Thread networkThread;

    @NotNull
    private final HashMap<UUID, NetworkInfo> sessionTable;
    private final int defaultPort;

    @NotNull
    private final AtomicBoolean running = new AtomicBoolean(false);
    @NotNull
    private final AtomicBoolean senderConstructed = new AtomicBoolean(false);
    @NotNull
    private final AtomicBoolean dispatcherConstructed = new AtomicBoolean(false);
    @NotNull
    private final AtomicBoolean keepAliveConstructed = new AtomicBoolean(false);


    /**
     * Default constructor for creating the ServerNetwork and all its components.
     *
     * @param port Port on which the server communicates.
     */
    public ServerNetwork(int port) {
        defaultPort = port;
        sessionTable = new HashMap<>();
    }


    @Override
    public void run() {
        running.set(true);

        // Initialise the services of the network.
        senderService = new SenderService(this);
        dispatcherService = new DispatcherService(defaultPort, this, senderService);
        keepAliveService = new KeepAliveService(this, dispatcherService, senderService);

        // Start the services of the network.
        senderService.startThread();
        keepAliveService.startThread();
        dispatcherService.startThread();
    }

    /**
     * Returns direct reference of the dispatcher object.
     *
     * @return Direct reference of dispatcher object if it exists, else null.
     */
    @Nullable
    public DispatcherService getDispatcherService() {
        return dispatcherService;
    }

    /**
     * Returns direct reference of the sender object.
     *
     * @return Direct reference of sender object if it exists, else null.
     */
    @Nullable
    public SenderService getSenderService() {
        return senderService;
    }

    /**
     * Returns direct reference of the keep alive object.
     *
     * @return Direct reference of keep alive object if it exists, else null.
     */
    @Nullable
    public KeepAliveService getKeepAliveService() {
        return keepAliveService;
    }

    /**
     * Returns direct reference of the SendingQueue of SenderService.
     *
     * @return SendingQueue of SenderService if it exists, else null.
     */
    @Nullable
    public BlockingQueue<Message> getSendingQueue() {
        if (senderService == null) {
            return null;
        }
        return senderService.getQueue();
    }

    /**
     * Returns direct reference of the SessionTable.
     *
     * @return Direct reference of SessionTable.
     */
    @NotNull
    public HashMap<UUID, NetworkInfo> getSessionTable() {
        return sessionTable;
    }


    /**
     * Returns direct reference of queue of sessions to be terminated by the server.
     *
     * @return MorgueQueue of DispatcherService if it exists, else null.
     */
    @Nullable
    public BlockingQueue<Session> getMorgueQueue() {
        if (dispatcherService == null) {
            return null;
        }
        return dispatcherService.getQueue();
    }


    /**
     * Returns ServerNetwork's running status.
     *
     * @return Returns true if the ServerNetwork is running, else false.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Returns the port the network is receiving from.
     *
     * @return Returns the port the network is receiving from if it exists, else returns '-3'
     */
    public int getPort() {
        if (dispatcherService == null) {
            return -3;
        }
        return dispatcherService.getPort();
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