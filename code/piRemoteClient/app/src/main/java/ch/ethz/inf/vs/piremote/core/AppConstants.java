package ch.ethz.inf.vs.piremote.core;

/**
 * Created by andrina on 05/12/15.
 * Constants shared between different components of the Android app.
 */
public class AppConstants {

    // Constant strings for the access to the shared preferences.
    static final String SETTINGS_FILENAME = "ServerSettings";
    static final String SERVER_ADDRESS_STR = "address";
    static final String SERVER_PORT_STR = "port";

    // default information for server address and port
    static final String SERVER_ADDRESS = "10.0.2.2"; // This address is for the emulator.
    static final String SERVER_PORT = "4446";

    // Constant strings for the keys of the extras (key-value-pairs) on Intents between Activities.
    public static final String EXTRA_STATE = "piremote.extra.STATE";
    public static final String EXTRA_PATH_LIST = "piremote.extra.PATH_LIST";
    public static final String EXTRA_PICKED_PATH = "piremote.extra.PICKED_PATH";
}
