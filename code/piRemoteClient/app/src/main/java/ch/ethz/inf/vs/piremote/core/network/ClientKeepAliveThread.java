package ch.ethz.inf.vs.piremote.core.network;

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
    private BlockingQueue<Object> mainQueue;
    private Thread keepAliveThread;
    private long INTERVAL = 1000;


    public ClientKeepAliveThread(ClientSenderThread sender, BlockingQueue mainQueue) {
        sendingQueue = sender.getSendingQueue();
        this.mainQueue = mainQueue;

        keepAliveThread = new Thread(this);
        // start it when connecting
        // keepAliveThread.start();
    }

    @Override
    public void run() {

        while (ClientNetwork.running.get()) {

            if (System.currentTimeMillis() - ClientDispatcherThread.getLastSeen() < 3*INTERVAL) {
                Message keepAlive = new Message(ClientNetwork.uuid, ClientCore.getState());
                sendingQueue.add(keepAlive);
            } else {
                ClientDispatcherThread.getcoreMainQueue().add(new Message(ClientNetwork.uuid, CoreCsts.ServerState.SERVER_DOWN, null));
            }

            try {
                wait(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public Thread getThread() {
        return keepAliveThread;
    }
}