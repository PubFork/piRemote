package ConnectionManagement;

import java.util.UUID;

/**
 * Created by mvaenskae on 20/11/15.
 * This class is used for managing connections within our project sent by
 * clients to the server. The server will then either answer with a Message
 * or not at all. The server is not using this class for direct communication
 * with the client.
 */
public class Connection {

    // Field that is set by client and handled by server.
    protected Connect connection;
    protected UUID uuid;

    /*
    This enumeration allows us to decide what connection methods are handled
    by the server and client.
     */
    public enum Connect {
        CONNECT,
        DISCONNECT
    }

    /**
     * Default constructor, creates a connection-object with null content.
     */
    public Connection() {
        connection = null;
    }

    /**
     * Sets connection request.
     */
    public void requestConnection() {
        connection = Connect.CONNECT;
    }

    /**
     * Sets disconnection request.
     */
    public void disconnect(UUID uuid) {
        connection = Connect.DISCONNECT;
        this.uuid = uuid;
    }

    /**
     * Returns the object's connection request.
     * @return connection request to be handled.
     */
    public Connect getConnection() {
        return connection;
    }

    /**
     * Returns the object's uuid
     * @return
     */
    public UUID getUuid() {
        return uuid;
    }
}