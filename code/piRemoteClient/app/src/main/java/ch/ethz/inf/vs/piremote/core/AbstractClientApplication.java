package ch.ethz.inf.vs.piremote.core;

import MessageObject.Message;
import MessageObject.PayloadObject.*;
import SharedConstants.ApplicationCsts.ApplicationState;

/**
 * Created by andrina on 19/11/15.
 *
 * This abstract client application provides a way to access all applications on the client part in a uniform manner.
 */
public abstract class AbstractClientApplication {

    protected ApplicationState applicationState;

    protected static AbstractActivity activity; // reference to current Activity
    protected ClientCore clientCore;

    /**
     * Inspect the received message and react to it. We can be sure that the application is still running on the server.
     * @param msg Message the ClientCore forwarded
     */
    protected void processMessage(Message msg) {

        // First, we need to check the ApplicationState.
        if(!consistentApplicationState(msg)){
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

    public void setActivity(AbstractActivity activity) {
        AbstractClientApplication.activity = activity;
    }

    /**
     * Creates and sends an int message to the server.
     * @param i Message Payload
     */
    private void sendInt(int i) {
        clientCore.sendMessage(clientCore.makeMessage(new IntMessage(i)));
    }

    /**
     * Creates and sends a double message to the server.
     * @param d Message Payload
     */
    private void sendDouble(double d) {
        clientCore.sendMessage(clientCore.makeMessage(new DoubleMessage(d)));
    }

    /**
     * Creates and sends a string message to the server.
     * @param str Message Payload
     */
    private void sendString(String str) {
        clientCore.sendMessage(clientCore.makeMessage(new StringMessage(str)));
    }

    /**
     * Called right after a new application is created.
     * @param startState initial ApplicationState
     */
    public abstract void onApplicationStart(ApplicationState startState);

    /**
     * Called before an application is destroyed.
     */
    public abstract void onApplicationStop();

    /**
     * Called just before an application switches to another state. Update UI.
     * @param newState ApplicationState we change to
     */
    public abstract void onApplicationStateChange(ApplicationState newState);

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
