package ch.ethz.inf.vs.piremote.core.network;

import android.util.Log;

import MessageObject.Message;
import SharedConstants.CoreCsts;
import ch.ethz.inf.vs.piremote.core.ClientCore;

import java.lang.Thread;
import java.util.concurrent.BlockingQueue;

/**
 * created by fabian on 13.11.15
 */

public class ClientKeepAliveThread implements Runnable {

    private BlockingQueue<Object> sendingQueue;
    private Thread keepAliveThread;
    private final long INTERVAL = 1000;
    private final long ALLOWED_DROPS = 3;
    private final long TIMEOUT = ALLOWED_DROPS * INTERVAL;


    public ClientKeepAliveThread(ClientSenderThread sender) {
        sendingQueue = sender.getSendingQueue();

        keepAliveThread = new Thread(this);
        // start it when connecting
        // keepAliveThread.start();
    }

    @Override
    public void run() {
        while (ClientNetwork.running.get()) {
            long stillAlive = System.currentTimeMillis() - ClientDispatcherThread.getLastSeen();
            if (stillAlive < TIMEOUT) {
                // If the server<->client link hasn't timed out yet, place a keep alive message on
                // the sending queue.
                Message keepAlive = new Message(ClientNetwork.uuid, ClientCore.getState());
                sendingQueue.add(keepAlive);
                Log.d("## KeepAlive ##", "Sending keep alive to server");
            } else {
                // Else let us reset our application's state.
                Message missingKeepAlive = new Message(ClientNetwork.uuid, CoreCsts.ServerState.SERVER_DOWN, null);
                ClientDispatcherThread.getcoreMainQueue().add(missingKeepAlive);
                Log.d("## KeepAlive ##", "Server didn't answer, resetting application state.");
            }

            // Wait INTERVAL time before checking for a need to resend a keep-alive.
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Thread getThread() {
        return keepAliveThread;
    }
}