package ch.ethz.inf.vs.piremote.core;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import MessageObject.Message;
import MessageObject.PayloadObject.DoubleMessage;
import MessageObject.PayloadObject.IntMessage;
import MessageObject.PayloadObject.Payload;
import MessageObject.PayloadObject.StringMessage;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.TrafficLightApplicationState;
import StateObject.State;
import ch.ethz.inf.vs.piremote.application.TrafficLightActivity;

/**
 * Created by andrina on 19/11/15.
 *
 * This abstract client application provides a way to access all applications on the client part in a uniform manner.
 */
public abstract class AbstractClientActivity extends AppCompatActivity {

    protected ApplicationState applicationState;

    protected static ClientCore clientCore;

    @Nullable
    private FilePicker fp;

    private final String DEBUG_TAG = "# AbstractApp #";
    private final String ERROR_TAG = "#AbstractApp ERROR #";
    private final String VERBOSE_TAG = "# AbstractApp VERBOSE #";

    public final void processMessageFromThread(@NonNull final Message msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(VERBOSE_TAG, "Process message: " + msg);
                processMessage(msg);
            }});
    }

    public final void startActivityFromThread(@NonNull final State state) {
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
                if (fp == null) {
                    fp = new FilePicker(); // TODO FILE PICKER: set base path
                }
                fp.updateFilePicker(paths);
            }
        });
    }

    public final void closeFilePickerFromThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(VERBOSE_TAG, "Close file picker.");
                if (fp != null) {
                    fp.closeFilePicker();
                    fp = null; // Reset the state of the current file picker.
                } else {
                    Log.e(ERROR_TAG, "Request to close an inactive file picker.");
                }
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
        if (fp != null) {
            fp.closeFilePicker();
            fp = null;
        }
        super.onStop();
    }

    /**
     * Inspect the received message and react to it. We can be sure that the application is still running on the server.
     * @param msg Message the ClientCore forwarded
     */
    private void processMessage(@NonNull Message msg) {

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

    private void startAbstractActivity(@NonNull State state) {
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

    /**
     * Test whether the actual ApplicationState in the Message corresponds to the expected ApplicationState stored in the AbstractClientActivity.
     * @param msg Message object for which we have to check the application state
     */
    private boolean consistentApplicationState(@Nullable Message msg) {
        return applicationState != null
                && msg != null
                && msg.getApplicationState() != null
                && msg.getApplicationState().equals(applicationState);
    }

    /**
     * Allows the ClientCore to read the current application state.
     * @return ApplicationState of current application
     */
    ApplicationState getApplicationState() {
        return applicationState;
    }

    /**
     * Creates and sends an int message to the server.
     * @param i Message Payload
     */
    protected void sendInt(int i) {
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
     * The application has access to both, the old and the new ApplicationState.
     * There is no need to update ApplicationState in onApplicationStateChange().
     * @param newState ApplicationState we change to
     */
    protected abstract void onApplicationStateChange(ApplicationState newState);

    /**
     * Called when an int message arrives.
     * @param i Message Payload
     */
    protected abstract void onReceiveInt(int i);

    /**
     * Called when a double message arrives.
     * @param d Message Payload
     */
    protected abstract void onReceiveDouble(double d);

    /**
     * Called when a string message arrives.
     * @param str Message Payload
     */
    protected abstract void onReceiveString(String str);
}
