package core;

import MessageObject.Message;
import MessageObject.PayloadObject.Close;
import MessageObject.PayloadObject.DoubleMessage;
import MessageObject.PayloadObject.IntMessage;
import MessageObject.PayloadObject.StringMessage;
import SharedConstants.ApplicationCsts;
import com.sun.istack.internal.NotNull;

import java.io.File;
import java.util.UUID;

/**
 * Created by sandro on 11.11.15.
 * Parent class for all applications' server part
 */
public abstract class AbstractApplication {

    protected static ApplicationCsts.ApplicationState applicationState;

    public ApplicationCsts.ApplicationState getApplicationState(){
        // This returns the current ApplicationState. To be invoked by ServerCore only.
        // To retrieve the state from outside, use ServerCore.getState()
        return applicationState;
    }

    public void processMessage(@NotNull Message msg){
        // This takes a message, looks at it and reacts to it (can reply to the sender, call application functions etc.).

        if(!checkApplicationState(msg)){
            // Application State mismatch! Send ss to whoever sent this to us
            ServerCore.sendMessage(ServerCore.makeMessage(msg.getUuid()));
            return;
        }

        // Application State is consistent. Look at the payload
        if(msg.hasPayload()){
            if(msg.getPayload() instanceof IntMessage){
                onReceiveInt(((IntMessage) msg.getPayload()).i, msg.getUuid());
            }else if(msg.getPayload() instanceof DoubleMessage){
                onReceiveDouble(((DoubleMessage) msg.getPayload()).d, msg.getUuid());
            }else if(msg.getPayload() instanceof StringMessage){
                onReceiveString(((StringMessage) msg.getPayload()).str, msg.getUuid());
            }else{
                System.out.println("WARNING: Unknown Payload type: "+msg.getPayload().getClass().toString());
            }
        }// otherwise it was just an ss.
    }

    protected boolean checkApplicationState(Message msg) {
        // This returns whether or not the ApplicationState in the message corresponds to the actual ApplicationState.
        if(applicationState == null || msg.getApplicationState() == null) return false;
        return msg.getApplicationState().equals(applicationState);
    }

    protected void changeApplicationState(ApplicationCsts.ApplicationState newState){
        // This shall be called by the server application to request an ApplicationState change)
        applicationState = newState; // Change the applicationState
        ServerCore.sendMessage(ServerCore.makeMessage()); // Inform clients about it
        onApplicationStateChange(applicationState); // Get back to the application
    }

    protected void pickFile(@NotNull String path, @NotNull UUID destination){
        // This shall be called by the server application to initiate a FilePick scenario.
        // path: Root path (must be directory!) to start file pick with
        // destination: UUID of the client to send the offer to
        File f = new File(path);
        if(f.exists() && f.isDirectory()){
            ServerCore.setFilePickerBasePath(path);
            ServerCore.sendMessage(ServerCore.makeOffer(destination,f));
        }else System.out.println("WARNING: <"+path+"> is not a valid directory on this machine!");
    }

    protected void closeFilePicker(UUID destination){
        // This shall be called by the server application to make the file pick overlay disappear in the client's UI
        ServerCore.sendMessage(ServerCore.makeMessage(destination, new Close()));
    }

    public void sendInt(int i, UUID destinationUUID){
        // Use to send a message containing an int to one specific client
        IntMessage payload = new IntMessage();
        payload.i = i;
        ServerCore.sendMessage(ServerCore.makeMessage(destinationUUID, payload));
    }

    public void sendInt(int i){
        // Use to send a broadcast message containing an int
        IntMessage payload = new IntMessage();
        payload.i = i;
        ServerCore.sendMessage(ServerCore.makeMessage(payload));
    }

    public void sendDouble(double d, UUID destinationUUID){
        // Use to send a message containing a double to one specific client
        DoubleMessage payload = new DoubleMessage();
        payload.d = d;
        ServerCore.sendMessage(ServerCore.makeMessage(destinationUUID, payload));
    }

    public void sendDouble(double d){
        // Use to send a broadcast message containing a double
        DoubleMessage payload = new DoubleMessage();
        payload.d = d;
        ServerCore.sendMessage(ServerCore.makeMessage(payload));
    }

    public void sendString(String str, UUID destinationUUID){
        // Use to send a message containing a String to one specific client
        StringMessage payload = new StringMessage();
        payload.str = str;
        ServerCore.sendMessage(ServerCore.makeMessage(destinationUUID, payload));
    }

    public void sendString(String str){
        // Use to send a broadcast message containing a double
        StringMessage payload = new StringMessage();
        payload.str = str;
        ServerCore.sendMessage(ServerCore.makeMessage(payload));
    }

    public abstract void onApplicationStart();                                                // Called right after creation of the application
    public abstract void onApplicationStop();                                                 // Called right before the destruction of the application
    public abstract void onApplicationStateChange(ApplicationCsts.ApplicationState newState); // Called right BEFORE application switches to another state
    public abstract void onFilePicked(File file, UUID senderUUID);                            // Called when the FilePicker on the client sent a file pick message
    public abstract void onReceiveInt(int i, UUID senderUUID);                                // Called when an int    message arrived
    public abstract void onReceiveDouble(double d, UUID senderUUID);                          // Called when a  double message arrived
    public abstract void onReceiveString(String str, UUID senderUUID);                        // Called when a  string message arrived
}
