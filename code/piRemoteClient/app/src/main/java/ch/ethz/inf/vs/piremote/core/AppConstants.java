package ch.ethz.inf.vs.piremote.core;

import android.content.SharedPreferences;

/**
 * Created by andrina on 05/12/15.
 * Constants shared between different components of the Android app.
 */
public class AppConstants {

    // Constant strings for the access to the shared preferences.
    protected static final String SETTINGS_FILENAME = "ServerSettings";
    protected static final String SERVER_ADDRESS_STR = "address";
    protected static final String SERVER_PORT_STR = "port";

    // default information for server address and port
    protected static final String SERVER_ADDRESS = "10.0.2.2"; // This address is for the emulator.
    protected static final String SERVER_PORT = "4446";

    // Constant strings for the keys of the extras (key-value-pairs) on Intents between Activities.
    public static final String EXTRA_STATE = "piremote.extra.STATE";
}
