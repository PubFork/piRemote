package ch.ethz.inf.vs.piremote.core.network;

import MessageObject.Message;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * created by fabian on 13.11.15
 */

public class ClientDispatcherThread implements Runnable {

    private Thread clientDispatcher;
    private final DatagramSocket socket; // DatagramSocket used for communication.
    private static BlockingQueue coreMainQueue; // Queue which the DispatchetThread puts received messages in.
    private AtomicLong lastSeen = null; // Timestamp of last received message from the server.

    private final InetAddress inetAddress; // Address of server.
    private final int port; // Port on which is being listened/sent by the client.

    /**
     * Default constructor for the DispatcherThread.
     * @param socket Socket used for receiving.
     * @param inetAddress Address to communicate with.
     * @param queue Queue to put received messages on.
     */
    public ClientDispatcherThread(DatagramSocket socket, InetAddress inetAddress, LinkedBlockingQueue queue){
        this.socket = socket;
        this.port = socket.getPort();
        this.inetAddress = inetAddress;
        this.coreMainQueue = queue;

        // set up the thread
        clientDispatcher = new Thread(this);
        // start it when connecting
        // clientDispatcher.start();
    }


    @Override
    public void run() {
        
        while (ClientNetwork.running.get()) {
            try {

                /**
                 * read a message from the inputstream an forward it to the
                 * main Queue of the ClientCore
                 */
                Message readMessage = (Message) inputStream.readObject();
                lastSeen.set(System.currentTimeMillis());

                if (ClientNetwork.uuid == null) {
                    // set uuid when connected
                    ClientNetwork.uuid = readMessage.getUuid();
                }

                coreMainQueue.put(readMessage);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Returns the last timestamp a message has been received.
     * @return lastSeen-value of successful incoming communication if any happened, else return -1;
     */
    public long getLastSeen() {
        if (null != lastSeen) {
            return lastSeen.get();
        } else {
            return -1;
        }
    }

    public static BlockingQueue getcoreMainQueue() {
        return coreMainQueue;
    }

    public Thread getThread() {
        return clientDispatcher;
    }
}