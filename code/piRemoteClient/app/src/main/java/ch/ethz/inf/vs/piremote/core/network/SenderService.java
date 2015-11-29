package ch.ethz.inf.vs.piremote.core.network;

import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import NetworkConstants.NetworkConstants;

public class SenderService implements Runnable {
    //TODO(Mickey) Add proper Android logging

    @NonNull
    private final ClientNetwork clientNetwork;
    @NonNull
    private final InetAddress inetAddress; // Address of server.
    private final int port; // Port on which is being listened/sent by the client.

    @NonNull
    private final BlockingQueue<Object> sendingQueue;
    @NonNull
    private final DatagramSocket socket; // DatagramSocket used for communication.

    @NonNull
    private final Thread senderThread;

    /**
     * Default constructor for the SenderService. The service has to be started explicitly.
     * @param clientNetwork The Network starting this service.
     */
    public SenderService(@NonNull ClientNetwork clientNetwork) {
        this.clientNetwork = clientNetwork;
        this.inetAddress = clientNetwork.getAddress();
        this.socket = clientNetwork.getSocket();
        this.port = clientNetwork.getPort();

        this.sendingQueue = new LinkedBlockingQueue<>();
        this.senderThread = new Thread(this);
        // senderThread.start();
    }


    /**
     * Take a message from the queue and send it using marshalling.
     */
    @Override
    public void run() {
        // Allocate variables for thread to have less overhead creating them anew.
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(NetworkConstants.PACKETSIZE);
        ObjectOutputStream objectStream = null;
        Object messageToSend;
        DatagramPacket packet;

        try {
            objectStream = new ObjectOutputStream(byteStream);
            objectStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // End of allocating memory and objects.

        while(clientNetwork.isRunning() || !sendingQueue.isEmpty()) {
            // Try sending a message while ClientNetwork is running.
            //TODO(Mickey) Proper documentation on the program logic
            try{
                // Take an Object from the Queue.
                messageToSend = sendingQueue.take();

                // Serialise the message to send
                if (objectStream == null) {
                    throw new IllegalArgumentException("objectStream to write to was null.");
                }
                objectStream.writeObject(messageToSend);
                objectStream.flush();

                // Create a buffer and the corresponding packet to be sent to address/port.
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
            if (objectStream == null) {
                throw new IllegalArgumentException("objectStream to close was null.");
            }
            objectStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns direct reference to the sendingQueue.
     * @return Direct reference to sendingQueue.
     */
    @NonNull
    public BlockingQueue<Object> getQueue() {
        return sendingQueue;
    }

    /**
     * Returns direct reference to the senderThread.
     * @return Direct reference to senderThread.
     */
    @NonNull
    public Thread getThread() {
        return senderThread;
    }
}