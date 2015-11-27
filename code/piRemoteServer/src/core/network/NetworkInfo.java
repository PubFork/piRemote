package core.network;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Fabian on 13.11.15.
 */
public class NetworkInfo {

    private final InetAddress ip;
    private final int port;
    private final AtomicLong lastSeen;

    /**
     * Default constructor for saving a client's connection NetworkInfo.
     * @param ip IP of active client.
     * @param port Port with which the client communicates.
     * @param lastSeen AtomicLong of last communication by the client to the server.
     */
    public NetworkInfo(InetAddress ip, int port, AtomicLong lastSeen){
        this.ip = ip;
        this.port = port;
        this.lastSeen = new AtomicLong(lastSeen.get());
    }
}