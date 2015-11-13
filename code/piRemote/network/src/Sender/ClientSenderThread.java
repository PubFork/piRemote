package Sender;

import java.lang.Thread;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Dispatcher.ClientDispatcherThread;
import MessageObject.Message;

/**
 * created by fabian on 13.11.15
 */

public class ClientSenderThread implements Runnable {

    public final static BlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<Message>();
    private Thread senderThread;
    private Socket socket;

    public ClientSenderThread(Socket socket) {
        this.socket = socket;

        senderThread = new Thread(this);
        senderThread.start();
    }


    @Override
    public void run() {

        try {
            Message messageToSend = sendingQueue.take();
            // send message via socket to server
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}