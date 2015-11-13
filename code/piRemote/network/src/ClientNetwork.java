import Dispatcher.ClientDispatcherThread;
import KeepAlive.ClientKeepAliveThread;
import Sender.SenderThread;

import java.net.Socket;

/**
 * Created by Fabian on 13.11.15.
 */
public class ClientNetwork {

    ClientDispatcherThread dispatcherThread;
    ClientKeepAliveThread keepAliveThread;
    SenderThread senderThread;

    Socket socket;

    // TODO: bring it all together
}
