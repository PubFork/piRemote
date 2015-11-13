package Sender;

import java.lang.Thread;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import MessageObject.Message;

/**
 * created by fabian on 13.11.15
 */

public class SenderThread extends Thread {
    // maybe implements Runnable instead of extending Thread?

    public final static BlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<Message>();


}