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
    private InetAddress address;
    private int port;
    private LinkedBlockingQueue mainQueue;

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
     * use this getter-function to get the senderthread
     * @return
     */
    public ClientSenderThread getClientSenderThread() {
        return clientSenderThread;
    }

    public BlockingQueue<Object> getSendingQueue() {
        return getClientSenderThread().getSendingQueue();
    }


    /**
     * call this function to connect to the server
     */
    public void connect() {
        Connection request = new Connection();
        request.requestConnection();

        // put connection request on sendingQueue
        getSendingQueue().add(request);
    }

    /**
     * call this function to disconnect from the server
     */
    public void disconnect() {

        Connection disconnectRequest = new Connection();
        disconnectRequest.disconnect(uuid);

        // put disconnect on sendingQueue
        getSendingQueue().add(disconnectRequest);

        // notify ClientCore that server is down
        ClientDispatcherThread.getcoreMainQueue().add(new Message(ClientNetwork.uuid, CoreCsts.ServerState.SERVER_DOWN, null));
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
        dispatcherThread = new ClientDispatcherThread(socket, keepAliveThread, mainQueue);

        // start threads
        clientSenderThread.getThread().start();
        dispatcherThread.getThread().start();
        keepAliveThread.getThread().start();
    }
}
