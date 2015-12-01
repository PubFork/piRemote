package core.network;

import MessageObject.Message;
import NetworkConstants.NetworkConstants;
import core.ServerCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

class KeepAliveService implements Runnable {

    @NotNull
    private final ServerNetwork serverNetwork;
    @NotNull
    private final DispatcherService dispatcherService;
    @NotNull
    private final SenderService senderService;
    @Nullable
    private Thread keepAliveThread;

    /**
     * Default constructor for the KeepAliveThread on the Server.
     *
     * @param network    Reference to the main network.
     * @param dispatcher Reference to the network's dispatcher.
     * @param sender     Reference to the network's sender.
     */
    KeepAliveService(@NotNull ServerNetwork network, @NotNull DispatcherService dispatcher, @NotNull SenderService sender) {
        serverNetwork = network;
        dispatcherService = dispatcher;
        senderService = sender;
        network.setKeepAliveConstructed();
    }

    @Override
    public void run() {
        while (serverNetwork.isRunning()) {
            for (Map.Entry<UUID, NetworkInfo> entry : serverNetwork.getSessionTable().entrySet()) {
                // Iterate over all elements of the sessionTable
                long stillAlive = System.currentTimeMillis() - entry.getValue().getLastSeen();
                if (stillAlive < NetworkConstants.TIMEOUT) {
                    // If the server<->client link hasn't timed out yet, place a keep alive message on
                    // the sending queue.
                    Message keepAlive = new Message(entry.getKey(), ServerCore.getState());
                    senderService.putOnQueue(keepAlive);
                } else {
                    // Else notify the dispatcherService to delete the Session.
                    Session deadSession = new Session(entry.getKey(), entry.getValue());
                    dispatcherService.putOnQueue(deadSession);
                }
            }

            // Wait INTERVAL time before checking for a need to resend a keep-alive.
            try {
                Thread.sleep(NetworkConstants.INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Start the keepAliveThread.
     */
    void startThread() {
        keepAliveThread = new Thread(this);
        keepAliveThread.start();
    }
}