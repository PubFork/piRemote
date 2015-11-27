package core.network;

import java.util.UUID;

/**
 * Created by FR4NK-W on 11/21/2015.
 */
public class Session {

    private final UUID uuid;
    private final NetworkInfo networkInfo;

    /**
     * Default constructor for creating a session object managed by the server.
     * @param uuid UUID of the client to manage.
     * @param networkInfo NetworkInfo of the client to manage.
     */
    public Session (UUID uuid, NetworkInfo networkInfo) {
        this.uuid = uuid;
        this.networkInfo = networkInfo;
    }

    /**
     * Returns session UUID.
     * @return UUID if set, else null.
     */
    public UUID getUUID () {
        return uuid;
    }

    /**
     * Returns NetworkInfo object (ip, port, lastSeen) of the session.
     * @return Instantiated object NetworkInfo.
     */
    public NetworkInfo getNetworkInfo () {
        return networkInfo;
    }
}