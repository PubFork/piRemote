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


    // TODO: bring it all together
}
