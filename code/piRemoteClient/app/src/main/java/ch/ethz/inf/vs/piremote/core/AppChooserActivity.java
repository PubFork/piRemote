package ch.ethz.inf.vs.piremote.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import SharedConstants.ApplicationCsts;
import SharedConstants.CoreCsts.ServerState;
import ch.ethz.inf.vs.piremote.R;

/**
 * The Application Chooser is also represented by an application.
 */
public class AppChooserActivity extends AbstractClientActivity {

    private final ServerState[] serverStates = ServerState.values();

    // UI references
    private View mProgressView;
    private View mAppChooserView;

    private final String DEBUG_TAG = "# Chooser #";
    private final String ERROR_TAG = "# Chooser ERROR #";
    private final String WARN_TAG = "# Chooser WARN #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");

        setContentView(R.layout.activity_application_chooser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get an array of all the available applications (given by the ServerState enumeration) and store their names.
        final String[] applicationNames = new String[serverStates.length-2];
        for (int i = 2; i < serverStates.length; i++) {
            applicationNames[i-2] = serverStates[i].toString();
        }

        // Display the available applications in a ListView.
        ListView mApplicationList = (ListView) findViewById(R.id.list_applications);
        mApplicationList.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, applicationNames));

        mApplicationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, @NonNull View view, int position, long id) {
                Log.d(DEBUG_TAG, "Clicked button: " + view.toString());
                chooseApplication(position+2);
            }
        });

        mProgressView = findViewById(R.id.view_progress);
        mAppChooserView = findViewById(R.id.view_application_chooser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple_navigation, menu);
        return true;
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

    @Override
    protected void showProgress(boolean show) {
        // Shows the progress UI and hides the application chooser screen.
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mAppChooserView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * Sends a request to change the application to the server.
     * @param position the index of the application in the array of serverStates
     */
    private void chooseApplication(int position) {
        switch (serverStates[position]) {
            case NONE:
            case SERVER_DOWN:
                Log.w(WARN_TAG, "Picked invalid application: " + serverStates[position]);
                break;
            default:
                sendServerStateChange(serverStates[position]);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        disconnectRunningApplication();
        Toast.makeText(this, R.string.toast_disconnected, Toast.LENGTH_SHORT).show();
    }

    // back key does not reset things
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            disconnectRunningApplication();
            Toast.makeText(this, R.string.toast_disconnected, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
