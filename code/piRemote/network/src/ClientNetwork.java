import Dispatcher.ClientDispatcherThread;
import KeepAlive.ClientKeepAliveThread;
import Sender.ClientSenderThread;

import java.net.Socket;

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
    public ClientNetwork() {
        /**
         * create the socket -> TODO: where get the ip and port of the server?
         */
        // socket = new Socket(address, port);

        clientSenderThread = new ClientSenderThread(socket);
        keepAliveThread = new ClientKeepAliveThread(clientSenderThread);
        dispatcherThread = new ClientDispatcherThread(socket, keepAliveThread);
    }

}
