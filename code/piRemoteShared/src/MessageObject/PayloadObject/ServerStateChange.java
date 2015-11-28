package MessageObject.PayloadObject;

import SharedConstants.CoreCsts.ServerState;

/**
 * Created by sandro on 11.11.15.
 */
public class ServerStateChange extends Payload {
    public ServerState newServerState;

    public ServerStateChange(ServerState newServerState) {
        this.newServerState = newServerState;
    }
}
