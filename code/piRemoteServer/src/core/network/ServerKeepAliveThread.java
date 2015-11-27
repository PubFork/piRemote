package core.network;

import MessageObject.Message;
import core.ServerCore;

import java.lang.Thread;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * created by fabian on 13.11.15
 */

public class ServerKeepAliveThread implements Runnable {

    private final ServerDispatcherThread dispatcherThread;
    private final ServerSenderThread senderThread;
    private final HashMap<UUID, NetworkInfo> sessionTable;
    private final Thread keepAliveThread;

    private final long INTERVAL = 1000;
    private final long ALLOWED_DROPS = 3;
    private final long TIMEOUT = ALLOWED_DROPS * INTERVAL;

    /**
     * Default constructor for the KeepAliveThread on the Server.
     * @param dispatcherThread DispatcherThread of the server, required for accessing the morgueQueue
     * @param sessionTable
     */
    public ServerKeepAliveThread(ServerDispatcherThread dispatcherThread, ServerSenderThread senderThread, HashMap<UUID, NetworkInfo> sessionTable) {
        this.dispatcherThread = dispatcherThread;
        this.senderThread = senderThread;
        this.sessionTable = sessionTable;

        keepAliveThread = new Thread(this);
        //keepAliveThread.start();
    }

    @Override
    public void run() {
        while (ServerNetwork.isRunning()) {
            for (Map.Entry<UUID, NetworkInfo> entry : sessionTable.entrySet()) {
                // Iterate over all elements of the sessionTable
                long stillAlive = System.currentTimeMillis() - entry.getValue().getLastSeen();
                if (stillAlive < TIMEOUT) {
                    // If the server<->client link hasn't timed out yet, place a keep alive message on
                    // the sending queue.
                    Message keepAlive = new Message(entry.getKey(), ServerCore.getState().getServerState(), ServerCore.getState().getApplicationState());
                    senderThread.getQueue().add(keepAlive);
                } else {
                    // Else set the session to be trashed.
                    Session deadSession = new Session(entry.getKey(), entry.getValue());
                    dispatcherThread.getQueue().add(deadSession);
                }
            }

            // Wait INTERVAL time before checking for a need to resend a keep-alive.
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns direct reference to the keepAliveThread.
     * @return Direct reference to keepAliveThread.
     */
    public Thread getThread() {
        return keepAliveThread;
    }
}