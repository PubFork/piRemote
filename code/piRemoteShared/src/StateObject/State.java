package StateObject;

import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.CoreCsts.ServerState;

import java.io.Serializable;

/**
 * Created by sandro on 10.11.15.
 * Contains ServerState and ApplicationState.
 */
public class State implements Serializable {

    protected ServerState serverState;
    protected ApplicationState applicationState;

    public State(ServerState serverState, ApplicationState applicationState){
        this.serverState = serverState;
        this.applicationState = applicationState;
    }

    public ServerState getServerState(){
        return serverState;
    }

    public ApplicationState getApplicationState(){
        return applicationState;
    }

    public boolean hasApplicationState(){
        return (serverState != ServerState.NONE);
    }

    // TODO: If setters are required, place them here
}
