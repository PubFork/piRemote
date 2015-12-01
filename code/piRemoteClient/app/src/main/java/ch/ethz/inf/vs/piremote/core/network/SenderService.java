package ch.ethz.inf.vs.piremote.core.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ConnectionManagement.Connection;
import MessageObject.Message;
import NetworkConstants.NetworkConstants;

public class SenderService implements Runnable {
    //TODO(Mickey) Add proper Android logging

    @NonNull
    private final ClientNetwork clientNetwork;
    @NonNull
    private final InetAddress inetAddress; // Address of server.
    private final int port; // Port on which is being listened/sent by the client.

    @NonNull
    private final BlockingQueue<Object> sendingQueue = new LinkedBlockingQueue<>();
    @NonNull
    private final DatagramSocket socket; // DatagramSocket used for communication.

    @Nullable
    private Thread senderThread;

    /**
     * Default constructor for the SenderService. The service has to be started explicitly.
     *
     * @param network The Network starting this service.
     */
    SenderService(@NonNull ClientNetwork network) {
        clientNetwork = network;
        inetAddress = clientNetwork.getAddress();
        socket = clientNetwork.getSocket();
        port = clientNetwork.getPort();
        network.setSenderConstructed();
    }


    /**
     * Take a message from the queue and send it using marshalling.
     */
    @Override
    public void run() {
        while (clientNetwork.isRunning() || !sendingQueue.isEmpty()) {
            // Try sending a message while ClientNetwork is running.
            //TODO(Mickey) Proper documentation on the program logic
            try {
                //TODO(Mickey): This is NOT??? blocking... WTF?!
                // Take an Object from the Queue.
                Object messageToSend = sendingQueue.take();

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream(NetworkConstants.PACKETSIZE);
                ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

                // Serialise the message to send
                if (messageToSend instanceof Message) {
                    Message message = (Message) messageToSend;
                    objectStream.writeObject(message);
                } else if (messageToSend instanceof Connection) {
                    Connection connection = (Connection) messageToSend;
                    objectStream.writeObject(connection);
                }
                objectStream.flush();

                // Create a buffer and the corresponding packet to be sent to address/port.
                byte[] sendBuffer = byteStream.toByteArray();
                DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddress, port);
                // Send the packet.
                socket.send(packet);
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
    @NonNull
    public BlockingQueue<Object> getQueue() {
        return sendingQueue;
    }

    /**
     * Start the senderThread.
     */
    void startThread() {
        senderThread = new Thread(this);
        senderThread.start();
    }
}