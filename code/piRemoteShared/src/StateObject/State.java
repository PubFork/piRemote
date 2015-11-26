package StateObject;

import SharedConstants.ApplicationCsts;
import SharedConstants.CoreCsts;

import java.io.Serializable;

/**
 * Created by sandro on 10.11.15.
 * Contains ServerState and ApplicationState.
 */
public class State implements Serializable {

    protected CoreCsts.ServerState serverState;
    protected ApplicationCsts.ApplicationState applicationState;

    public State(CoreCsts.ServerState serverState, ApplicationCsts.ApplicationState applicationState){
        this.serverState = serverState;
        this.applicationState = applicationState;
    }

    public CoreCsts.ServerState getServerState(){
        return serverState;
    }

    public ApplicationCsts.ApplicationState getApplicationState(){
        return applicationState;
    }

    public boolean hasApplicationState(){
        return (serverState != CoreCsts.ServerState.NONE);
    }

    // TODO: If setters are required, place them here
}
