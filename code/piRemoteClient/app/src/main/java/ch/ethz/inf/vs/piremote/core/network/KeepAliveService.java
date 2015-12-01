package ch.ethz.inf.vs.piremote.core.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import MessageObject.Message;
import NetworkConstants.NetworkConstants;

public class KeepAliveService implements Runnable {

    @NonNull
    private final ClientNetwork clientNetwork;
    @NonNull
    private final DispatcherService dispatcherService;
    @NonNull
    private final SenderService senderService;
    @Nullable
    private Thread keepAliveThread;


    /**
     * Default constructor for the KeepAliveThread on the Client.
     *
     * @param network    Reference to the main network.
     * @param dispatcher Reference to the network's dispatcher.
     * @param sender     Reference to the network's sender.
     */
    KeepAliveService(@NonNull ClientNetwork network, @NonNull DispatcherService dispatcher, @NonNull SenderService sender) {
        clientNetwork = network;
        dispatcherService = dispatcher;
        senderService = sender;
        network.setKeepAliveConstructed();
    }

    @Override
    public void run() {
        while (clientNetwork.isRunning()) {
            long stillAlive = System.currentTimeMillis() - dispatcherService.getLastSeen();
            if (stillAlive < NetworkConstants.TIMEOUT) {
                // If the server<->client link hasn't timed out yet, place a keep alive message on
                // the sending queue.
                Message keepAlive = new Message(clientNetwork.getUuid(), clientNetwork.getClientCore().getState());
                clientNetwork.putOnSendingQueue(keepAlive);
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
     * Start the keepAliveThread.
     */
    void startThread() {
        keepAliveThread = new Thread(this);
        keepAliveThread.start();
    }
}