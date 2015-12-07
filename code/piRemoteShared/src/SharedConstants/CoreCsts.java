package SharedConstants;

import java.io.Serializable;

/**
 * Created by sandro on 10.11.15.
 * Contains all constants used by ServerCore and ClientCore
 */
public class CoreCsts implements Serializable {

    public enum ServerState {
        // Constants that indicate what application is currently running, use NONE if no application is running
        NONE("Application Chooser"),
        SERVER_DOWN("Connect Screen"), // Message put into Client MainQueue by Client Dispatcher when the server times out
        TRAFFIC_LIGHT("Traffic Light"),
        MULTIMEDIA("Multimedia");

        private final String stateRepresentation;

        ServerState(String s) {
            this.stateRepresentation = s;
        }

        @Override
        public String toString() {
            return stateRepresentation;
        }
    }
}
