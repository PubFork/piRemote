package core.network;

import java.util.UUID;

/**
 * Created by FR4NK-W on 11/21/2015.
 */
public class Session {
    private UUID uuid;
    private  NetworkInfo networkInfo;
    public Session (UUID uuid, NetworkInfo networkInfo) {
        this.uuid = uuid;
        this.networkInfo = networkInfo;
    }

    public UUID getUUID () { return uuid;}
    public NetworkInfo getNetworkInfo () { return networkInfo;}
}