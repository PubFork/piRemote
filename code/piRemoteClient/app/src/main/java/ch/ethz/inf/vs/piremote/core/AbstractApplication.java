package ch.ethz.inf.vs.piremote.core;

import java.io.File;

import MessageObject.Message;
import MessageObject.PayloadObject.*;
import SharedConstants.ApplicationCsts.ApplicationState;

/**
 * Created by andrina on 19/11/15.
 *
 * This abstract client application provides a way to access all applications on the client part in a uniform manner.
 */
public abstract class AbstractApplication {

    private static ApplicationState applicationState;

    protected static AbstractActivity activity; // reference to current Activity
    protected static ClientCore clientCore;

    /**
     * Inspect the received message and react to it. We can be sure that the application is still running on the server.
     * @param msg Message the ClientCore forwarded
     */
    public void processMessage(Message msg) {

        // First, we need to check the ApplicationState.
        if(!checkApplicationState(msg)){
            // Inconsistent state: Change the applicationState before looking at the payload.
            applicationState = msg.getApplicationState();
            onApplicationStateChange(applicationState); // Update UI.
        }

        // ApplicationState is consistent. Look at the payload for additional information.
        if (msg.hasPayload()) {
            Payload receivedPayload = msg.getPayload();

            if (receivedPayload instanceof IntMessage) {
                onReceiveInt(((IntMessage) receivedPayload).i);
            } else if (receivedPayload instanceof DoubleMessage) {
                onReceiveDouble(((DoubleMessage) receivedPayload).d);
            } else if (receivedPayload instanceof StringMessage) {
                onReceiveString(((StringMessage) receivedPayload).str);
            }
        }
    }

    /**
     * Test whether the actual ApplicationState in the Message corresponds to the expected ApplicationState stored in the AbstractApplication.
     * @param msg Message object for which we have to check the server state
     */
    private boolean checkApplicationState(Message msg) {
        return applicationState != null
                && msg != null
                && msg.getApplicationState() != null
                && msg.getApplicationState().equals(applicationState);
    }

    /**
     * Is called by a client application to request an ApplicationState change.
     * @param newState the ApplicationState the application wants to change to
     */
    protected void changeApplicationState(ApplicationState newState) {
        // Do not yet change the applicationState locally, but rather wait for a state update (confirmation) from the server.
        ClientCore.sendMessage(ClientCore.makeMessage(new ApplicationStateChange(newState))); // Send request to the server
    }

    /**
     * The client application picks a file, which we forward to the server.
     * @param path represents the picked path, may be either a directory or a file
     */
    protected void pickFile(String path) {
        ClientCore.sendMessage(ClientCore.makeMessage(new Pick(path))); // Send request to the server
    }

    /**
     * Use this to read the current application state.
     * Should be invoked by ClientCore only.
     */
    public static ApplicationState getApplicationState() {
        return applicationState;
    }

    public static AbstractActivity getActivity() {
        return activity;
    }

    public static void setActivity(AbstractActivity activity) {
        AbstractApplication.activity = activity;
    }

    public static ClientCore getClientCore() {
        return clientCore;
    }

    public static void setClientCore(ClientCore clientCore) {
        AbstractApplication.clientCore = clientCore;
    }

    /**
     * Called when a new application is created.
     * @param startState initial ApplicationState
     */
    public abstract void onApplicationStart(ApplicationState startState);

    /**
     * Called when an application is destroyed.
     */
    public abstract void onApplicationStop();

    /**
     * Called when an application switches to another state. Update UI.
     * @param newState ApplicationState we change to
     */
    public abstract void onApplicationStateChange(ApplicationState newState);

    /**
     * Called when the FilePicker on the server sends a picked file.
     * @param file File which was requested by the application
     */
    public abstract void onFilePicked(File file);

    /**
     * Called when an int message arrives.
     * @param i Message Payload
     */
    public abstract void onReceiveInt(int i);

    /**
     * Called when a double message arrives.
     * @param d Message Payload
     */
    public abstract void onReceiveDouble(double d);

    /**
     * Called when a string message arrives.
     * @param str Message Payload
     */
    public abstract void onReceiveString(String str);
}
