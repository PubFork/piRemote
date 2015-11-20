package ch.ethz.inf.vs.piremote.core.network;

import MessageObject.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * created by fabian on 13.11.15
 */

public class ClientSenderThread implements Runnable {

    private final static BlockingQueue<Object> sendingQueue = new LinkedBlockingQueue<Object>();
    private Thread senderThread;
    private Socket socket;
    private ObjectOutputStream outputStream;

    public ClientSenderThread(Socket socket) {
        this.socket = socket;

        try {
            // get the output stream of the socket
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create Thread
        senderThread = new Thread(this);
        // start it when connecting
        // senderThread.start();
    }


    @Override
    public void run() {

        while (ClientNetwork.running || !sendingQueue.isEmpty()) {
            try {
                /**
                 * take one message from the queue, put it on the outputstream
                 * and then flush (send via the socket)
                 */
                Object messageToSend = sendingQueue.take(); // or poll?
                outputStream.writeObject(messageToSend);
                outputStream.flush(); // send messages in stream

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    /**
     * use this getter-function to get the sendingQueue
     * @return
     */
    public BlockingQueue<Object> getSendingQueue() {
        return sendingQueue;
    }

    public Thread getThread() {
        return senderThread;
    }
}