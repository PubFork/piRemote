package ch.ethz.inf.vs.piremote.core;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import MessageObject.Message;
import MessageObject.PayloadObject.*;
import SharedConstants.ApplicationCsts.*;
import SharedConstants.CoreCsts.ServerState;
import StateObject.State;
import ch.ethz.inf.vs.piremote.application.TrafficLightApplication;
import ch.ethz.inf.vs.piremote.core.network.ClientNetwork;

/**
 * Core part of the client running in the background and processing all incoming messages.
 */
public class ClientCore extends IntentService {

    // The ClientNetwork delivers incoming messages to the ClientCore by putting them into the queue.
    private final LinkedBlockingQueue<Message> mainQueue = new LinkedBlockingQueue<>();

    private ServerState serverState;

    // There is ALWAYS a running application on the client: MainActivity and AppChooserActivity are also AbstractClientApplications.
//    private static AbstractClientApplication application;

    // Keep track of all activities in the background
    private ClientNetwork clientNetwork;

    private final String DEBUG_TAG = "# Core #";
    private final String ERROR_TAG = "# Core ERROR #";
    private final String WARN_TAG = "# Core WARN #";
    private final String VERBOSE_TAG = "# Core VERBOSE #";

    // Default constructor is called implicitly when starting the service.
    public ClientCore() {
        super("clientCore");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // get the arguments from the intent
        Bundle arguments = intent.getExtras();

        if (arguments != null) {
            // read the arguments out of the intent
            InetAddress address = (InetAddress) arguments.get(AppConstants.EXTRA_ADDRESS);
            int port = (int) arguments.get(AppConstants.EXTRA_PORT);

            Log.v(VERBOSE_TAG, "Received address: " + address);
            Log.v(VERBOSE_TAG, "Received port: " + port);

            if (address == null) {
                Log.w(WARN_TAG, "Could not read the ip address from the Intent. Return from service.");
                return;
            }

            // Create a ClientNetwork object, which takes care of starting all other threads running in the background.
            clientNetwork = new ClientNetwork(address, port, this);
        } else {
            Log.w(WARN_TAG, "Unable to read arguments from Intent. Return from service.");
            return;
        }

        // start the network and connect to the server
        clientNetwork.startNetwork();
        clientNetwork.connectToServer();

        // handle messages on the mainQueue that arrived over the network
        while (clientNetwork.isRunning()) {
            try {
                Message msg = mainQueue.take();
                processMessage(msg);
            } catch (InterruptedException e) {
                Log.e(ERROR_TAG, "Unable to take message from the queue. ", e.getCause());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder binder; // TODO
        return super.onBind(intent); // return binder;
    }

    @Override
    public void onDestroy() {
        clientNetwork.disconnectFromServer(); // Stop background threads
        super.onDestroy();
    }

    /**
     * Inspect the received message and react to it. In most cases we have to forward it to the application currently running.
     * @param msg Message the Dispatcher put into the mainQueue
     */
    private void processMessage(Message msg) {

        // First, we need to check the ServerState.
        if(!consistentServerState(msg)){
            Log.d(DEBUG_TAG, "Inconsistent server state.");
            // Inconsistent state: Change the serverState before looking at the payload.
            serverState = msg.getServerState(); // Update state

            Class newApplication; // Start activity depending on the server state denoting which application to start.
            switch (serverState) {
                case TRAFFIC_LIGHT:
                    newApplication = TrafficLightApplication.class;
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
            switch (serverState) {
                case TRAFFIC_LIGHT:
                    applicationStartIntent.putExtra(AppConstants.EXTRA_STATE, (TrafficLightApplicationState) msg.getApplicationState());
                    break;
                default:
                    break;
            }
            startActivity(applicationStartIntent); // Calls onDestroy() of current activity and onCreate() of the new activity. TODO
        }

        // ServerState is consistent. Look at the payload for additional information.
        if (msg.hasPayload()) {
            Payload receivedPayload = msg.getPayload();
            Log.d(DEBUG_TAG, "Process message with payload. " + receivedPayload);

            if (receivedPayload instanceof Offer) {
                startFilePicker(((Offer) receivedPayload).paths);
            } else if (receivedPayload instanceof Close) {
                closeFilePicker();
            }
        }

        // Forward the message to the application so that it can check the state.
/*
        application.processMessage(msg);
*/
    }

    /**
     * Test whether the actual ServerState in the Message corresponds to the expected ServerState stored in the ClientCore.
     * @param msg Message object for which we have to check the server state
     */
    private boolean consistentServerState(Message msg) {
        return msg != null
                && msg.getServerState() != null
                && msg.getServerState().equals(serverState);
    }

    /**
     * Is called by a client application to request a ServerState change.
     * @param newState the ServerState the application wants to change to
     */
    protected void changeServerState(ServerState newState) {
        Log.d(DEBUG_TAG, "Request to change the sever state to: " + newState);
        // Do not yet change the serverState locally, but rather wait for a state update (confirmation) from the server.
        sendMessage(makeMessage(new ServerStateChange(newState))); // Send request to the server
    }

    /**
     * The client application picks a file, which we forward to the server.
     * @param path represents the picked path, may be either a directory or a file
     */
    private void pickFile(String path) {
        Log.d(DEBUG_TAG, "Picked path. " + path);
        sendMessage(makeMessage(new Pick(path))); // Send request to the server
    }

    /**
     * Start FilePicker displaying a list of directories and files to choose from. Adjust UI accordingly.
     * @param paths list of offered directories and files
     */
    private void startFilePicker(List<String> paths) {
        Log.d(DEBUG_TAG, "Start file picker. " + paths);
        // TODO
        // application.setContentView(R.layout.overlay_file_picker);
    }

    /**
     * Close FilePicker. Adjust UI accordingly.
     */
    private void closeFilePicker() {
        Log.d(DEBUG_TAG, "Request to close the file picker from the server.");
        // TODO
        // application.setContentView(R.layout.activity_traffic_light);
    }

    /**
     * Put the Message on the sendingQueue of the SenderService.
     * @param msg Message object which the client wants to send to the server
     */
    protected void sendMessage(Message msg) {
        if (msg == null) {
            Log.w(WARN_TAG, "Wanted to send an uninitialized message.");
            return;
        }
        msg.setUuid(clientNetwork.getUuid());
        msg.setServerState(serverState);
        Log.d(DEBUG_TAG, "Send message. " + msg);
        clientNetwork.putOnSendingQueue(msg);
    }

    /**
     * Builds a message with the given payload that includes the session state.
     * @param payload Payload object to be sent to the server
     * @return Message object containing the specified payload and also the server and application state
     */
    protected Message makeMessage(Payload payload) {
        return new Message(clientNetwork.getUuid(), getState(), payload);
    }

    /**
     * Use this to read the current state (server and application state) of the client.
     * @return state object containing both the current server and application state
     */
    public State getState() {
//        return new State(serverState, application.getApplicationState()); TODO: how do we manage that the core has access to both server and application state?
        return new State(serverState, null);
    }

    public LinkedBlockingQueue<Message> getMainQueue() {
        return mainQueue;
    }
}
