package ch.ethz.inf.vs.piremote.core;

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
public class MainActivity extends AbstractClientActivity {

    private InetAddress mServerAddress;
    private int mServerPort;

    // UI references
    private EditText mAddressView;
    private EditText mPortView;

    private static Thread coreThread;

    // Store server address and port entered
    private SharedPreferences settings;

    private final String DEBUG_TAG = "# Main #";
    private final String ERROR_TAG = "# Main ERROR #";
    private final String VERBOSE_TAG = "# Main VERBOSE #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");

        defaultActivityView = R.layout.activity_main;
        setContentView(defaultActivityView);
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
                startCore();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // We want to stop the background processes whenever we return to the MainActivity and started them before by connecting to the server.
        clientCore.destroyConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(VERBOSE_TAG, "ONRESUME: Restore preferences.");

        // Restore preferences
        settings = getSharedPreferences(AppConstants.SETTINGS_FILENAME, 0);
        mAddressView.setText(settings.getString(AppConstants.SERVER_ADDRESS_STR, AppConstants.SERVER_ADDRESS));
        mPortView.setText(settings.getString(AppConstants.SERVER_PORT_STR, AppConstants.SERVER_PORT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(VERBOSE_TAG, "ONPAUSE: Save preferences.");

        // Store preference changes
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AppConstants.SERVER_ADDRESS_STR, mAddressView.getText().toString());
        editor.putString(AppConstants.SERVER_PORT_STR, mPortView.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "ONDESTROY: Exiting.");
    }

    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        Log.d(DEBUG_TAG, "Changing from state _ to _: " + applicationState + newState);
    }

    @Override
    public void onReceiveInt(int i) {
        Log.d(DEBUG_TAG, "Received an int: " + i);
    }

    @Override
    public void onReceiveDouble(double d) {
        Log.d(DEBUG_TAG, "Received a double: " + d);
    }

    @Override
    public void onReceiveString(String str) {
        Log.d(DEBUG_TAG, "Received a string: " + str);
    }

    /**
     * Attempts to connect to the Raspberry Pi using the address and port specified.
     * If the entered server information is invalid, errors are displayed and no actual connection attempt is made.
     */
    private void startCore() {
        if(validServerInformation()){

            // display "connecting"
            Toast.makeText(this, R.string.toast_connecting, Toast.LENGTH_SHORT).show();

            clientCore = new ClientCore(mServerAddress, mServerPort, (CoreApplication) getApplication());
            ((CoreApplication) getApplication()).setCoreThread(new Thread(clientCore));
            Log.v(VERBOSE_TAG, "Created core thread.");

            ((CoreApplication) getApplication()).getCoreThread().start();
            Log.v(VERBOSE_TAG, "Started core thread.");
        }
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
