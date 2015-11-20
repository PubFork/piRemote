package ch.ethz.inf.vs.piremote.core.network;

import MessageObject.Message;
import ch.ethz.inf.vs.piremote.core.network.ClientSenderThread;

import java.lang.Thread;
import java.util.concurrent.BlockingQueue;

/**
 * created by fabian on 13.11.15
 */

public class ClientKeepAliveThread implements Runnable {

    private BlockingQueue<Message> sendingQueue;
    private BlockingQueue<Message> mainQueue;
    private Thread keepAliveThread;
    private long INTERVAL = 1000;


    public ClientKeepAliveThread(ClientSenderThread sender, BlockingQueue mainQueue) {
        sendingQueue = sender.getSendingQueue();
        this.mainQueue = mainQueue;

        keepAliveThread = new Thread(this);
        keepAliveThread.start();
    }

    @Override
    public void run() {

        while (ClientNetwork.running) {


            if (System.currentTimeMillis() - ClientDispatcherThread.getLastSeen() < 3*INTERVAL) {
                try {
                    wait(INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message keepAlive = null; // new message with states from core
                sendingQueue.add(keepAlive);
            } else {
                // TODO: put some message in mainQueue
                keepAliveThread.stop();
            }

        }
    }
}