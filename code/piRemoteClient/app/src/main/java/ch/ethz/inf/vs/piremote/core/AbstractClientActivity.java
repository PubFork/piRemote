package ch.ethz.inf.vs.piremote.core;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import MessageObject.Message;
import MessageObject.PayloadObject.*;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.TrafficLightApplicationState;
import SharedConstants.CoreCsts.ServerState;
import StateObject.State;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.application.TrafficLightActivity;

/**
 * Created by andrina on 19/11/15.
 *
 * This abstract client application provides a way to access all applications on the client part in a uniform manner.
 */
public abstract class AbstractClientActivity extends AppCompatActivity {

    protected ApplicationState applicationState;
    protected int defaultActivityView;

    protected static ClientCore clientCore;

    private final String DEBUG_TAG = "# AbstractApp #";
    private final String VERBOSE_TAG = "# AbstractApp VERBOSE #";

    public final void processMessageFromThread(final Message msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(VERBOSE_TAG, "Process message: " + msg);
                processMessage(msg);
            }});
    }

    public final void startActivityFromThread(final State state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(VERBOSE_TAG, "Start abstract activity with server and application state, respectively: " + state.getServerState() + state.getApplicationState());
                startAbstractActivity(state);
            }
        });
    }

    public final void updateFilePickerFromThread(final List<String> paths) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(VERBOSE_TAG, "Update file picker: " + paths);
                updateFilePicker(paths);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((CoreApplication) getApplication()).setCurrentActivity(this);
        Log.v(VERBOSE_TAG, "ONSTART: Set current activity." + this);
    }

    @Override
    protected void onStop() {
        ((CoreApplication) getApplication()).resetCurrentActivity(this);
        Log.v(VERBOSE_TAG, "ONSTOP: Removed current activity." + this);
        super.onStop();
    }

    /**
     * Inspect the received message and react to it. We can be sure that the application is still running on the server.
     * @param msg Message the ClientCore forwarded
     */
    private void processMessage(Message msg) {

        // First, we need to check the ApplicationState.
        if(!consistentApplicationState(msg)) {
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

    private void startAbstractActivity(State state) {
        Class newApplication; // Start activity depending on the server state denoting which application to start.
        switch (state.getServerState()) {
            case TRAFFIC_LIGHT:
                newApplication = TrafficLightActivity.class;
                break;
            case NONE:
                newApplication = AppChooserActivity.class; // No application is running: The client may choose an application to run.
                break;
            case SERVER_DOWN:
            default:
                newApplication = MainActivity.class; // Server timed out: Disconnect and switch back to the MainActivity.
                break;
        }
        Intent applicationStartIntent = new Intent(this, newApplication);

        // If the application is already running on the server, wee need to forward the dictated state.
        switch (state.getServerState()) {
            case TRAFFIC_LIGHT:
                applicationStartIntent.putExtra(AppConstants.EXTRA_STATE, (TrafficLightApplicationState) state.getApplicationState());
                break;
            default:
                break;
        }
        startActivity(applicationStartIntent); // Calls onStop() of current activity and onCreate()/onStart() of the new activity.
    }

    private void updateFilePicker(List<String> paths) {
        if (paths != null) {
            // update file picker overlay
            setContentView(R.layout.overlay_file_picker);
            // TODO IMPLEMENT
            // set list view
            // register listener to call pickFile(path)
        } else {
            // close file picker
            setContentView(defaultActivityView);
        }
    }

    /**
     * Test whether the actual ApplicationState in the Message corresponds to the expected ApplicationState stored in the AbstractClientActivity.
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
        Log.d(DEBUG_TAG, "Request to change the sever state from _ to _: " + clientCore.serverState + newState);
        // Do not yet change the serverState locally, but rather wait for a state update (confirmation) from the server.
        clientCore.sendMessage(clientCore.makeMessage(new ServerStateChange(newState)));
    }

    /**
     * Creates and sends an int message to the server.
     * @param i Message Payload
     */
    public void sendInt(int i) {
        Log.d(DEBUG_TAG, "Send an int: " + i);
        clientCore.sendMessage(clientCore.makeMessage(new IntMessage(i)));
    }

    /**
     * Creates and sends a double message to the server.
     * @param d Message Payload
     */
    public void sendDouble(double d) {
        Log.d(DEBUG_TAG, "Send a double: " + d);
        clientCore.sendMessage(clientCore.makeMessage(new DoubleMessage(d)));
    }

    /**
     * Creates and sends a string message to the core.
     * @param str Message Payload
     */
    public void sendString(String str) {
        Log.d(DEBUG_TAG, "Send a string: " + str);
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
