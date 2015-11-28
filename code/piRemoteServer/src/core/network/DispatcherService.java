package core.network;

import ConnectionManagement.Connection;
import MessageObject.Message;
import core.ServerCore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * created by fabian on 13.11.15
 */

public class DispatcherService implements Runnable {

    private final ServerNetwork serverNetwork;
    private final SenderService senderService;

    private BlockingQueue<Session> morgueQueue;
    private HashMap<UUID, NetworkInfo> sessionTable;
    private BlockingQueue<Message> sendingQueue;

    private DatagramSocket socket;
    private final int defaultPort;

    private AtomicLong lastSeen; // Timestamp of last received message.
    private final Thread dispatcherThread;

    /**
     * Default constructor for the DispatcherService.
     * @param defaultPort The default port for receiving incoming packets.
     * @param serverNetwork The Network starting this service.
     * @param senderService The Network's sending service.
     */
    public DispatcherService(int defaultPort, ServerNetwork serverNetwork, SenderService senderService) {
        this.defaultPort = defaultPort;
        this.serverNetwork = serverNetwork;
        this.senderService = senderService;

        this.sessionTable = serverNetwork.getSessionTable();
        this.sendingQueue = senderService.getQueue();

        this.lastSeen = new AtomicLong(0l);
        this.morgueQueue = new LinkedBlockingQueue<>();
        this.dispatcherThread = new Thread(this);
        // dispatcherThread.start();
    }

    /**
     * Receive messages from clients and manage the connections plus inputs accordingly.
     */
    @Override
    public void run() {
        // Initialise the socket to receive data.
        startSocket(defaultPort);

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

        while(serverNetwork.isRunning()) {
            // Receive packets while ServerNetwork is running.
            try {
                // Receive an input and update the lastSeen value
                socket.receive(packet);
                lastSeen.set(System.currentTimeMillis());

                // TODO(Mickey) Check proper handling order from here on.

                // Check for any clients that need to be removed
                while (!morgueQueue.isEmpty()) {
                    sessionTable.remove(morgueQueue.take());
                }

                // Marshalling of the DatagramPacket back to an object
                //byteStream = new ByteArrayInputStream(receiveBuffer);
                //receiveBuffer = packet.getData();
                //objectStream = new ObjectInputStream(byteStream);
                input = objectStream.readObject();

                if (input instanceof Message) {
                    Message receivedMessage = (Message) input;
                    UUID uuid = receivedMessage.getUuid();

                    // check the sessionTable
                    if (!sessionTable.containsKey(uuid)) {
                        // The client has likely timed out and doesn't have a valid UUID anymore, reassociate it.
                        addNewClient(packet.getAddress(), packet.getPort(), lastSeen);
                    } else {
                        // Update lastSeen and put the message on the Core's queue.
                        sessionTable.get(uuid).updateLastSeen(lastSeen.get());
                        ServerCore.mainQueue.put(receivedMessage);
                    }
                    // (TODO: first check if it is a FilePickerRequest) is handled by ServerCore
                } else if (input instanceof Connection) {
                    Connection connection = (Connection) input;

                    if (connection.getConnection() == Connection.Connect.CONNECT) {
                        // Explicit connection request from client received, add new session.
                        addNewClient(packet.getAddress(), packet.getPort(), lastSeen);
                    } else if (connection.getConnection() == Connection.Connect.DISCONNECT) {
                        // Explicit disconnect request from client received, remove the session.
                        UUID uuid = connection.getUuid();
                        sessionTable.remove(uuid);
                    }
                } else {
                    // Something unknown has been received. This is really bad! Abort!
                    throw new RuntimeException("Unknown input received from network!");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a socket bound to port 'number' if possible, else gets any open port.
     * @param number Port on which the socket should bind to.
     */
    private void startSocket(int number) {
        try {
            socket = new DatagramSocket(number);
        } catch (SocketException e) {
            try {
                socket = new DatagramSocket();
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Add a new session to the sessionTable and send it an UUID back with the current ServerState
     * @param inetAddress Client address requesting a new session.
     * @param port Client port requesting a new session.
     * @param lastSeen Timestamp of received message.
     */
    private void addNewClient(InetAddress inetAddress, int port, AtomicLong lastSeen) {
        // Generate a new uuid for the client and save the connection's details.
        UUID clientUUID = UUID.randomUUID();
        NetworkInfo clientInfo = new NetworkInfo(inetAddress, port, lastSeen.get());

        // Store the info in the table.
        sessionTable.put(clientUUID, clientInfo);

        // Create a reply and store it on the sender's queue.
        Message newClient = new Message(clientUUID, ServerCore.getState());
        senderService.getQueue().add(newClient);
    }

    /**
     * Returns direct reference to the morgueQueue.
     * @return Direct reference to morgueQueue.
     */
    public BlockingQueue<Session> getQueue() {
        return morgueQueue;
    }

    /**
     * Returns direct reference to the dispatcherThread.
     * @return Direct reference to dispatcherThread.
     */
    public Thread getThread() {
        return dispatcherThread;
    }

    /**
     * Returns the port the dispatcher is listenting to.
     * @return Returns the port the network is receiving from.
     */
    public int getPort() {
        return socket.getPort();
    }
}