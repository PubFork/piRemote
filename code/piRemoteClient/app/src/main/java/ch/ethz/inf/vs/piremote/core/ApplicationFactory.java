package ch.ethz.inf.vs.piremote.core;

import SharedConstants.CoreCsts.ServerState;
import ch.ethz.inf.vs.piremote.application.TrafficLightApplication;

/**
 * Created by andrina on 19/11/15.
 *
 * Create application depending on the server state denoting which application to start.
 */
public class ApplicationFactory {

    public static AbstractApplication makeApplication(ServerState applicationToStart) {
        switch (applicationToStart) {
            case TRAFFIC_LIGHT:
                return new TrafficLightApplication();
            case SERVER_DOWN:
                return new MainApplication(); // Server timed out: Disconnect and switch back to the MainActivity.
            case NONE:
                return new AppChooserApplication(); // No application is running: The client may choose an application to run.
            default:
                return null;
        }
    }
}
