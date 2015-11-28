package core.network;

import MessageObject.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Fabian on 13.11.15.
 */

public class SenderService implements Runnable {

    private final ServerNetwork serverNetwork;

    private final BlockingQueue<Message> sendingQueue;
    private DatagramSocket socket;

    private final Thread senderThread;

    /**
     * Default constructor for the SenderService. The service has to be started explicitly.
     * @param serverNetwork The Network starting this service.
     */
    public SenderService(ServerNetwork serverNetwork){
        this.serverNetwork = serverNetwork;

        this.sendingQueue = new LinkedBlockingQueue<>();
        this.senderThread = new Thread(this);
        // senderThread.start();
    }


    @Override
    public void run() {
        // Open a new socket for outgoing communication with the clients.
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Allocate variables for thread to have less overhead creating them anew.
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8000);
        ObjectOutputStream objectStream = null;
        Message messageToSend;
        NetworkInfo networkInfo;
        DatagramPacket packet;

        try {
            objectStream = new ObjectOutputStream(byteStream);
            objectStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // End of allocating memory and objects.

        while (serverNetwork.isRunning() || !sendingQueue.isEmpty()) {
            // Try sending a message while SendingService is running.
            try {
                //TODO(Mickey) Proper documentation on the program logic
                // Take an Object from the Queue.
                messageToSend = sendingQueue.take();

                // Get the NetworkInfo about the client supposed to the receive the Message.
                networkInfo = serverNetwork.getSessionTable().get(messageToSend.getUuid());

                // Serialise the message to send
                objectStream.writeObject(messageToSend);
                objectStream.flush();

                // Create a buffer and the corresponding packet to be sent to inetAddress/port.
                byte[] sendBuffer = byteStream.toByteArray();
                packet = new DatagramPacket(sendBuffer, sendBuffer.length, networkInfo.getIp(), networkInfo.getPort());

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
            objectStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns direct reference to the sendingQueue.
     * @return Direct reference to sendingQueue.
     */
    public BlockingQueue<Message> getQueue() {
        return sendingQueue;
    }

    /**
     * Returns direct reference to the senderThread.
     * @return Direct reference to senderThread.
     */
    public Thread getThread() {
        return senderThread;
    }

}
