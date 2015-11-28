package core.network;

import MessageObject.Message;
import core.ServerCore;

import java.lang.Thread;
import java.util.Map;
import java.util.UUID;


/**
 * created by fabian on 13.11.15
 */

public class KeepAliveService implements Runnable {

    private final ServerNetwork serverNetwork;
    private final DispatcherService dispatcherService;
    private final SenderService senderService;
    private final Thread keepAliveThread;

    private final long INTERVAL = 1500;
    private final long ALLOWED_DROPS = 4;
    private final long TIMEOUT = ALLOWED_DROPS * INTERVAL;

    /**
     * Default constructor for the KeepAliveThread on the Server.
     * @param serverNetwork Reference to the main network.
     * @param dispatcherService Reference to the network's dispatcher.
     * @param senderService Reference to the network's sender.
     */
    public KeepAliveService(ServerNetwork serverNetwork, DispatcherService dispatcherService, SenderService senderService) {
        this.serverNetwork = serverNetwork;
        this.dispatcherService = dispatcherService;
        this.senderService = senderService;

        this.keepAliveThread = new Thread(this);
        //keepAliveThread.start();
    }

    @Override
    public void run() {
        while (serverNetwork.isRunning()) {
            for (Map.Entry<UUID, NetworkInfo> entry : serverNetwork.getSessionTable().entrySet()) {
                // Iterate over all elements of the sessionTable
                long stillAlive = System.currentTimeMillis() - entry.getValue().getLastSeen();
                if (stillAlive < TIMEOUT) {
                    // If the server<->client link hasn't timed out yet, place a keep alive message on
                    // the sending queue.
                    Message keepAlive = new Message(entry.getKey(), ServerCore.getState());
                    senderService.getQueue().add(keepAlive);
                } else {
                    // Else set the session to be trashed.
                    Session deadSession = new Session(entry.getKey(), entry.getValue());
                    dispatcherService.getQueue().add(deadSession);
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