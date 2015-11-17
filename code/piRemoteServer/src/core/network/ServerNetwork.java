package core.network;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Fabian on 13.11.15.
 */
public class ServerNetwork {

    ServerDispatcherThread dispatcherThread;
    ServerKeepAliveThread keepAliveThread;
    static HashMap<UUID, NetworkInfo> sessionTable = new HashMap<>();

    ServerSocket socket;
    // TODO: bring it all together

}
