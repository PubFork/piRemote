package ch.ethz.inf.vs.piremote.core.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import MessageObject.Message;
import NetworkConstants.NetworkConstants;

public class DispatcherService implements Runnable {
    //TODO(Mickey) Add proper Android logging

    @NonNull
    private final ClientNetwork clientNetwork;

    @NonNull
    private final DatagramSocket socket; // DatagramSocket used for communication.
    @NonNull
    private final AtomicLong lastSeen = new AtomicLong(System.currentTimeMillis()); // Timestamp of last received message from the server.

    @Nullable
    private Thread dispatcherThread;

    /**
     * Default constructor for the DispatcherService.
     *
     * @param network The Network starting this service.
     */
    DispatcherService(@NonNull ClientNetwork network) {
        clientNetwork = network;
        socket = clientNetwork.getSocket();
        network.setDispatcherConstructed();
    }


    /**
     * Receive a message from the server and manage the connection and input accordingly.
     */
    @Override
    public void run() {
        // Allocation of variable to minimise overhead of recreating them every iteration.
        byte[] receiveBuffer = new byte[NetworkConstants.PACKETSIZE];
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(receiveBuffer);
        ObjectInputStream objectStream = null;
        // TODO(Mickey): Narrow down to Message?
        Object input;

        try {
            objectStream = new ObjectInputStream(byteStream);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Allocation end

        while (clientNetwork.isRunning()) {
            // Receive packets while ClientNetwork is running.
            try {
                // Receive an input and update the lastSeen value
                socket.receive(packet);
                lastSeen.set(System.currentTimeMillis());

                // Marshalling of the DatagramPacket back to an object
                //byteStream = new ByteArrayInputStream(receiveBuffer);
                //receiveBuffer = packet.getData();
                //objectStream = new ObjectInputStream(byteStream);
                if (objectStream == null) {
                    throw new IllegalArgumentException("objectStream to read from was null.");
                }
                input = objectStream.readObject();

                // Handle the object received.
                if (input instanceof Message) {
                    // Message object received
                    UUID clientUUID = clientNetwork.getUuid();
                    if ((clientUUID == null) || !clientUUID.equals(((Message) input).getUuid())) {
                        // The client doesn't have a UUID yet or it has an invalid UUID.
                        clientNetwork.setUuid(((Message) input).getUuid());
                    }
                    clientNetwork.putOnMainQueue((Message) input);
                } else {
                    // Unknown object received, this shouldn't happen
                    throw new RuntimeException("Unknown input received from network!");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (OptionalDataException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close streams, thread has been closed.
        try {
            if (objectStream == null) {
                throw new IllegalArgumentException("objectStream to close was null.");
            }
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
     *
     * @return lastSeen-value of successful incoming communication if any happened, else return 0;
     */
    public long getLastSeen() {
        return lastSeen.get();
    }

    /**
     * Start the dispatcherThread.
     */
    void startThread() {
        dispatcherThread = new Thread(this);
        dispatcherThread.start();
    }
}