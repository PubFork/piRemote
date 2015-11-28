package ch.ethz.inf.vs.piremote.core.network;

import ConnectionManagement.Connection;
import MessageObject.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * created by fabian on 13.11.15
 */

public class DispatcherService implements Runnable {
    //TODO(Mickey) Add proper Android logging

    private final ClientNetwork clientNetwork;

    private final DatagramSocket socket; // DatagramSocket used for communication.
    private BlockingQueue coreMainQueue; // Queue which the DispatcherService puts received messages in.
    private AtomicLong lastSeen; // Timestamp of last received message from the server.

    private final InetAddress inetAddress; // Address of server.
    private final int port; // Port on which is being listened/sent by the client.

    private Thread clientDispatcher;

    /**
     * Default constructor for the DispatcherService.
     * @param clientNetwork The Network starting this service.
     * @param queue Queue to put received messages on.
     */
    public DispatcherService(ClientNetwork clientNetwork, LinkedBlockingQueue queue){
        this.clientNetwork = clientNetwork;
        this.coreMainQueue = queue;

        this.socket = clientNetwork.getSocket();
        this.inetAddress = clientNetwork.getInetAddress();
        this.port = clientNetwork.getPort();

        this.lastSeen = new AtomicLong(0l);
        clientDispatcher = new Thread(this);
        // clientDispatcher.start();
    }


    /**
     * Receive a message from the server and manage the connection and input accordingly.
     */
    @Override
    public void run() {
        // Allocation of variable to minimise overhead of recreating them every iteration.
        byte[] receiveBuffer = new byte[8000];
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(receiveBuffer);
        ObjectInputStream objectStream = null;
        Object input;

        try {
            objectStream = new ObjectInputStream(byteStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Allocation end

        while(clientNetwork.isRunning()) {
            // Receive packets while ClientNetwork is running.
            try {
                // Receive an input and update the lastSeen value
                socket.receive(packet);
                lastSeen.set(System.currentTimeMillis());

                // Marshalling of the DatagramPacket back to an object
                //byteStream = new ByteArrayInputStream(receiveBuffer);
                //receiveBuffer = packet.getData();
                //objectStream = new ObjectInputStream(byteStream);
                input = objectStream.readObject();

                // Handle the object received.
                if (input instanceof Message) {
                    // Message object received
                    UUID clientUUID = clientNetwork.getUuid();
                    if (clientUUID == null || clientUUID != ((Message) input).getUuid()) {
                        // The client doesn't have a UUID yet or it has an invalid UUID.
                        clientNetwork.setUuid(((Message) input).getUuid());
                    }
                    coreMainQueue.put(input);
                } else if (input instanceof Connection) {
                    // Connection object received, this shouldn't happen
                } else {
                    // Something unknown has been received. This is really bad! Abort!
                    throw new RuntimeException("Unknown input received from network!");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (OptionalDataException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close streams, thread has been closed.
        try {
            objectStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            byteStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the last timestamp a message has been received.
     * @return lastSeen-value of successful incoming communication if any happened, else return 0;
     */
    public long getLastSeen() {
        return lastSeen.get();
    }

    public BlockingQueue getCoreMainQueue() {
        return coreMainQueue;
    }

    public Thread getThread() {
        return clientDispatcher;
    }
}