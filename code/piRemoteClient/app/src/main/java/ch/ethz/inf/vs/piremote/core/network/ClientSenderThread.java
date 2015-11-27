package ch.ethz.inf.vs.piremote.core.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ConnectionManagement.Connection;

/**
 * created by fabian on 13.11.15
 */

public class ClientSenderThread implements Runnable {
    //TODO(Mickey) Add proper Android logging

    private final static BlockingQueue<Object> sendingQueue = new LinkedBlockingQueue<>();
    private final Thread senderThread;
    private final DatagramSocket socket; // DatagramSocket used for communication.

    private final InetAddress inetAddress; // Address of server.
    private final int port; // Port on which is being listened/sent by the client.

    /**
     * Default constructor for the SenderThread.
     * @param socket Socket used for sending.
     * @param inetAddress Address to communicate with.
     */
    public ClientSenderThread(DatagramSocket socket, InetAddress inetAddress) {
        this.socket = socket;
        this.inetAddress = inetAddress;
        this.port = socket.getPort();

        // create Thread
        senderThread = new Thread(this);
        // start it when connecting
        // senderThread.start();
    }


    /**
     * Take a message from the queue and send it using marshalling.
     */
    @Override
    public void run() {
        // Allocate variables for thread to have less overhead creating them anew.
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8000);
        ObjectOutputStream outputStream = null;
        Object messageToSend = null;
        DatagramPacket packet;

        try {
            outputStream = new ObjectOutputStream(byteStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // End of allocating memory and objects.

        while (ClientNetwork.running.get()) {
            // Try sending a message while ClientNetwork is running.
            //TODO(Mickey) Proper documentation on the program logic
            try{
                while (!sendingQueue.isEmpty()) {
                    // Block on an empty queue.
                    messageToSend = sendingQueue.take();
                }

                // Serialise the message to send
                outputStream.writeObject(messageToSend);
                outputStream.flush();

                // Create a buffer and the corresponding packet to be sent to inetAddress/port.
                byte[] sendBuffer = byteStream.toByteArray();
                packet = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddress, port);

                // Send the packet.
                socket.send(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close streams, thread has been closed.
        try {
            byteStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * use this getter-function to get the sendingQueue
     * @return
     */
    public BlockingQueue<Object> getSendingQueue() {
        return sendingQueue;
    }

    /**
     * Method returns if the sendingQueue is empty (packet should be sent) or the sendingQueue does
     * not have an object connection inside it.
     * @param connection Object to check for.
     */
    public void connectionChangeToServerisSent(Connection connection) {
        while(sendingQueue.contains(connection) || !sendingQueue.isEmpty()) {
        }
        return;
    }

    public Thread getThread() {
        return senderThread;
    }
}