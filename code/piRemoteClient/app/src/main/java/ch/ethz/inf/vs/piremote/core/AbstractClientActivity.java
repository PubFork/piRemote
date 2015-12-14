package ch.ethz.inf.vs.piremote.core;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Arrays;

import MessageObject.Message;
import MessageObject.PayloadObject.DoubleMessage;
import MessageObject.PayloadObject.IntMessage;
import MessageObject.PayloadObject.Payload;
import MessageObject.PayloadObject.ServerStateChange;
import MessageObject.PayloadObject.StringMessage;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.TrafficLightApplicationState;
import SharedConstants.ApplicationCsts.VideoApplicationState;
import SharedConstants.CoreCsts.ServerState;
import StateObject.State;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.application.TrafficLightActivity;
import ch.ethz.inf.vs.piremote.application.VideoActivity;

/**
 * Created by andrina on 19/11/15.
 *
 * This abstract client application provides a way to access all applications on the client part in a uniform manner.
 */
public abstract class AbstractClientActivity extends AppCompatActivity {

    protected ApplicationState applicationState; // we allow the ClientCore to read the current application state

    static ClientCore clientCore;

    // UI references
    AlertDialog fpDialog;

    private final String DEBUG_TAG = "# AbstractApp #";
    private final String VERBOSE_TAG = "# AbstractApp VERBOSE #";

    final void processMessageFromThread(@NonNull final Message msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(VERBOSE_TAG, "Process message: " + msg);
                processMessage(msg);
            }
        });
    }

    final void startActivityFromThread(@NonNull final State state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(VERBOSE_TAG, "Start abstract activity with server and application state, respectively: " + state.getServerState() + state.getApplicationState());
                startAbstractActivity(state);
            }
        });
    }

    final void updateFilePickerFromThread(final String[] paths) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(VERBOSE_TAG, "Update file picker: " + Arrays.toString(paths));
                showFilePickerDialog(paths);
            }
        });
    }

    final void closeFilePickerFromThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v(VERBOSE_TAG, "Close file picker.");
                closeFilePickerDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((CoreApplication) getApplication()).setCurrentActivity(this); // Register the current activity to be notified by the core
        Log.v(VERBOSE_TAG, "ONSTART: Set current activity." + this);
    }

    @Override
    protected void onStop() {
        ((CoreApplication) getApplication()).resetCurrentActivity(this); // Unregister the current activity to no longer be notified by the core
        Log.v(VERBOSE_TAG, "ONSTOP: Removed current activity." + this);
        showProgress(false); // We reset the spinning wheel before leaving the application.
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(DEBUG_TAG, "Clicked button: " + item.toString());
        switch (item.getItemId()) {
            case R.id.menu_item_switch_app:
                // Respond to the menu's button to change the application
                closeRunningApplication();
                return true;
            case R.id.menu_item_disconnect:
                // Respond to the menu's button to disconnect from the server
                disconnectRunningApplication();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (this instanceof MainActivity) {
            super.onBackPressed();
        } else if (this instanceof AppChooserActivity) {
            disconnectRunningApplication();
            Toast.makeText(this, R.string.toast_disconnected, Toast.LENGTH_SHORT).show();
        } else {
            closeRunningApplication();
            Toast.makeText(this, R.string.toast_switch_app, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Inspects the received message and reacts to it. We can be sure that the application is still running on the server.
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

        showProgress(false); // We received an answer from the server, so we can display the activity again.
    }

    /**
     * Test whether the actual ApplicationState in the Message corresponds to the expected ApplicationState stored in the AbstractClientActivity.
     * @param msg Message object for which we have to check the application state
     */
    private boolean consistentApplicationState(@Nullable Message msg) {
        return msg != null
                && msg.getApplicationState() != null
                && msg.getApplicationState().equals(applicationState);
    }

    /**
     * Switches from the current activity to the activity representing our new server state.
     * @param state represents the activity to started
     */
    private void startAbstractActivity(@NonNull State state) {
        Class newApplication; // Start activity depending on the server state denoting which application to start.
        switch (state.getServerState()) {
            case TRAFFIC_LIGHT:
                newApplication = TrafficLightActivity.class;
                break;
            case VIDEO:
                newApplication = VideoActivity.class;
                break;
            case NONE:
                newApplication = AppChooserActivity.class; // No application is running: The client may choose an application to run.
                break;
            default:
                newApplication = MainActivity.class; // Server timed out: Disconnect and switch back to the MainActivity.
                break;
        }
        Intent applicationStartIntent = new Intent(this, newApplication);
        applicationStartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // If the application is already running on the server, wee need to forward the dictated state.
        switch (state.getServerState()) {
            case TRAFFIC_LIGHT:
                applicationStartIntent.putExtra(AppConstants.EXTRA_STATE, (TrafficLightApplicationState) state.getApplicationState());
                break;
            case VIDEO:
                applicationStartIntent.putExtra(AppConstants.EXTRA_STATE, (VideoApplicationState) state.getApplicationState());
                break;
            default:
                break;
        }
        startActivity(applicationStartIntent); // Calls onCreate()/onStart() of the new activity and onStop() of current activity.
    }

    /**
     * Places a File Picker Dialog over the current activity when the user wants to select a file or directory.
     * @param listItems array of all available files and directories
     */
    private void showFilePickerDialog(final String[] listItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Use the Builder class for convenient dialog construction
        builder.setTitle(R.string.title_dialog_file_picker)
                .setItems(listItems, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        clientCore.requestFilePicker(listItems[item]);
                    }
                })
                .setNegativeButton(R.string.button_cancel, null);
        if (fpDialog != null) {
            fpDialog.dismiss();
        }
        fpDialog = builder.create(); // Create an AlertDialog object
        fpDialog.show();

        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        fpDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clientCore.requestFilePicker(listItems[position]);
            }
        });
    }

    private void closeFilePickerDialog() {
        fpDialog.dismiss();
    }

    /**
     * Called when a BACK button is pressed. Forwards a request to the server to close the currently running application.
     */
    protected final void closeRunningApplication() {
        sendServerStateChange(ServerState.NONE);
    }

    /**
     * Called when a DISCONNECT button is pressed. Forwards a disconnect to the server and terminates all background threads.
     */
    final void disconnectRunningApplication() {
        showProgress(true);
        clientCore.destroyConnection();
    }

    /**
     * Is called by a client application to request a ServerState change.
     * @param newState the ServerState the application wants to change to
     */
    protected final void sendServerStateChange(ServerState newState) {
        Log.d(DEBUG_TAG, "Request to change the sever state to _: " + newState);
        // Do not yet change the serverState locally, but rather wait for a state update (confirmation) from the server.
        showProgress(true);
        clientCore.sendMessage(clientCore.makeMessage(new ServerStateChange(newState))); // Send request to the server
    }

    /**
     * Creates and sends an int message to the server.
     * @param i Message Payload
     */
    protected final void sendInt(int i) {
        Log.d(DEBUG_TAG, "Send an int: " + i);
        showProgress(true);
        clientCore.sendMessage(clientCore.makeMessage(new IntMessage(i)));
    }

    /**
     * Creates and sends a double message to the server.
     * @param d Message Payload
     */
    protected final void sendDouble(double d) {
        Log.d(DEBUG_TAG, "Send a double: " + d);
        showProgress(true);
        clientCore.sendMessage(clientCore.makeMessage(new DoubleMessage(d)));
    }

    /**
     * Creates and sends a string message to the core.
     * @param str Message Payload
     */
    protected final void sendString(String str) {
        Log.d(DEBUG_TAG, "Send a string: " + str);
        showProgress(true);
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

    /**
     * Called to show the progress UI and hide the view of the current UI components.
     */
    protected abstract void showProgress(boolean show);
}
