package Core;

import MessageObject.Message;
import MessageObject.PayloadObject.Pick;
import MessageObject.PayloadObject.ServerStateChange;
import SharedConstants.CoreCsts;
import StateObject.State;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sandro on 11.11.15.
 * Main class for the server. This will start all other threads.
 * Once started, ServerCore blocks on the main queue
 */
public class ServerCore{

    public static final BlockingQueue<Message> mainQueue = new LinkedBlockingQueue<>();

    protected static CoreCsts.ServerState serverState;
    protected static AbstractApplication application;
    protected static boolean running;

    public static void main(String [ ] args) throws InterruptedException {
        // Initialize state
        serverState = CoreCsts.ServerState.NONE;
        application = null;
        running = true;

        // Create and start other threads
        // TODO!

        // Main loop
        while(running){
            // Blocking wait for messages to arrive:
            Message msg = mainQueue.take();
            if(!checkServerState(msg)){
                // Server State mismatch! Send ss to whoever sent this to us
                sendMessage(makeSS(msg.getUuid()));
                continue;
            }

            // Server state is consistent. Is the message destined to us or to the Application?
            if(msg.hasPayload()){
                if(msg.getPayload() instanceof ServerStateChange){
                    // TODO!
                    continue;
                }else if(msg.getPayload() instanceof Pick){
                    // TODO!
                    continue;
                }
            }// otherwise it was an ss.

            // Forward the message to the application anyway so that it can check the state.
            if(application != null) {
                application.processMessage(msg);
            }
        }
    }

    public static State getState(){
        if(application != null) {
            return new State(serverState, application.getApplicationState());
        }else{
            assert serverState.equals(CoreCsts.ServerState.NONE);
            return new State(CoreCsts.ServerState.NONE, null);
        }
    }

    protected static boolean checkServerState(Message msg){
        return msg.getServerState().equals(serverState);
    }

    protected static void sendMessage(Message msg){
        // TODO!
    }

    protected static Message makeSS(){
        return new Message(getState());
    }

    protected static Message makeSS(UUID recipient){
        return new Message(recipient, getState());
    }
}
