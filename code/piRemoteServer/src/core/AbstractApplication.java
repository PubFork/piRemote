package core;

import MessageObject.Message;
import MessageObject.PayloadObject.Close;
import MessageObject.PayloadObject.DoubleMessage;
import MessageObject.PayloadObject.IntMessage;
import MessageObject.PayloadObject.StringMessage;
import SharedConstants.ApplicationCsts;

import java.io.File;
import java.util.UUID;

/**
 * Created by sandro on 11.11.15.
 * Parent class for all applications' server part
 */
public abstract class AbstractApplication {

    protected static ApplicationCsts.ApplicationState applicationState;

    public ApplicationCsts.ApplicationState getApplicationState(){
        return applicationState;
    }

    public void processMessage(Message msg){
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
        if(applicationState == null || msg.getApplicationState() == null) return false;
        return msg.getApplicationState().equals(applicationState);
    }

    protected void changeApplicationState(ApplicationCsts.ApplicationState newState){
        applicationState = newState; // Change the applicationState
        ServerCore.sendMessage(ServerCore.makeMessage()); // Inform clients about it
        onApplicationStateChange(applicationState); // Get back to the application
    }

    protected void pickFile(String path, UUID destination){
        File f = new File(path);
        if(f.exists() && f.isDirectory()) ServerCore.sendMessage(ServerCore.makeOffer(destination,f));
        else System.out.println("WARNING: <"+path+"> is not a valid directory on this machine!");
    }

    protected void closeFilePicker(UUID destination){
        ServerCore.sendMessage(ServerCore.makeMessage(destination, new Close()));
    }

    public abstract void onApplicationStart();                                                // Called right after creation of the application
    public abstract void onApplicationStop();                                                 // Called right before the destruction of the application
    public abstract void onApplicationStateChange(ApplicationCsts.ApplicationState newState); // Called right BEFORE application switches to another state
    public abstract void onFilePicked(File file, UUID senderUUID);                            // Called when the FilePicker on the client sent a file pick message
    public abstract void onReceiveInt(int i, UUID senderUUID);                                // Called when an int    message arrived
    public abstract void onReceiveDouble(double d, UUID senderUUID);                          // Called when a  double message arrived
    public abstract void onReceiveString(String str, UUID senderUUID);                        // Called when a  string message arrived
}
