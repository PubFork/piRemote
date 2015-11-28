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
    public NetworkInfo(InetAddress ip, int port, long lastSeen){
        this.ip = ip;
        this.port = port;
        this.lastSeen = new AtomicLong(lastSeen);
    }

    /**
     * Returns the IP of the active session.
     * @return IP of session.
     */
    public InetAddress getIp() {
        return this.ip;
    }

    /**
     * Returns the port of the active session.
     * @return Port of session.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Returns lastSeen info of the active session.
     * @return LastSeen of client communication.
     */
    public long getLastSeen() {
        return this.lastSeen.get();
    }

    /**
     * Updates the active session's lastSeen value with newSeen's value.
     * @param newSeen long with new lastSeen value to be set on old lastSeen.
     */
    public void updateLastSeen(long newSeen) {
        this.lastSeen.set(newSeen);
    }
}