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
     *
     * @param clientUUID UUID of the client to manage.
     * @param clientInfo NetworkInfo of the client to manage.
     */
    Session(@NotNull UUID clientUUID, @NotNull NetworkInfo clientInfo) {
        uuid = clientUUID;
        networkInfo = clientInfo;
    }

    /**
     * Returns session UUID.
     *
     * @return UUID if set, else null.
     */
    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns NetworkInfo object (ip, port, lastSeen) of the session.
     *
     * @return Instantiated object NetworkInfo.
     */
    @NotNull
    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    /**
     * Method overriding object comparison on Session objects.
     * @param other Other object to compare to.
     * @return True if both objects are the same, else returns false.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Session) {
            if (((Session) other).getNetworkInfo() != networkInfo) {
                return false;
            }
            if (((Session) other).getUUID() != uuid) {
                return false;
            }
            return true;
        }
        return false;
    }
}