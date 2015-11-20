package ch.ethz.inf.vs.piremote.core;

import SharedConstants.CoreCsts;
import ch.ethz.inf.vs.piremote.application.TrafficLightApplication;

/**
 * Created by andrina on 19/11/15.
 *
 * Create application depending on the server state denoting which application to start.
 */
public class ApplicationFactory {

    public static AbstractApplication makeApplication(CoreCsts.ServerState applicationToStart) {
        if(applicationToStart.equals(CoreCsts.ServerState.TRAFFIC_LIGHT)) {
            return new TrafficLightApplication();
        }
        return null;
    }
}
