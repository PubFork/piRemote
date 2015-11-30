package ch.ethz.inf.vs.piremote.core;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import MessageObject.Message;
import MessageObject.PayloadObject.Close;
import MessageObject.PayloadObject.Offer;
import MessageObject.PayloadObject.Payload;
import MessageObject.PayloadObject.ServerStateChange;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.CoreCsts.ServerState;
import StateObject.State;
import ch.ethz.inf.vs.piremote.core.network.ClientNetwork;

/**
 * Core part of the client running in the background and processing all incoming messages.
 */
public class ClientCore extends Service {

    private final LinkedBlockingQueue<Message> mainQueue = new LinkedBlockingQueue<>();

    protected ServerState serverState;

    // There is ALWAYS an application running: Main and AppChooser are also AbstractApplications.
    protected AbstractApplication application;

    // Keep track of all activities in the background
    private ClientNetwork clientNetwork;

    public void createClientCore(InetAddress mAddress, int mPort, AbstractApplication mApplication) {

        // We guarantee that there is always an application running.
        application = mApplication;
        application.clientCore = this;

        // Create a ClientNetwork object, which takes care of starting all other threads running in the background.
        clientNetwork = new ClientNetwork(mAddress, mPort, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);

        // get the arguments from the intent
        Bundle arguments = intent.getExtras();

        if (arguments != null) {
            // read the arguments out of the intent
            InetAddress address = (InetAddress) arguments.get("address");
            int port = (Integer) arguments.get("port");

            // create the client core
            createClientCore(address, port, MainActivity.application);
        } else {

        }

        // start the network and connect to the server
        clientNetwork.startNetwork();
        clientNetwork.connectToServer();

        // TODO: while isRunning() do some stuff
        while (clientNetwork.isRunning()) {
            try {
                Message msg = mainQueue.take();
                processMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return result;
    }

    @Override
    public void onDestroy() {
        clientNetwork.disconnectFromServer(); // Stop background threads

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service. Return null to disallow binding.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Inspect the ServerState of the received message and react to it. In most cases we have to forward it to the application currently running.
     * @param msg Message the Dispatcher put into the mainQueue
     */
    public void processMessage(Message msg) {

        // First, we need to check the ServerState.
        if(!checkServerState(msg)){
            // Inconsistent state: Change the serverState before looking at the payload.
            serverState = msg.getServerState();

            // Destroy the running application
            application.onApplicationStop();

            // Create new application and start its execution.
            application = ApplicationFactory.makeApplication(serverState);
            application.onApplicationStart(msg.getApplicationState()); // Update UI.
        }

        // ServerState is consistent. Look at the payload for additional information.
        if (msg.hasPayload()) {
            Payload receivedPayload = msg.getPayload();

            if (receivedPayload instanceof Offer) {
                startFilePicker(((Offer) receivedPayload).paths);
            } else if (receivedPayload instanceof Close) {
                closeFilePicker();
            }
        }

        // Forward the message to the application so that it can check the state.
        application.processMessage(msg);
    }

    /**
     * Use this to read the current state (server and application state) of the client.
     * @return state object containing both the current server and application state
     */
    public State getState() {
        return new State(getServerState(), application.getApplicationState());
    }

    /**
     * Test whether the actual ServerState in the Message corresponds to the expected ServerState stored in the ClientCore.
     * @param msg Message object for which we have to check the server state
     */
    private boolean checkServerState(Message msg) {
        return msg != null
                && msg.getServerState() != null
                && msg.getServerState().equals(getServerState());
    }

    /**
     * Is called by a client application to request a ServerState change.
     * @param newState the ServerState the application wants to change to
     */
    protected void changeServerState(ServerState newState) {
        // Do not yet change the serverState locally, but rather wait for a state update (confirmation) from the server.
        sendMessage(makeMessage(new ServerStateChange(newState))); // Send request to the server
    }

    /**
     * Start FilePicker displaying a list of directories and files to choose from. Adjust UI accordingly.
     * @param paths list of offered directories and files
     */
    protected void startFilePicker(List<String> paths) {
    }

    /**
     * Close FilePicker. Adjust UI accordingly.
     */
    protected void closeFilePicker() {
    }

    /**
     * Enqueue the Message on the sendingQueue of the SenderService.
     * @param msg Message object which the client wants to send to the server
     */
    protected void sendMessage(Message msg){
        if (msg == null) return;
        if (clientNetwork.getSendingQueue() != null) {
            try {
                clientNetwork.getSendingQueue().put(msg);
            } catch (InterruptedException e) {
                System.out.println("Failed to enqueue message for sending!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Builds a session state update.
     * @return Message object containing the server and application state
     */
    protected Message makeMessage(){
        return new Message(clientNetwork.getUuid(), getState());
    }

    /**
     * Builds a message with the given payload that includes the session state.
     * @param payload Payload object to be sent to the server
     * @return Message object containing the specified payload and also the server and application state
     */
    protected Message makeMessage(Payload payload){
        return new Message(clientNetwork.getUuid(), getState(), payload);
    }

    public LinkedBlockingQueue<Message> getMainQueue() {
        return mainQueue;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public AbstractApplication getAbstractApplication() {
        return application;
    }

    public void setApplication(AbstractApplication application) {
        this.application = application;
    }
}
