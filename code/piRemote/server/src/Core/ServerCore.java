package Core;

import MessageObject.Message;
import SharedConstants.CoreCsts;
import StateObject.State;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sandro on 11.11.15.
 * Main class for the server. This will start all other threads.
 * Once started, ServerCore blocks on the main queue
 */
public class ServerCore {

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
            Message msg = mainQueue.take();
            if(!checkServerState(msg)){
                // TODO!
                continue;
            }
            // Server state is consistent
        }
    }

    public static State getState(){
        return new State(serverState, application.getApplicationState());
    }

    protected static boolean checkServerState(Message msg){
        return msg.getServerState().equals(serverState);
    }
}
