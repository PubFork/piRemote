package core.test;

import MessageObject.Message;
import MessageObject.PayloadObject.IntMessage;
import MessageObject.PayloadObject.Payload;
import MessageObject.PayloadObject.ServerStateChange;
import SharedConstants.ApplicationCsts;
import SharedConstants.CoreCsts;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sandro on 18.11.15.
 * Give this to ServerCore and let SC create it, also replace sendingQueue by this sending queue.
 * Note that this was implemented before there was a networking part. It's minimal and just to see if the Servercore
 * works at all in the first place.
 */
public class ServerCoreTester {
    public static BlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();
    protected final BlockingQueue<Message> mainQueue;
    protected final UUID uuid;

    public ServerCoreTester(BlockingQueue<Message> mainQueue){
        this.mainQueue = mainQueue;
        this.uuid = new UUID(123456,789456);
    }

    public void phase1() throws InterruptedException {
        ///////// Phase 1: Generate events and feed them into main queue /////////

        ServerStateChange ssc = new ServerStateChange();
        IntMessage im = new IntMessage();

        // Start TrafficLightApplication
        ssc.newServerState = CoreCsts.ServerState.TRAFFIC_LIGHT;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.NONE,
                null, // appl state
                ssc
        ));

        // Switch to red
        im.i = ApplicationCsts.GO_RED;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.TRAFFIC_LIGHT,
                ApplicationCsts.TrafficLightApplicationState.GREEN,
                im
        ));

        // Switch to orange
        im.i = ApplicationCsts.GO_ORANGE;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.TRAFFIC_LIGHT,
                ApplicationCsts.TrafficLightApplicationState.RED,
                im
        ));

        // Stop TrafficLightApplication
        ssc.newServerState = CoreCsts.ServerState.NONE;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.TRAFFIC_LIGHT,
                ApplicationCsts.TrafficLightApplicationState.ORANGE,
                ssc
        ));

        // Consistency check: Switch to orange as above
        im.i = ApplicationCsts.GO_ORANGE;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.TRAFFIC_LIGHT,
                ApplicationCsts.TrafficLightApplicationState.RED,
                im
        ));

        // Consistency check: Switch to orange with correct ServerState (NullPointerException if forgot to handle this)
        im.i = ApplicationCsts.GO_ORANGE;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.NONE,
                null,
                im
        ));

        // Start TrafficLightApplication
        ssc.newServerState = CoreCsts.ServerState.TRAFFIC_LIGHT;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.NONE,
                null, // appl state
                ssc
        ));

        // Consistency check: Try to start it again ("replay-attack" ;-)
        ssc.newServerState = CoreCsts.ServerState.TRAFFIC_LIGHT;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.NONE,
                null, // appl state
                ssc
        ));

        // Consistency check: Try to stop TrafficLightApplication with invalid ServerState
        ssc.newServerState = CoreCsts.ServerState.NONE;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.NONE,
                ApplicationCsts.TrafficLightApplicationState.ORANGE,
                ssc
        ));

        // Consistency check: Try to switch to red thinking it is orange (actually it is green)
        // Switch to orange
        im.i = ApplicationCsts.GO_RED;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.TRAFFIC_LIGHT,
                ApplicationCsts.TrafficLightApplicationState.ORANGE,
                im
        ));

        // Stop TrafficLightApplication thinking it is orange (actually it's green, but this should work anyway)
        ssc.newServerState = CoreCsts.ServerState.NONE;
        mainQueue.put(new Message(
                uuid,
                CoreCsts.ServerState.TRAFFIC_LIGHT,
                ApplicationCsts.TrafficLightApplicationState.ORANGE,
                ssc
        ));
    }

    public void phase2() throws InterruptedException {
        ///////// Phase 2: Read results back from sending queue /////////

        while(!sendingQueue.isEmpty()){
            System.out.println(sendingQueue.take().toString());
        }
    }
}
