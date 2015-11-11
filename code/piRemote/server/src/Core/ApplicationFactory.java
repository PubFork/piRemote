package Core;

import Application.TrafficLight;
import SharedConstants.CoreCsts;

/**
 * Created by sandro on 11.11.15.
 */
public class ApplicationFactory {
    public static AbstractApplication makeApplication(CoreCsts.ServerState applicationToStart){
        if(applicationToStart.equals(CoreCsts.ServerState.TRAFFIC_LIGHT)) return new TrafficLight();
        return null;
    }
}
