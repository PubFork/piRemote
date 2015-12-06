package ch.ethz.inf.vs.piremote.core;

import SharedConstants.CoreCsts.ServerState;
import ch.ethz.inf.vs.piremote.application.TrafficLightActivity;

/**
 * Created by andrina on 19/11/15.
 *
 * Create application depending on the server state denoting which application to start.
 */
public class ApplicationFactory {

    public static AbstractClientActivity makeApplication(ServerState applicationToStart) {
        switch (applicationToStart) {
            case TRAFFIC_LIGHT:
                return new TrafficLightActivity();
            case NONE:
                return new AppChooserActivity(); // No application is running: The client may choose an application to run.
            case SERVER_DOWN:
            default:
                return new MainActivity(); // Server timed out: Disconnect and switch back to the MainActivity.
        }
    }
}
