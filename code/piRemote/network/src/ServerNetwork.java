import Dispatcher.ServerDispatcherThread;
import KeepAlive.ServerKeepAliveThread;
import Sender.SenderThread;

import java.net.ServerSocket;

/**
 * Created by Fabian on 13.11.15.
 */
public class ServerNetwork {

    ServerDispatcherThread dispatcherThread;
    ServerKeepAliveThread keepAliveThread;
    SenderThread senderThread;

    ServerSocket socket;
    // TODO: bring it all together

}
