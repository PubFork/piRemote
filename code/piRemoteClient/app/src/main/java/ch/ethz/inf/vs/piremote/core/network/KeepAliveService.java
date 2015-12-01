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

    private final String INFO_TAG = "# KeepAlive #";
    private final String DEBUG_TAG = "# KeepAlive DEBUG #";
    private final String ERROR_TAG = "# KeepAlive ERROR #";
    private final String WTF_TAG = "# KeepAlive WTF #";
    private final String WARN_TAG = "# KeepAlive WARN #";
    private final String VERBOSE_TAG = "# KeepAlive VERBOSE #";


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
        Log.i(INFO_TAG, "Service constructed.");
    }

    @Override
    public void run() {
        while (clientNetwork.isRunning()) {
            Log.v(VERBOSE_TAG, "Woke up.");
            long stillAlive = System.currentTimeMillis() - dispatcherService.getLastSeen();
            if (stillAlive < NetworkConstants.TIMEOUT) {
                // If the server<->client link hasn't timed out yet, place a keep alive message on
                // the sending queue.
                Message keepAlive = new Message(clientNetwork.getUuid(), clientNetwork.getClientCore().getState());
                clientNetwork.putOnSendingQueue(keepAlive);
                Log.d(DEBUG_TAG, "Sending keep alive to server");
            } else {
                // Else let us reset our application's state.
                clientNetwork.disconnectFromServer();
                Log.d(DEBUG_TAG, "Server didn't answer, resetting application state.");
            }

            // Wait INTERVAL time before checking for a need to resend a keep-alive.
            try {
                Log.v(VERBOSE_TAG, "Going to sleep.");
                Thread.sleep(NetworkConstants.INTERVAL);
            } catch (InterruptedException e) {
                Log.e(ERROR_TAG, "Thread has been interrupted.");
                e.printStackTrace();
            }
        }
        Log.i(INFO_TAG, "Service ended.");
    }

    /**
     * Start the keepAliveThread.
     */
    void startThread() {
        keepAliveThread = new Thread(this);
        keepAliveThread.start();
        Log.i(INFO_TAG, "Service started.");
    }
}