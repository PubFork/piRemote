package StateObject;

import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.CoreCsts.ServerState;

/**
 * Created by sandro on 10.11.15.
 * Contains ServerState and ApplicationState.
 */
public class State {

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
