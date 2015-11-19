package core;

import MessageObject.Message;
import MessageObject.PayloadObject.Offer;
import MessageObject.PayloadObject.Payload;
import MessageObject.PayloadObject.Pick;
import MessageObject.PayloadObject.ServerStateChange;
import SharedConstants.CoreCsts;
import StateObject.State;
import core.network.ServerSenderThread;
import core.test.ServerCoreTester;

import java.io.File;
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
    public static int round=0; // TEST ONLY

    public static void main(String [ ] args) throws InterruptedException {
        // Initialize state
        serverState = CoreCsts.ServerState.NONE;
        application = null;
        running = true;

        // Create and start other threads
        // TODO!

        // TEST ONLY
        ServerCoreTester st = new ServerCoreTester(mainQueue);
        st.phase1();
        // END TEST

        // Main loop
        while(running){

            // Blocking wait for messages to arrive:
            Message msg = mainQueue.take();

            // TEST ONLY
            round++;
            System.out.println("\n("+round+") Incoming:");
            System.out.println(msg);
            if(mainQueue.isEmpty()){
                running = false;
            }
            // END TEST

            if(!checkServerState(msg)){
                // Server State mismatch! Send ss to whoever sent this to us
                sendMessage(makeMessage(msg.getUuid()));
                continue;
            }

            // Server state is consistent. Is the message destined to us or to the Application?
            if(msg.hasPayload()){
                if(msg.getPayload() instanceof ServerStateChange){
                    CoreCsts.ServerState newServerState = ((ServerStateChange) msg.getPayload()).newServerState;
                    if(serverState == CoreCsts.ServerState.NONE){
                        // Application shall start
                        application = ApplicationFactory.makeApplication(newServerState);
                        if(application != null) {
                            serverState = newServerState;
                            application.onApplicationStart();
                        }// otherwise no application was running, do nothing
                    }else{
                        // Application shall change or stop
                        if(!newServerState.equals(serverState)){
                            application.onApplicationStop();
                            application = ApplicationFactory.makeApplication(newServerState);
                            if(application != null) {
                                serverState = newServerState;
                                application.onApplicationStart();
                            }else{
                                // Failed to run the new application => this is an application stop
                                serverState = CoreCsts.ServerState.NONE;
                                sendMessage(makeMessage());
                            }
                        }// otherwise we are already in the correct state, do nothing
                    }
                    continue;
                }else if(msg.getPayload() instanceof Pick){
                    String path = ((Pick) msg.getPayload()).path;
                    File f = new File(path);
                    if(f.exists()){
                        if(f.isDirectory()){
                            // Directory picked
                            sendMessage(makeOffer(msg.getUuid(), f));
                        }else{
                            // File picked
                            if(application != null) application.onFilePicked(f,msg.getUuid());
                        }
                    }else{
                        System.out.println("WARNING: Picked path <"+path+"> is not valid!");
                    }
                    continue;
                }
            }// otherwise it was an ss.

            // If we land here, core is not responsible for handling this message.
            // Forward the message to the application anyway so that it can check the state.
            if(application != null) {
                application.processMessage(msg);
            }else{
                assert serverState== CoreCsts.ServerState.NONE;
                System.out.println("OMG what is this client doin'??? It knows I have no app running and asks me to talk to it?!");
            }
        }
        // TEST ONLY
        System.out.println("\n\n--- Results ---");
        st.phase2();
        // END TEST
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
        try {
            //ServerSenderThread.sendingQueue.put(msg);
            // TEST
            System.out.println("Outgoing:");
            System.out.println(msg);
            ServerCoreTester.sendingQueue.put(msg);
            // END TEST
        } catch (InterruptedException e) {
            System.out.println("Failed to enqueue message for sending!");
            e.printStackTrace();
        }
    }

    protected static Message makeOffer(UUID recipient, File dir){
        Offer offerPayload = new Offer();
        File[] contents = dir.listFiles();
        if(contents != null) {
            for (File path : contents) {
                offerPayload.paths.add(path.getName());
            }
            return makeMessage(recipient, offerPayload);
        }else{
            System.out.println("WARNING: "+dir.getName()+" should be a directory, gut listFiles() returns null!");
            return null;
        }
    }

    protected static Message makeMessage(){
        return new Message(getState());
    }

    protected static Message makeMessage(Payload payload){
        return new Message(getState(), payload);
    }

    protected static Message makeMessage(UUID recipient){
        return new Message(recipient, getState());
    }

    protected static Message makeMessage(UUID recipient, Payload payload){
        return new Message(recipient, getState(), payload);
    }
}
