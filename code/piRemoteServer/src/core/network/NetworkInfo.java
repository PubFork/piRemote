package core.network;

import java.net.InetAddress;

/**
 * Created by Fabian on 13.11.15.
 */
public class NetworkInfo {
    InetAddress ip;
    int port;
    long lastSeen;


    public NetworkInfo(InetAddress ip, int port, long lastSeen){
        this.ip = ip;
        this.port = port;
        this.lastSeen = lastSeen;
    }
}
