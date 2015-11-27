package ch.ethz.inf.vs.piremote.core.network;

import ConnectionManagement.Connection;
import MessageObject.Message;
import SharedConstants.CoreCsts;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * created by fabian on 13.11.15
 */
public class ClientNetwork implements Runnable{

    private ClientDispatcherThread dispatcherThread;
    private ClientKeepAliveThread keepAliveThread;
    private ClientSenderThread clientSenderThread;

    public static UUID uuid;
    public static AtomicBoolean running;
    public static DatagramSocket socket;

    private Thread networkThread;
    private final InetAddress address;
    private final int port;
    private final LinkedBlockingQueue mainQueue;

    /**
     * create a ClientNetwork object that has several threads. This constructor is called
     * from the ClientCore to build its network. This constructor also starts the threads!
     * @param address core needs to provide the address of the server
     * @param port core also needs to provide the port of the server
     * @param mainQueue mainqueue on which the dispatcher will put the messages for the core
     */
    public ClientNetwork(InetAddress address, int port, LinkedBlockingQueue mainQueue) {
        this.address = address;
        this.port = port;
        this.mainQueue = mainQueue;

        networkThread = new Thread(this);
        networkThread.start();
    }


    /**
     * Returns SenderThread
     * @return Instantiated object of ClientSenderThread.
     */
    public ClientSenderThread getClientSenderThread() {
        return clientSenderThread;
    }

    public BlockingQueue<Object> getSendingQueue() {
        return getClientSenderThread().getSendingQueue();
    }

    //TODO(Mickey) Fix the connection/disconnection, give them the same behaviour, best would be a
    //             guarantee to have them only return once they successfully executed.

    /**
     * Connect to server. This method might block.
     */
    public void connectToServer() {
        Connection request = new Connection();
        request.requestConnection();

        // put connection request on sendingQueue
        getSendingQueue().add(request);
        clientSenderThread.connectionChangeToServerisSent(request);
    }

    /**
     * Disconnect from server. This method might block.
     */
    public void disconnectFromServer() {
        Connection disconnectRequest = new Connection();
        disconnectRequest.disconnect(uuid);

        // Send disconnection request to the server and wait until is has been sent there.
        getSendingQueue().add(disconnectRequest);
        clientSenderThread.connectionChangeToServerisSent(disconnectRequest);

        // Notify ClientCore that the connection to the server has been terminated. Set the status
        // of the client appropriately.
        Message disconnectServer = new Message(ClientNetwork.uuid, CoreCsts.ServerState.SERVER_DOWN, null);
        ClientDispatcherThread.getCoreMainQueue().add(disconnectServer);
        running.set(false);
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        running.set(true);
        uuid = null;

        // initialize the threads
        clientSenderThread = new ClientSenderThread(socket, address);
        keepAliveThread = new ClientKeepAliveThread(clientSenderThread);
        dispatcherThread = new ClientDispatcherThread(socket, address, mainQueue);

        // start threads
        clientSenderThread.getThread().start();
        dispatcherThread.getThread().start();
        keepAliveThread.getThread().start();
    }
}
