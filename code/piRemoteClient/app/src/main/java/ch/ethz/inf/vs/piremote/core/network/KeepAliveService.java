package ch.ethz.inf.vs.piremote.core.network;

import android.support.annotation.NonNull;
import android.util.Log;

import MessageObject.Message;
import NetworkConstants.NetworkConstants;
import ch.ethz.inf.vs.piremote.core.ClientCore;

import java.lang.Thread;

/**
 * created by fabian on 13.11.15
 */

public class KeepAliveService implements Runnable {

    private final ClientNetwork clientNetwork;
    private final DispatcherService dispatcherService;
    private final SenderService senderService;
    @NonNull
    private final Thread keepAliveThread;


    /**
     * Default constructor for the KeepAliveThread on the Client.
     * @param clientNetwork Reference to the main network.
     * @param dispatcherService Reference to the network's dispatcher.
     * @param senderService Reference to the network's sender.
     */
    public KeepAliveService(ClientNetwork clientNetwork, DispatcherService dispatcherService, SenderService senderService) {
        this.clientNetwork = clientNetwork;
        this.dispatcherService = dispatcherService;
        this.senderService = senderService;

        this.keepAliveThread = new Thread(this);
        // keepAliveThread.start();
    }

    @Override
    public void run() {
        while (clientNetwork.isRunning()) {
            long stillAlive = System.currentTimeMillis() - dispatcherService.getLastSeen();
            if (stillAlive < NetworkConstants.TIMEOUT) {
                // If the server<->client link hasn't timed out yet, place a keep alive message on
                // the sending queue.
                Message keepAlive = new Message(clientNetwork.getUuid(), clientNetwork.getClientCore().getState());
                senderService.getQueue().add(keepAlive);
                Log.d("## KeepAlive ##", "Sending keep alive to server");
            } else {
                // Else let us reset our application's state.
                clientNetwork.disconnectFromServer();
                Log.d("## KeepAlive ##", "Server didn't answer, resetting application state.");
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
     * Returns direct reference to the keepAliveThread.
     * @return Direct reference to keepAliveThread.
     */
    @NonNull
    public Thread getThread() {
        return keepAliveThread;
    }
}