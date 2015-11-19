package MessageObject;

import MessageObject.PayloadObject.*;
import SharedConstants.ApplicationCsts;
import SharedConstants.CoreCsts;
import StateObject.State;

import java.util.UUID;

/**
 * Created by sandro on 10.11.15.
 * Provides a uniform way to communicate within the client or server.
 * Each Message object contains:
 *   the UUID of the sender / reveiver (can be 0 for pseudo-broadcast),
 *   the state,
 *   and an optional field of type Payload.
 */
public class Message {

    protected UUID uuid;
    protected CoreCsts.ServerState serverState;
    protected ApplicationCsts.ApplicationState applicationState;
    protected Payload payload;


    // Constructors for a specific recipient

    public Message(UUID uuid, CoreCsts.ServerState serverState, ApplicationCsts.ApplicationState applicationState){
        // Constructor for building a message to without payload to a recipient
        this.uuid = uuid;
        this.serverState = serverState;
        this.applicationState = applicationState;
        this.payload = null;
    }

    public Message(UUID uuid, State state){
        // Constructor for building a message to without payload to a recipient using a State object
        this.uuid = uuid;
        this.serverState = state.getServerState();
        this.applicationState = state.getApplicationState();
        this.payload = null;
    }

    public Message(UUID uuid, CoreCsts.ServerState serverState, ApplicationCsts.ApplicationState applicationState, Payload payload){
        // Constructor for building a message to with payload to a recipient
        this.uuid = uuid;
        this.serverState = serverState;
        this.applicationState = applicationState;
        this.payload = payload;
    }

    public Message(UUID uuid, State state, Payload payload){
        // Constructor for building a message to with payload to a recipient using a State object
        this.uuid = uuid;
        this.serverState = state.getServerState();
        this.applicationState = state.getApplicationState();
        this.payload = payload;
    }


    // Broadcast Message constructors

    public Message(CoreCsts.ServerState serverState, ApplicationCsts.ApplicationState applicationState){
        // Constructor for building a broadcast message to without payload
        this.uuid = null;
        this.serverState = serverState;
        this.applicationState = applicationState;
        this.payload = null;
    }

    public Message(State state){
        // Constructor for building a broadcast message to without payload sing a State object
        this.uuid = null;
        this.serverState = state.getServerState();
        this.applicationState = state.getApplicationState();
        this.payload = null;
    }

    public Message(CoreCsts.ServerState serverState, ApplicationCsts.ApplicationState applicationState, Payload payload){
        // Constructor for building a broadcast message to with payload
        this.uuid = null;
        this.serverState = serverState;
        this.applicationState = applicationState;
        this.payload = payload;
    }

    public Message(State state, Payload payload){
        // Constructor for building a broadcast message to with payload using a State object
        this.uuid = null;
        this.serverState = state.getServerState();
        this.applicationState = state.getApplicationState();
        this.payload = payload;
    }

    public String toString(){
        String str = "Message ";
        if(!isBroadcast()) str+="to "+uuid.toString()+".\n";
        else str+="to all known clients.\n";
        str+="-> ServerState: "+serverState+"\n";
        str+="-> ApplicationState: "+applicationState+"\n";
        if(hasPayload()){
            str+="-> Payload of type "+payload.getClass().toString()+":\n";
            if(payload instanceof ServerStateChange)
                str+="--> newServerState: "+((ServerStateChange) payload).newServerState;
            if(payload instanceof IntMessage)
                str+="--> i: "+((IntMessage) payload).i;
            if(payload instanceof DoubleMessage)
                str+="--> d: "+((DoubleMessage) payload).d;
            if(payload instanceof StringMessage)
                str+="--> str: "+((StringMessage) payload).str;
            if(payload instanceof Pick)
                str+="--> path: "+((Pick) payload).path;
            if(payload instanceof Offer)
                str+="--> paths: "+((Offer) payload).paths;
        }else{
            str+="-> (no payload)";
        }
        return str;
    }


    // Getters

    public UUID getUuid(){
        return uuid;
    }

    public CoreCsts.ServerState getServerState(){
        return serverState;
    }

    public ApplicationCsts.ApplicationState getApplicationState(){
        return applicationState;
    }

    public State getState(){
        return new State(serverState, applicationState);
    }

    public Payload getPayload(){
        return payload;
    }


    // boolean convenience functions

    public boolean hasPayload(){
        return (payload != null);
    }

    public boolean hasApplicationState(){
        return (serverState != CoreCsts.ServerState.NONE);
    }

    public boolean isBroadcast(){
        return (uuid == null);
    }

    // TODO: If setters are required, place them here
}
