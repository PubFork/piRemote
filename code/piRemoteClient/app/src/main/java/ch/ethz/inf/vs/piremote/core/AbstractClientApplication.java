package ch.ethz.inf.vs.piremote.core;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import MessageObject.Message;
import MessageObject.PayloadObject.*;
import SharedConstants.ApplicationCsts.ApplicationState;

/**
 * Created by andrina on 19/11/15.
 *
 * This abstract client application provides a way to access all applications on the client part in a uniform manner.
 */
public abstract class AbstractClientApplication extends AppCompatActivity {

    protected ApplicationState applicationState;

    protected ClientCore clientCore;

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
     * Creates and sends an int message to the server.
     * @param i Message Payload
     */
    public void sendInt(int i) {
        Log.d(DEBUG_TAG, "Send an int. " + i);
        clientCore.sendMessage(clientCore.makeMessage(new IntMessage(i))); // TODO: NULL
    }

    /**
     * Creates and sends a double message to the server.
     * @param d Message Payload
     */
    public void sendDouble(double d) {
        Log.d(DEBUG_TAG, "Send a double. " + d);
        clientCore.sendMessage(clientCore.makeMessage(new DoubleMessage(d)));
    }

    /**
     * Creates and sends a string message to the server.
     * @param str Message Payload
     */
    public void sendString(String str) {
        Log.d(DEBUG_TAG, "Send a string. " + str);
        clientCore.sendMessage(clientCore.makeMessage(new StringMessage(str)));
    }

    /**
     * Called just before an application switches to another state. Update UI.
     * @param newState ApplicationState we change to
     */
    public abstract void onApplicationStateChange(ApplicationState newState); // No need to update applicationState in onApplicationStateChange().

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
