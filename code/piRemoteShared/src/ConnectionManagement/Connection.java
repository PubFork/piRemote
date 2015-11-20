package ConnectionManagement;

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

    /*
    This enumeration allows us to decide what connection methods are handled
    by the server and client.
     */
    private enum Connect {
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
    public void disconnect() {
        connection = Connect.DISCONNECT;
    }

    /**
     * Returns the object's connection request.
     * @return connection request to be handled.
     */
    public Connect getConnection() {
        return connection;
    }
}