package SharedConstants;

/**
 * Created by sandro on 10.11.15.
 * Contains all constants used by applications, grouped by application
 * Must provide at least XApplicationState where X is the name of the application
 */
public class ApplicationCsts {

    public interface ApplicationState{}

    public enum TrafficLightApplicationState implements ApplicationState{
        RED,
        ORANGE,
        GREEN
    }

    // TrafficLightApplication
    public static final int GO_GREEN = 0;
    public static final int GO_ORANGE = 1;
    public static final int GO_RED = 2;
}
