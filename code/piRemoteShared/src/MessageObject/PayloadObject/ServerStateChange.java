package MessageObject.PayloadObject;

import SharedConstants.CoreCsts.ServerState;

import java.io.Serializable;

/**
 * Created by sandro on 11.11.15.
 * Payload for Message from Client to Server.
 */
public class ServerStateChange extends Payload implements Serializable {
    public ServerState newServerState;

    public ServerStateChange() { }

    public ServerStateChange(ServerState newServerState) {
        this.newServerState = newServerState;
    }
}
