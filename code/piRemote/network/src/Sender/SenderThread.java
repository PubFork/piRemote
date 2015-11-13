package Sender;

import java.lang.Thread;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Dispatcher.ClientDispatcherThread;
import MessageObject.Message;

/**
 * created by fabian on 13.11.15
 */

public class SenderThread implements Runnable {

    public final static BlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<Message>();
    private Thread senderThread;

    public SenderThread() {

    }


    @Override
    public void run() {

    }
}