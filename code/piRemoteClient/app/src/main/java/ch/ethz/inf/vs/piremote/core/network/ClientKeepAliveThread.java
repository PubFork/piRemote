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

    public ClientKeepAliveThread(ClientSenderThread sender, BlockingQueue mainQueue) {
        sendingQueue = sender.getSendingQueue();
        this.mainQueue = mainQueue;

        keepAliveThread = new Thread(this);
        keepAliveThread.start();
    }

    @Override
    public void run() {

    }
}