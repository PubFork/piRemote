package ch.ethz.inf.vs.piremote.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;

import SharedConstants.ApplicationCsts;
import ch.ethz.inf.vs.piremote.R;

/**
 * The MainActivity is also represented by an application.
 */
public class MainActivity extends AbstractClientApplication {

    private static ClientCore clientCore;
    private InetAddress mServerAddress;
    private int mServerPort;

    private static Intent clientCoreIntent;

    // UI references
    private EditText mAddressView;
    private EditText mPortView;

    // Store server address and port entered
    private static SharedPreferences settings;
    // Constant strings for the access to the shared preferences
    private static final String SETTINGS_FILENAME = "ServerSettings";
    private static final String SERVER_ADDRESS_STR = "address";
    private static final String SERVER_PORT_STR = "port";

    // TODO: default information for server address and port
    private static final String SERVER_ADDRESS = "10.0.2.2"; // This address is for the emulator.
    private static final String SERVER_PORT = "4446";

    private final String DEBUG_TAG = "# Main #";
    private final String ERROR_TAG = "# Main ERROR #";
    private final String VERBOSE_TAG = "# Main VERBOSE #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "Starting up.");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Keep track of the settings form.
        mAddressView = (EditText) findViewById(R.id.server_address);
        mPortView = (EditText) findViewById(R.id.server_port);

        Button mConnectButton = (Button) findViewById(R.id.button_connect);
        mConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                connectToPi();
            }
        });

        // We want to stop the background processes whenever we return to the MainActivity and started them before by connecting to the server.
        if (clientCore != null) { // TODO maybe use clientCoreIntent != null
            disconnectFromPi();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Restore preferences
        settings = getSharedPreferences(SETTINGS_FILENAME, 0);
        mAddressView.setText(settings.getString(SERVER_ADDRESS_STR, SERVER_ADDRESS));
        mPortView.setText(settings.getString(SERVER_PORT_STR, SERVER_PORT));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Store preference changes
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SERVER_ADDRESS_STR, mAddressView.getText().toString());
        editor.putString(SERVER_PORT_STR, mPortView.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "Exiting.");
    }

    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        Log.d(DEBUG_TAG, "Changing to state: " + newState);
    }

    @Override
    public void onReceiveInt(int i) {
        Log.d(DEBUG_TAG, "Received an int. " + i);
    }

    @Override
    public void onReceiveDouble(double d) {
        Log.d(DEBUG_TAG, "Received a double. " + d);
    }

    @Override
    public void onReceiveString(String str) {
        Log.d(DEBUG_TAG, "Received a string. " + str);
    }

    /**
     * Attempts to connect to the Raspberry Pi using the address and port specified.
     * If the entered server information is invalid, errors are displayed and no actual connection attempt is made.
     */
    private void connectToPi() {
        if(validServerInformation()){

            // display "connecting"
            Toast.makeText(this, R.string.toast_connecting, Toast.LENGTH_SHORT).show();

            clientCore = new ClientCore(); // TODO
            // set up the intent and put some arguments to it
            clientCoreIntent = new Intent(this, ClientCore.class);
            clientCoreIntent.putExtra(SERVER_ADDRESS_STR, mServerAddress);
            clientCoreIntent.putExtra(SERVER_PORT_STR, mServerPort);
            Log.v(VERBOSE_TAG, "Created intent to start service.");

            startService(clientCoreIntent);
            Log.v(VERBOSE_TAG, "Started service.");
        }
    }

    /**
     * Disconnect from the Raspberry Pi and terminate all running threads.
     */
    private void disconnectFromPi() {
        Log.v(VERBOSE_TAG, "Attempting to stop service.");
        stopService(clientCoreIntent); // Calls onDestroy() which takes care of cleaning up all resources the service used.
        clientCore = null; // TODO maybe set clientCoreInten = null;
        Log.v(VERBOSE_TAG, "Stopped service.");
    }

    /**
     * Checks whether the address and port specified for the Raspberry Pi are valid.
     */
    private boolean validServerInformation(){

        // Reset errors
        mAddressView.setError(null);
        mPortView.setError(null);

        boolean validAddress = true;
        boolean validPort = true;

        // Get values at the time of the connection attempt.
        String mAddressRepr = mAddressView.getText().toString();
        String mPortRepr = mPortView.getText().toString();

        // Check for a valid address
        if(TextUtils.isEmpty(mAddressRepr)) {
            mAddressView.setError(getString(R.string.error_field_required));
            validAddress = false;
        } else if (!validAddress(mAddressRepr)){
            mAddressView.setError(getString(R.string.error_invalid_address));
            validAddress = false;
        }

        // Check for a valid port
        if (TextUtils.isEmpty(mPortRepr)) {
            mPortView.setError(getString(R.string.error_field_required));
            validPort = false;
        } else if (!validPort(mPortRepr)){
            mPortView.setError(getString(R.string.error_invalid_port));
            validPort = false;
        }

        if (!validAddress) {
            // Address is not set or invalid; focus the address view.
            mAddressView.requestFocus();
        } else if (!validPort) {
            // Port is not set or invalid; focus the port view.
            mPortView.requestFocus();
        }

        return validAddress && validPort;
    }

    /**
     * Test for a valid serverAddress by converting it into an InetAddress.
     * @param serverAddress String representation of the input for the server address
     * @return true if the input represents a valid ip address
     */
    private boolean validAddress(String serverAddress) {
        try {
            mServerAddress = InetAddress.getByName(serverAddress);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Test for a valid serverPort.
     * @param serverPort String representation of the input for the server port
     * @return true if the input represents a valid port
     */
    private boolean validPort(String serverPort) {
        try {
            mServerPort = Integer.parseInt(serverPort);
        } catch (Exception e) {
            return false;
        }
        return 0 <= mServerPort && mServerPort < 65536;
    }
}
