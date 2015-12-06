package ch.ethz.inf.vs.piremote.core;

import android.util.Log;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import MessageObject.Message;
import MessageObject.PayloadObject.*;
import SharedConstants.CoreCsts.ServerState;
import StateObject.State;
import ch.ethz.inf.vs.piremote.application.TrafficLightActivity;
import ch.ethz.inf.vs.piremote.core.network.ClientNetwork;

/**
 * Core part of the client running in the background and processing all incoming messages.
 */
public class ClientCore implements Runnable {

    // The ClientNetwork delivers incoming messages to the ClientCore by putting them into the queue.
    private final LinkedBlockingQueue<Message> mainQueue = new LinkedBlockingQueue<>();

    // TEST ONLY
    protected ServerState serverState;

    // TEST ONLY
    protected CoreApplication coreApplication;
    // Keep track of all activities in the background
    private ClientNetwork clientNetwork;

    private final String DEBUG_TAG = "# Core #";
    private final String ERROR_TAG = "# Core ERROR #";
    private final String WARN_TAG = "# Core WARN #";
    private final String VERBOSE_TAG = "# Core VERBOSE #";

    public ClientCore(InetAddress mServerAddress, int mServerPort, CoreApplication mCoreApplication) {
        coreApplication = mCoreApplication;
        clientNetwork = new ClientNetwork(mServerAddress, mServerPort, this);
        Log.v(VERBOSE_TAG, "Created clientNetwork: " + clientNetwork);
    }

    @Override
    public void run() {
        // start the network and connect to the server
        clientNetwork.startNetwork();
        clientNetwork.connectToServer();

        // TEST ONLY
        serverState = ServerState.NONE;
        coreApplication.startAbstractActivity(new State(ServerState.NONE, null));
        // TEST ONLY

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

    public void onDestroy() {
        clientNetwork.disconnectFromServer(); // Stop background threads TODO THREAD
    }

    /**
     * Inspect the received message and react to it. In most cases we have to forward it to the application currently running.
     * @param msg Message the Dispatcher put into the mainQueue
     */
    private void processMessage(Message msg) {

        // First, we need to check the ServerState.
        if(!consistentServerState(msg)) {
            Log.d(DEBUG_TAG, "Inconsistent server state.");
            // Inconsistent state: Change the serverState before looking at the payload.
            serverState = msg.getServerState(); // Update state
            coreApplication.startAbstractActivity(msg.getState());
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

        Log.v(VERBOSE_TAG, "Forward message to activity.");
        // Forward the message to the application so that it can check the state.
        coreApplication.processMessage(msg);
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
        coreApplication.updateFilePicker(paths);
    }

    /**
     * Close FilePicker. Adjust UI accordingly.
     */
    private void closeFilePicker() {
        Log.d(DEBUG_TAG, "Request to close the file picker from the server.");
        coreApplication.updateFilePicker(null);
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
        if (coreApplication.getCurrentActivity() == null) {
            return new State(serverState, null);
        }
        return new State(serverState, coreApplication.getCurrentActivity().getApplicationState());
    }

    public LinkedBlockingQueue<Message> getMainQueue() {
        return mainQueue;
    }
}
