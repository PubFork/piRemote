package ch.ethz.inf.vs.piremote.core;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import SharedConstants.ApplicationCsts;
import SharedConstants.CoreCsts.ServerState;
import StateObject.State;
import ch.ethz.inf.vs.piremote.R;

/**
 * The Application Chooser is also represented by an application.
 */
public class AppChooserActivity extends AbstractClientActivity {

    private final ServerState[] serverStates = ServerState.values();

    private final String DEBUG_TAG = "# Chooser #";
    private final String ERROR_TAG = "# Chooser ERROR #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "Starting up.");

        defaultActivityView = R.layout.activity_application_chooser;
        setContentView(defaultActivityView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get an array of all the available applications (given by the ServerState enumeration) and store their names.
        final String[] applicationNames = new String[serverStates.length];
        for (int i = 0; i < serverStates.length; i++) {
            applicationNames[i] = serverStates[i].name();
        }

        // Display the available applications in a ListView.
        ListView mApplicationList = (ListView) findViewById(R.id.list_application);
        mApplicationList.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, applicationNames));

        mApplicationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(DEBUG_TAG, "Clicked button: " + view.toString());
                chooseApplication(position);
            }
        });
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

    private void chooseApplication(int position) {
        switch (serverStates[position]) {
            case NONE:
                break;
            case SERVER_DOWN:
                // TEST ONLY
                clientCore.serverState = ServerState.SERVER_DOWN;
                clientCore.coreApplication.startAbstractActivity(new State(ServerState.SERVER_DOWN, null));
                // TEST ONLY
                break;
            default:
                // TEST ONLY
                clientCore.serverState = serverStates[position];
                clientCore.coreApplication.startAbstractActivity(new State(serverStates[position], null));
                // TEST ONLY
                clientCore.changeServerState(serverStates[position]);
                break;
        }
    }
}
