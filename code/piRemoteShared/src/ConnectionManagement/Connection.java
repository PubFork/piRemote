package ConnectionManagement;

/**
 * Created by mvaenskae on 20/11/15.
 * This class is used for managing connections within our project sent by
 * clients to the server. The server will then either answer with a Message
 * or not at all. The server is not using this class for direct communication
 * with the client.
 */
public class Connection {

    /*
    This enumeration allows us to decide what connection methods are handled
    by the server and client.
     */
    public enum Connect {
        CONNECT,
        DISCONNECT
    }
}