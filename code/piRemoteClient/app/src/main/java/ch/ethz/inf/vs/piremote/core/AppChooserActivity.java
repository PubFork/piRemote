package ch.ethz.inf.vs.piremote.core;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import SharedConstants.CoreCsts.ServerState;
import ch.ethz.inf.vs.piremote.R;

public class AppChooserActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_chooser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get an array of all the available applications (given by the ServerState enumeration) and store their names.
        final ServerState[] serverStates = ServerState.values();
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
                switch (serverStates[position]) {
                    case NONE:
                        break;
                    case SERVER_DOWN:
                        // TEST ONLY
                        // Switch to back to main
                        application.onApplicationStop();
                        application = ApplicationFactory.makeApplication(ServerState.SERVER_DOWN);
                        application.onApplicationStart(null);
                        // TEST ONLY
                        break;
                    default:
                        application.clientCore.changeServerState(serverStates[position]);
                        break;
                }
            }
        });
    }
}
