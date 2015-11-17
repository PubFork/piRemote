import Dispatcher.ClientDispatcherThread;
import KeepAlive.ClientKeepAliveThread;
import Sender.ClientSenderThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Fabian on 13.11.15.
 */
public class ClientNetwork {

    ClientDispatcherThread dispatcherThread;
    ClientKeepAliveThread keepAliveThread;
    ClientSenderThread clientSenderThread;

    public static Socket socket;


    /**
     * create a ClientNetwork object that has several threads
     */
    public ClientNetwork(InetAddress address, int port, LinkedBlockingQueue mainQueue) {
        /**
         * create the socket -> TODO: where get the ip and port of the server?
         */
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientSenderThread = new ClientSenderThread(socket);
        keepAliveThread = new ClientKeepAliveThread(clientSenderThread);
        dispatcherThread = new ClientDispatcherThread(socket, keepAliveThread, mainQueue);
    }

}
