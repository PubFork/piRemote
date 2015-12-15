package core;

import application.ImageApplication;
import application.RadioPi;
import application.TrafficLight;
import SharedConstants.CoreCsts;
import application.VideoApplication;

/**
 * Created by sandro on 11.11.15.
 * Register all applications here.
 */
public class ApplicationFactory {
    public static AbstractApplication makeApplication(CoreCsts.ServerState applicationToStart){
        if(applicationToStart.equals(CoreCsts.ServerState.TRAFFIC_LIGHT)) return new TrafficLight();
        if(applicationToStart.equals(CoreCsts.ServerState.VIDEO)) return new VideoApplication();
        if(applicationToStart.equals(CoreCsts.ServerState.IMAGE)) return new ImageApplication();
        if(applicationToStart.equals(CoreCsts.ServerState.RADIO_PI)) return new RadioPi();
        return null;
    }
}
