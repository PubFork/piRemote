package core.network;

import ConnectionManagement.Connection;
import MessageObject.Message;
import NetworkConstants.NetworkConstants;
import core.ServerCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

class DispatcherService implements Runnable {

    @NotNull
    private final ServerNetwork serverNetwork;

    @NotNull
    private final BlockingQueue<Session> morgueQueue = new LinkedBlockingQueue<>();
    @NotNull
    private final BlockingQueue<Message> sendingQueue;
    @NotNull
    private final HashMap<UUID, NetworkInfo> sessionTable;

    @Nullable
    private DatagramSocket socket;
    private final int defaultPort;

    @NotNull
    private final AtomicLong lastSeen = new AtomicLong(0L);
    @Nullable
    private Thread dispatcherThread;

    /**
     * Default constructor for the DispatcherService.
     *
     * @param portNumber The default port for receiving incoming packets.
     * @param network    The Network starting this service.
     * @param sender     The Network's sending service.
     */
    DispatcherService(int portNumber, @NotNull ServerNetwork network, @NotNull SenderService sender) {
        defaultPort = portNumber;
        serverNetwork = network;
        sessionTable = network.getSessionTable();
        sendingQueue = sender.getQueue();
        network.setDispatcherConstructed();
    }

    /**
     * Receive messages from clients and manage the connections plus inputs accordingly.
     */
    @Override
    public void run() {
        // Initialise the socket to receive data.
        startSocket(defaultPort);

        byte[] receiveBuffer = new byte[NetworkConstants.PACKETSIZE];
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        while (serverNetwork.isRunning()) {
            // Receive packets while ServerNetwork is running.
            try {
                // Receive an input and update the lastSeen value
                socket.receive(packet);
                lastSeen.set(System.currentTimeMillis());

                // TODO(Mickey) Check proper handling order from here on.

                // Check for any clients that need to be removed
                while (!morgueQueue.isEmpty()) {
                    Session temp = morgueQueue.take();
                    sessionTable.remove(temp.getUUID(), temp.getNetworkInfo());
                    System.out.println("DispatcherService: Removed client due to timeout: "+temp.getUUID().toString());
                    printSessionTable();
                }

                // Marshalling of the DatagramPacket back to an object
                ByteArrayInputStream byteStream = new ByteArrayInputStream(receiveBuffer);
                //receiveBuffer = packet.getData();
                ObjectInputStream objectStream = new ObjectInputStream(byteStream);
                Object input = objectStream.readObject();

                if (input instanceof Message) {
                    Message receivedMessage = (Message) input;
                    UUID uuid = receivedMessage.getUuid();

                    // check the sessionTable
                    if (sessionTable.containsKey(uuid)) {
                        // Update lastSeen and put the message on the Core's queue.
                        sessionTable.get(uuid).updateLastSeen(lastSeen.get());
                        ServerCore.mainQueue.put(receivedMessage);
                    } else {
                        // The client has likely timed out and doesn't have a valid UUID anymore, reassociate it.
                        addNewClient(packet.getAddress(), packet.getPort(), lastSeen);
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
                        System.out.println("DispatcherService: Removed client due to disconnect: "+uuid.toString());
                        printSessionTable();
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
     *
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
     *
     * @param address   Client address requesting a new session.
     * @param port      Client port requesting a new session.
     * @param timestamp Timestamp of received message.
     */
    private void addNewClient(@NotNull InetAddress address, int port, @NotNull AtomicLong timestamp) {
        // Generate a new uuid for the client and save the connection's details.
        UUID clientUUID = UUID.randomUUID();
        NetworkInfo clientInfo = new NetworkInfo(address, port, timestamp.get());

        // Store the info in the table.
        sessionTable.put(clientUUID, clientInfo);

        // Create a reply and store it on the sender's queue.
        Message newClient = new Message(clientUUID, ServerCore.getState());
        sendingQueue.add(newClient);

        System.out.println("DispatcherService: Added new client: "+clientUUID.toString());
        printSessionTable();
    }

    /**
     * Returns direct reference to the morgueQueue.
     *
     * @return Direct reference to morgueQueue.
     */
    @NotNull
    public BlockingQueue<Session> getQueue() {
        return morgueQueue;
    }

    /**
     * Put a session on the MorgueQueue.
     *
     * @param ses Session scheduled for deletion.
     */
    void putOnQueue(Session ses) {
        morgueQueue.add(ses);
    }

    /**
     * Start the dispatcherThread.
     */
    void startThread() {
        dispatcherThread = new Thread(this);
        dispatcherThread.start();
    }

    /**
     * Returns the port the dispatcher is listening on.
     *
     * @return Returns the port the network is receiving from if exists, else returns '-2'.
     */
    public int getPort() {
        if (socket == null) {
            return -2;
        }
        return socket.getLocalPort();
    }

    /**
     * Prints session table.
     * Use for debug
     */
    public void printSessionTable(){
        System.out.println("Printing sessionTable:");
        for(UUID uuid : sessionTable.keySet()){
            System.out.println("   "+uuid.toString()+ " @ "+sessionTable.get(uuid).getIp().toString());
        }
    }
}