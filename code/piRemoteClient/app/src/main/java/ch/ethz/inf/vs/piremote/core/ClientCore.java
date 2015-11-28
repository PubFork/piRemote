package ch.ethz.inf.vs.piremote.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import MessageObject.Message;
import MessageObject.PayloadObject.Payload;
import SharedConstants.CoreCsts.ServerState;
import StateObject.State;
import ch.ethz.inf.vs.piremote.core.network.ClientNetwork;

/**
 * Core part of the client running in the background and processing all incoming messages.
 */
public class ClientCore extends Service {

    public static final LinkedBlockingQueue<Message> mainQueue = new LinkedBlockingQueue<>();

    protected static UUID uuid; // Store UUID assigned from the server // TODO: NETWORK ?
    protected static ServerState serverState;
    protected static AbstractApplication application;
    protected static boolean running;

    protected static InetAddress address;
    protected static int port;
    protected static ClientNetwork network;

    public ClientCore(InetAddress mAddress, int mPort) {
        address = mAddress;
        port = mPort;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a ClientNetwork object, which takes care of starting all other threads running in the background.
        // Includes a connection request.
        network = new ClientNetwork(address, port, mainQueue); // TODO: NETWORK ?

        // TODO: Then block on the main queue and process messages using processMessage().
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Destroy the ClientNetwork object and all threads running in the background.
        network.disconnectFromServer(); // TODO: NETWORK ?
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static State getState() {
        if (application != null) {
            return new State(serverState, application.getApplicationState());
        } else {
            assert serverState.equals(ServerState.NONE);
            return new State(ServerState.NONE, null);
        }
    }

    public static void processMessage(Message msg) {
    }

    protected static boolean checkServerState(Message msg){
        return msg.getServerState().equals(serverState);
    }

    protected static void sendMessage(Message msg){
        // put msg in sendingQueue of SenderService
    }

    protected static Message makeMessage(){
        return new Message(uuid, getState());
    }

    protected static Message makeMessage(Payload payload){
        return new Message(uuid, getState(), payload);
    }
}
