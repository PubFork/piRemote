package core.network;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicLong;

class NetworkInfo {

    @NotNull
    private final InetAddress ip;
    private final int port;
    @NotNull
    private final AtomicLong lastSeen;

    /**
     * Default constructor for saving a client's connection NetworkInfo.
     *
     * @param address           IP of active client.
     * @param portNumber        Port with which the client communicates.
     * @param lastSeenTimeStamp AtomicLong of last communication by the client to the server.
     */
    NetworkInfo(@NotNull InetAddress address, int portNumber, long lastSeenTimeStamp) {
        ip = address;
        port = portNumber;
        lastSeen = new AtomicLong(lastSeenTimeStamp);
    }

    /**
     * Returns the IP of the active session.
     *
     * @return IP of session.
     */
    @NotNull
    public InetAddress getIp() {
        return ip;
    }

    /**
     * Returns the port of the active session.
     *
     * @return Port of session.
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns lastSeen info of the active session.
     *
     * @return LastSeen of client communication.
     */
    public long getLastSeen() {
        return lastSeen.get();
    }

    /**
     * Updates the active session's lastSeen value with newSeen's value.
     *
     * @param newSeen long with new lastSeen value to be set on old lastSeen.
     */
    public void updateLastSeen(long newSeen) {
        lastSeen.set(newSeen);
    }
}