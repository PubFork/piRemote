package ch.ethz.inf.vs.piremote.core;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import MessageObject.Message;
import MessageObject.PayloadObject.*;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.CoreCsts.ServerState;

/**
 * Created by andrina on 19/11/15.
 *
 * This abstract client application provides a way to access all applications on the client part in a uniform manner.
 */
public abstract class AbstractClientApplication extends AppCompatActivity {

    protected ApplicationState applicationState;

    private static Intent clientCoreIntent;

//    protected ClientCore clientCore;

    private final String DEBUG_TAG = "# AbstractApp #";
    private final String VERBOSE_TAG = "# AbstractApp VERBOSE #";

    /**
     * Inspect the received message and react to it. We can be sure that the application is still running on the server.
     * @param msg Message the ClientCore forwarded
     */
    protected void processMessage(Message msg) {

        // First, we need to check the ApplicationState.
        if(!consistentApplicationState(msg)){
            Log.d(DEBUG_TAG, "Inconsistent application state.");
            // Inconsistent state: Change the applicationState before looking at the payload.
            onApplicationStateChange(msg.getApplicationState()); // Update UI.
            applicationState = msg.getApplicationState();
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
     * Test whether the actual ApplicationState in the Message corresponds to the expected ApplicationState stored in the AbstractClientApplication.
     * @param msg Message object for which we have to check the application state
     */
    private boolean consistentApplicationState(Message msg) {
        return applicationState != null
                && msg != null
                && msg.getApplicationState() != null
                && msg.getApplicationState().equals(applicationState);
    }

    /**
     * Allows the ClientCore to read the current application state.
     * @return ApplicationState of current application
     */
    protected ApplicationState getApplicationState() {
        return applicationState;
    }

    /**
     * Is called by a client application to request a ServerState change.
     * @param newState the ServerState the application wants to change to
     */
    protected void sendServerStateChange(ServerState newState) {
        Log.d(DEBUG_TAG, "Request to change the sever state to: " + newState);
        // Do not yet change the serverState locally, but rather wait for a state update (confirmation) from the server.
        Message msg = makeMessage(new ServerStateChange(newState));
        forwardMessage(msg); // Send request to the server
    }

    /**
     * Creates an int message and forwards it to the core.
     * @param i Message Payload
     */
    public void sendInt(int i) {
        Log.d(DEBUG_TAG, "Send an int. " + i);
        Message msg = makeMessage(new IntMessage(i));
        forwardMessage(msg); // Send request to the server
    }

    /**
     * Creates a double message and forwards it to the core.
     * @param d Message Payload
     */
    public void sendDouble(double d) {
        Log.d(DEBUG_TAG, "Send a double. " + d);
        Message msg = makeMessage(new DoubleMessage(d));
        forwardMessage(msg); // Send request to the server
    }

    /**
     * Creates a string message and forwards it to the core.
     * @param str Message Payload
     */
    public void sendString(String str) {
        Log.d(DEBUG_TAG, "Send a string. " + str);
        Message msg = makeMessage(new StringMessage(str));
        forwardMessage(msg); // Send request to the server
    }

    /**
     * Forward the message to the core service.
     * @param msg Message object which the client wants to send to the server
     */
    protected void forwardMessage(Message msg) {
//        bindService(); TODO: core has to add uuid and serverState
//        clientCore.sendMessage(msg);
    }

    /**
     * Builds a message with the given payload that includes the application state.
     * This requires the ClientCore to add the uuid and the server state to the message later on.
     * @param payload Payload object to be sent to the server
     * @return Message object containing the specified payload and the application state
     */
    private Message makeMessage(Payload payload) {
        return new Message(null, applicationState, payload);
    }

    /**
     * Called just before an application switches to another state. Update UI.
     * @param newState ApplicationState we change to
     */
    public abstract void onApplicationStateChange(ApplicationState newState); // No need to update applicationState in onApplicationStateChange(). TODO

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
