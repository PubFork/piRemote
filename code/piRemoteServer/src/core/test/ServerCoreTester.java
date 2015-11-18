package core.test;

import MessageObject.Message;

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

    ServerCoreTester(BlockingQueue<Message> mainQueue){
        this.mainQueue = mainQueue;

        ///////// Phase 1: Generate events and feed them into main queue /////////

        // Start TrafficLightApplication
        // TODO

        // Switch to red
        // TODO

        // Switch to orange
        // TODO

        // Stop TrafficLightApplication
        // TODO

        // Consistency check: Switch to orange
        // TODO

        // Start TrafficLightApplication
        // TODO

        // Consistency check: Try to start it again
        // TODO

        // Consistency check: Try to stop TrafficLightApplication with invalid ServerState
        // TODO

        // Consistency check: Try to switch to red thinking it is orange (actually it is green)
        // TODO

        // Stop TrafficLightApplication thinking it is orange (should work anyway)
        // TODO

        ///////// Phase 2: Read results back from sending queue /////////

        // TODO

    }
}
