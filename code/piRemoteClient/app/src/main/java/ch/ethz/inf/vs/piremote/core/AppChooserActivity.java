package ch.ethz.inf.vs.piremote.core;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import SharedConstants.ApplicationCsts;
import SharedConstants.CoreCsts.ServerState;
import ch.ethz.inf.vs.piremote.R;

/**
 * The Application Chooser is also represented by an application.
 */
public class AppChooserActivity extends AbstractClientActivity {

    private final ServerState[] serverStates = ServerState.values();

    private final String DEBUG_TAG = "# Chooser #";
    private final String ERROR_TAG = "# Chooser ERROR #";
    private final String WARN_TAG = "# Chooser WARN #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");

        setContentView(R.layout.activity_application_chooser);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar)
        //setSupportActionBar(toolbar);

        // Get an array of all the available applications (given by the ServerState enumeration) and store their names.
        final String[] applicationNames = new String[serverStates.length-2];
        for (int i = 0; i < serverStates.length-2; i++) {
            applicationNames[i] = serverStates[i].toString();
        }

        // Display the available applications in a ListView.
        ListView mApplicationList = (ListView) findViewById(R.id.list_applications);
        mApplicationList.setAdapter(new ArrayAdapter<>(this, R.layout.custom_textview, applicationNames));

        mApplicationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, @NonNull View view, int position, long id) {
                Log.d(DEBUG_TAG, "Clicked button: " + view.toString());
                chooseApplication(position);
            }
        });

        mProgressView = findViewById(R.id.view_progress);
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
}
