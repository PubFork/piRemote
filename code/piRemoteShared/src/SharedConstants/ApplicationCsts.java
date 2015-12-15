package SharedConstants;

import java.io.Serializable;

/**
 * Created by sandro on 10.11.15.
 * Contains all constants used by applications, grouped by application
 * Must provide at least XApplicationState where X is the name of the application
 */
public class ApplicationCsts implements Serializable {

    public interface ApplicationState{}

    // TrafficLightApplication
    public enum TrafficLightApplicationState implements ApplicationState{
        TRAFFIC_RED("Red"),
        TRAFFIC_ORANGE("Orange"),
        TRAFFIC_GREEN("Green");

        private final String stateRepresentation;
        TrafficLightApplicationState(String s) {
            this.stateRepresentation = s;
        }

        @Override
        public String toString() {
            return stateRepresentation;
        }
    }
    public static final int TRAFFIC_GO_GREEN = 0;
    public static final int TRAFFIC_GO_ORANGE = 1;
    public static final int TRAFFIC_GO_RED = 2;
    public static final int TRAFFIC_PICK_FILE = 3;


    // VideoApplication
    public enum VideoApplicationState implements ApplicationState{
        VIDEO_STOPPED,
        VIDEO_PLAYING,
        VIDEO_PAUSED
    }
    public static final int VIDEO_PLAY = 0;
    public static final int VIDEO_PAUSE = 1;
    public static final int VIDEO_STOP = 2;
    public static final int VIDEO_JUMP_BACK = 3;
    public static final int VIDEO_JUMP_FORWARD = 4;
    public static final int VIDEO_SPEED_SLOWER = 5;
    public static final int VIDEO_SPEED_FASTER = 6;
    public static final int VIDEO_VOLUME_INCREASE = 7;
    public static final int VIDEO_VOLUME_DECREASE = 8;
    public static final int VIDEO_PICK_FILE = 9;


    // ImageApplication
    public enum ImageApplicationState implements ApplicationState{
        IMAGE_DISPLAYED,
        IMAGE_NOT_DISPLAYED
    }
    public static final int IMAGE_PICK_FILE = 0;
    public static final int IMAGE_SHOW = 1;
    public static final int IMAGE_HIDE = 2;


    // RadioPiApplication
    public enum RadioPiApplicationState implements ApplicationState{
        RADIO_STOP("Stop"),
        RADIO_INIT("Initialize"),
        RADIO_PLAY("Play");

        private final String stateRepresentation;
        RadioPiApplicationState(String s) {
            this.stateRepresentation = s;
        }

        @Override
        public String toString() {
            return stateRepresentation;
        }
    }
    public static final int RADIO_GO_PLAY = 0;
    public static final int RADIO_GO_INIT = 1;
    public static final int RADIO_GO_STOP = 2;
    public static final int RADIO_PICK_FILE = 3;
}
