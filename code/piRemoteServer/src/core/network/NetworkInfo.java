package core.network;

/**
 * Created by Fabian on 13.11.15.
 */
public class NetworkInfo {
    int ip;
    int port;
    long lastSeen;


    public NetworkInfo(int ip, int port, long lastSeen){
        this.ip = ip;
        this.port = port;
        this.lastSeen = lastSeen;
    }
}
