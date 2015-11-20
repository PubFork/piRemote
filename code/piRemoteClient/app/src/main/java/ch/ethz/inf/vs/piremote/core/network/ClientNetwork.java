package ch.ethz.inf.vs.piremote.core.network;

import MessageObject.Message;
import SharedConstants.CoreCsts;
import ch.ethz.inf.vs.piremote.core.network.ClientDispatcherThread;
import ch.ethz.inf.vs.piremote.core.network.ClientKeepAliveThread;
import ch.ethz.inf.vs.piremote.core.network.ClientSenderThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * created by fabian on 13.11.15
 */
public class ClientNetwork {

    private ClientDispatcherThread dispatcherThread;
    private ClientKeepAliveThread keepAliveThread;
    private ClientSenderThread clientSenderThread;
    private UUID uuid;

    public static Socket socket;


    /**
     * create a ClientNetwork object that has several threads. This constructor is called
     * from the ClientCore to build its network
     * @param address core needs to provide the address of the server
     * @param port core also needs to provide the port of the server
     * @param mainQueue mainqueue on which the dispatcher will put the messages for the core
     */
    public ClientNetwork(InetAddress address, int port, LinkedBlockingQueue mainQueue) {

        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        uuid = null;
        clientSenderThread = new ClientSenderThread(socket);
        keepAliveThread = new ClientKeepAliveThread(clientSenderThread, mainQueue);
        dispatcherThread = new ClientDispatcherThread(socket, keepAliveThread, mainQueue);
    }


    /**
     * use this getter-function to get the senderthread
     * @return
     */
    public ClientSenderThread getClientSenderThread() {
        return clientSenderThread;
    }

    public BlockingQueue<Message> getSendingQueue() {
        return getClientSenderThread().getSendingQueue();
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * call this function to connect to the server
     */
    public void connect() {


    }

    /**
     * call this function to disconnect from the server
     */
    public void disconnect() {

    }
}
