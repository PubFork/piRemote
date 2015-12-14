package core.network;

import MessageObject.Message;
import NetworkConstants.NetworkConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class SenderService implements Runnable {

    @NotNull
    private final ServerNetwork serverNetwork;

    @NotNull
    private final BlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();

    @Nullable
    private DatagramSocket socket;

    @Nullable
    private Thread senderThread;

    /**
     * Default constructor for the SenderService. The service has to be started explicitly.
     *
     * @param network The Network starting this service.
     */
    SenderService(@NotNull ServerNetwork network) {
        serverNetwork = network;
        network.setSenderConstructed();
    }

    @Override
    public void run() {
        // Open a new socket for outgoing communication with the clients on any free port.
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (serverNetwork.isRunning() || !sendingQueue.isEmpty()) {
            // Try sending a message while SendingService is running.
            //TODO(Mickey) Proper documentation on the program logic
            try {
                // Take an Object from the Queue.
                Message messageToSend = sendingQueue.take();

                // Serialise the message to send
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream(NetworkConstants.PACKETSIZE);
                ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
                objectStream.writeObject(messageToSend);
                objectStream.flush();

                // Create a buffer and the corresponding packet to be sent to address/port.
                byte[] sendBuffer = byteStream.toByteArray();

                if (messageToSend.isBroadcast()) {
                    // Get a copy of the session table
                    HashMap<UUID, NetworkInfo> sessionTableSnapshot = (HashMap) serverNetwork.getSessionTable().clone();

                    // Iterate over the map and send the message to each client
                    for (NetworkInfo client : sessionTableSnapshot.values()) {
                        DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, client.getIp(), client.getPort());

                        // Send the packet.
                        socket.send(packet);
                    }
                } else {
                    // Get the NetworkInfo about the client supposed to the receive the Message.
                    NetworkInfo client = serverNetwork.getSessionTable().get(messageToSend.getUuid());

                    if (client != null) {
                        DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, client.getIp(), client.getPort());

                        // Send the packet.
                        socket.send(packet);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns direct reference to the sendingQueue.
     *
     * @return Direct reference to sendingQueue.
     */
    @NotNull BlockingQueue<Message> getQueue() {
        return sendingQueue;
    }

    /**
     * Put a Message on the SendingQueue to send.
     *
     * @param msg Message to send.
     */
    void putOnQueue(Message msg) {
        sendingQueue.add(msg);
    }

    /**
     * Start the senderThread.
     */
    void startThread() {
        senderThread = new Thread(this);
        senderThread.start();
    }
}
