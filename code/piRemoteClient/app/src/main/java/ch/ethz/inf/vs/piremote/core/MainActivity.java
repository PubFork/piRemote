package ch.ethz.inf.vs.piremote.core;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;

import ch.ethz.inf.vs.piremote.R;

public class MainActivity extends AbstractActivity {

    private InetAddress mServerAddress;
    private int mServerPort;
    private static ClientCore clientCore;

    // Store server address and port entered
    static SharedPreferences settings;
    // Constant strings for the access to the shared preferences
    private static final String SETTINGS_FILENAME = "ServerSettings";
    private static final String SERVER_ADDRESS_STR = "address";
    private static final String SERVER_PORT_STR = "port";

    // TODO: default information for server address and port
    private static final String SERVER_ADDRESS = "10.0.2.2"; // This address is for the emulator.
    private static final String SERVER_PORT = "4446";

    // UI references
    private EditText mAddressView;
    private EditText mPortView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                connectToPi();
            }
        });
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

    /**
     * Attempts to connect to the Raspberry Pi using the address and port specified.
     * If the entered server information is invalid, errors are displayed and no actual connection attempt is made.
     */
    private void connectToPi() {
        if(validServerInformation()){

            application = new MainApplication(); // create application for main
            AbstractApplication.setActivity(this); // set reference to current activity

            // TODO: I think the proper way to start a service would be to call startService() on an Intent.
            // And use binding to pass arguments.
            // startService(new Intent(this,ClientCore.class));
            clientCore = new ClientCore(mServerAddress, mServerPort, application);
            clientCore.onCreate();

            // Let the user know that something is going on.
            Toast.makeText(this, R.string.toast_connecting, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Disconnect from the Raspberry Pi and terminate all running threads.
     */
    void disconnetFromPi() {
        // TODO: stopService();
        clientCore.onDestroy();
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
     */
    private boolean validPort(String serverPort){
        try {
            mServerPort = Integer.parseInt(serverPort);
        } catch (Exception e){
            return false;
        }
        return 0 <= mServerPort && mServerPort < 65536;
    }
}
