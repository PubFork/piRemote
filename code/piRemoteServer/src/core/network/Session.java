package core.network;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

class Session {

    @NotNull
    private final UUID uuid;
    @NotNull
    private final NetworkInfo networkInfo;

    /**
     * Default constructor for creating a session object managed by the server.
     * @param uuid UUID of the client to manage.
     * @param networkInfo NetworkInfo of the client to manage.
     */
    public Session(@NotNull UUID uuid, @NotNull NetworkInfo networkInfo) {
        this.uuid = uuid;
        this.networkInfo = networkInfo;
    }

    /**
     * Returns session UUID.
     * @return UUID if set, else null.
     */
    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns NetworkInfo object (ip, port, lastSeen) of the session.
     * @return Instantiated object NetworkInfo.
     */
    @NotNull
    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }
}