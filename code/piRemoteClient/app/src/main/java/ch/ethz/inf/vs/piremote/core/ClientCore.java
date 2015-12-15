package ch.ethz.inf.vs.piremote.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MessageObject.Message;
import MessageObject.PayloadObject.Close;
import MessageObject.PayloadObject.Offer;
import MessageObject.PayloadObject.Payload;
import MessageObject.PayloadObject.Pick;
import SharedConstants.CoreCsts.ServerState;
import StateObject.State;
import ch.ethz.inf.vs.piremote.core.network.ClientNetwork;

/**
 * Core part of the client running in the background and processing all incoming messages.
 */
public class ClientCore implements Runnable {

    private ServerState serverState;

    final CoreApplication coreApplication;

    private String basePath; // client keeps track of the initial base path
    private String currentPath;
    private static final String RESET_PATH = "//";
    private static final String LEVEL_UP = "..";

    // The ClientNetwork delivers incoming messages to the ClientCore by putting them into the queue.
    private final LinkedBlockingQueue<Message> mainQueue = new LinkedBlockingQueue<>();

    @NonNull
    private final ClientNetwork clientNetwork; // Keep track of all activities in the background
    private final AtomicBoolean connected = new AtomicBoolean(false);

    boolean serverDownReceived = false; // flag to indicate receive of notification that server does not react anymore

    private final String DEBUG_TAG = "# Core #";
    private final String ERROR_TAG = "# Core ERROR #";
    private final String WARN_TAG = "# Core WARN #";
    private final String VERBOSE_TAG = "# Core VERBOSE #";

    public ClientCore(@NonNull InetAddress mServerAddress, int mServerPort, CoreApplication mCoreApplication) {
        coreApplication = mCoreApplication;
        clientNetwork = new ClientNetwork(mServerAddress, mServerPort, this);
        Log.v(VERBOSE_TAG, "Created clientNetwork: " + clientNetwork);
    }

    @Override
    public void run() {
        establishConnection();  // start the network and connect to the server

        // handle messages on the mainQueue that arrived over the network
        while (clientNetwork.isRunning() || !mainQueue.isEmpty() || !serverDownReceived) {
            try {
                Message msg = mainQueue.take();
                processMessage(msg);
            } catch (InterruptedException e) {
                Log.e(ERROR_TAG, "Unable to take message from the queue. ", e.getCause());
            }
        } // terminates as soon as the clientNetwork disconnects from the server
        Log.d(DEBUG_TAG, "Core ended.");
    }

    private void establishConnection() {
        clientNetwork.startNetwork();
        clientNetwork.connectToServer();
        connected.set(true);
        serverDownReceived = false;
    }

    void destroyConnection() {
        if (connected.get()) {
            connected.set(false);
            clientNetwork.disconnectFromServer(); // Stop background threads
        }
    }

    /**
     * Inspect the received message and react to it. In most cases we have to forward it to the application currently running.
     * @param msg Message the Dispatcher put into the mainQueue
     */
    private void processMessage(@NonNull Message msg) {

        // First, we need to check the ServerState.
        if(!consistentServerState(msg)) {
            Log.d(DEBUG_TAG, "Inconsistent server state.");
            // Inconsistent state: Change the serverState before looking at the payload.
            if (msg.getServerState() == ServerState.SERVER_DOWN) {
                serverDownReceived = true;
            }
            coreApplication.startAbstractActivity(msg.getState());
            serverState = msg.getServerState(); // Update state
        }

        // ServerState is consistent. Look at the payload for additional information.
        if (msg.hasPayload()) {
            Payload receivedPayload = msg.getPayload();
            Log.d(DEBUG_TAG, "Process message with payload. " + receivedPayload);

            if (receivedPayload instanceof Offer) {
                Log.d(DEBUG_TAG, "Start file picker: " + ((Offer) receivedPayload).paths);
                // Start FilePicker displaying a list of offered directories and files to choose from. Adjust UI accordingly.
                trackFilePicker(((Offer) receivedPayload).paths);
            } else if (receivedPayload instanceof Close) {
                Log.d(DEBUG_TAG, "Request to close the file picker from the server.");
                coreApplication.closeFilePicker();
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
    private boolean consistentServerState(@Nullable Message msg) {
        return msg != null
                && msg.getServerState() != null
                && msg.getServerState().equals(serverState);
    }

    /**
     *
     */
    private void trackFilePicker(List<String> paths) {
        String[] relativePaths; // elements to be displayed
        if (paths.get(0).startsWith(RESET_PATH)) {
            basePath = paths.get(0).substring(1); // reset base path
            currentPath = basePath;
        } else {
            currentPath = paths.get(0); // store current path anyway
        }

        if (basePath.equals(currentPath)) {
            relativePaths = new String[paths.size()-1]; // don't allow the user to navigate up
            for (int i = 1; i < paths.size(); i++) {
                relativePaths[i-1] = paths.get(i);
            }
        } else {
            relativePaths = new String[paths.size()];
            relativePaths[0] = LEVEL_UP;

            for (int i = 1; i < paths.size(); i++) {
                relativePaths[i] = paths.get(i);
            }
        }

        coreApplication.updateFilePicker(relativePaths);
    }

    /**
     * The client application picks a file, which we forward to the server.
     * @param relativePath represents the picked path, may be either a directory or a file
     */
    void requestFilePicker(String relativePath) {
        Log.d(DEBUG_TAG, "Picked path: " + relativePath);
        String absolutePath;
        switch (relativePath) {
            case LEVEL_UP:
                // compute path for folder one level up, without using ..
                Pattern pattern = Pattern.compile("(.*\\/)(.*\\/)"); // pattern for level up folder
                Matcher matcher = pattern.matcher(currentPath);
                matcher.find();
                absolutePath = matcher.group(1); // set path for ..
                break;
            default:
                absolutePath = currentPath + relativePath;
                break;
        }
        sendMessage(makeMessage(new Pick(absolutePath))); // Send request to the server
    }

    /**
     * Put the Message on the sendingQueue of the SenderService.
     * @param msg Message object which the client wants to send to the server
     */
    void sendMessage(@Nullable Message msg) {
        if (msg == null) {
            Log.w(WARN_TAG, "Wanted to send an uninitialized message.");
            return;
        }
        Log.d(DEBUG_TAG, "Send message: " + msg);
        clientNetwork.putOnSendingQueue(msg);
    }

    /**
     * Builds a message with the given payload that includes the session state.
     * @param payload Payload object to be sent to the server
     * @return Message object containing the specified payload and also the server and application state
     */
    @NonNull
    Message makeMessage(Payload payload) {
        return new Message(clientNetwork.getUuid(), getState(), payload);
    }

    /**
     * Use this to read the current state (server and application state) of the client.
     * @return state object containing both the current server and application state
     */
    @Nullable
    public State getState() {
        if (coreApplication.getCurrentActivity() == null) {
            return new State(serverState, null);
        }
        return new State(serverState, coreApplication.getCurrentActivity().applicationState);
    }

    @NonNull
    public LinkedBlockingQueue<Message> getMainQueue() {
        return mainQueue;
    }
}
