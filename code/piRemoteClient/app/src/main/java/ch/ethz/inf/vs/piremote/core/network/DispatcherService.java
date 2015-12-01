package ch.ethz.inf.vs.piremote.core.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
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

    private final String INFO_TAG = "# Dispatcher #";
    private final String DEBUG_TAG = "# Dispatcher DEBUG #";
    private final String ERROR_TAG = "# Dispatcher ERROR #";
    private final String WTF_TAG = "# Dispatcher WTF #";
    private final String WARN_TAG = "# Dispatcher WARN #";
    private final String VERBOSE_TAG = "# Dispatcher VERBOSE #";

    /**
     * Default constructor for the DispatcherService.
     *
     * @param network The Network starting this service.
     */
    DispatcherService(@NonNull ClientNetwork network) {
        clientNetwork = network;
        socket = clientNetwork.getSocket();
        network.setDispatcherConstructed();
        Log.i(INFO_TAG, "Service constructed.");
    }


    /**
     * Receive a message from the server and manage the connection and input accordingly.
     */
    @Override
    public void run() {
        // Allocation of variable to minimise overhead of recreating them every iteration.
        byte[] receiveBuffer = new byte[NetworkConstants.PACKETSIZE];
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        while (clientNetwork.isRunning()) {
            // Receive packets while ClientNetwork is running.
            try {
                // Receive an input and update the lastSeen value
                Log.v(VERBOSE_TAG, "Waiting for packet from Server.");
                socket.receive(packet);
                lastSeen.set(System.currentTimeMillis());
                Log.v(VERBOSE_TAG, "Received a message at " + lastSeen.get());

                // Marshalling of the DatagramPacket back to an object
                ByteArrayInputStream byteStream = new ByteArrayInputStream(receiveBuffer);
                //receiveBuffer = packet.getData();
                ObjectInputStream objectStream = new ObjectInputStream(byteStream);
                Object input = objectStream.readObject();

                // Handle the object received.
                if (input instanceof Message) {
                    // Message object received
                    UUID clientUUID = clientNetwork.getUuid();
                    if ((clientUUID == null) || !clientUUID.equals(((Message) input).getUuid())) {
                        // The client doesn't have a UUID yet or it has an invalid UUID.
                        clientNetwork.setUuid(((Message) input).getUuid());
                        Log.v(VERBOSE_TAG, "New UUID received.");
                    }
                    Log.i(INFO_TAG, "Received a message, routing it through.");
                    Log.v(VERBOSE_TAG, ((Message) input).toString());
                    clientNetwork.putOnMainQueue((Message) input);
                } else {
                    // Unknown object received, this shouldn't happen
                    Log.wtf(WTF_TAG, "Unknown input received from network.");
                }
            } catch (ClassNotFoundException e) {
                Log.e(ERROR_TAG, "Unknown class has been passed through.");
            } catch (OptionalDataException e) {
                Log.e(ERROR_TAG, "OptionalDataException.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                Log.e(ERROR_TAG, "Received an interrupt while receiving messages.");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(ERROR_TAG, "IO error occurred while receiving messages.");
                e.printStackTrace();
            }
        }
        Log.i(INFO_TAG, "Service ended.");
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