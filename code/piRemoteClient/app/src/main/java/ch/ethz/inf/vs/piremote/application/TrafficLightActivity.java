package ch.ethz.inf.vs.piremote.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.TrafficLightApplicationState;
import SharedConstants.CoreCsts;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;
import ch.ethz.inf.vs.piremote.core.AppConstants;

/**
 * Created by andrina on 19/11/15.
 *
 * Application for demonstration purposes.
 */
public class TrafficLightActivity extends AbstractClientActivity {

    // UI references
    private Button mBackButton;
    private Button mPickButton;
    private TextView mPathView;
    private TextView mStatusView;
    private Button mRedButton;
    private Button mOrangeButton;
    private Button mGreenButton;

    private final String DEBUG_TAG = "# TLApp #";
    private final String ERROR_TAG = "# TLApp ERROR #";
    private final String WARN_TAG = "# TLApp WARN #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");

        setContentView(R.layout.activity_traffic_light);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBackButton = (Button) findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                // Request to stop current application
                clientCore.changeServerState(CoreCsts.ServerState.NONE);
            }
        });

        mPickButton = (Button) findViewById(R.id.button_pick);
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.TRAFFIC_PICK_FILE);
            }
        });

        // Keep track of the text field to change the output text when a file was picked.
        mPathView = (TextView) findViewById(R.id.picked_path);

        mStatusView = (TextView) findViewById(R.id.text_status);

        mRedButton = (Button) findViewById(R.id.button_red);
        mRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.TRAFFIC_GO_RED);
            }
        });

        mOrangeButton = (Button) findViewById(R.id.button_orange);
        mOrangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.TRAFFIC_GO_ORANGE);
            }
        });

        mGreenButton = (Button) findViewById(R.id.button_green);
        mGreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.TRAFFIC_GO_GREEN);
            }
        });

        TrafficLightApplicationState startTLState = (TrafficLightApplicationState) getIntent().getSerializableExtra(AppConstants.EXTRA_STATE);
        // Test whether the startState is set: Cannot update text from null objects.
        if (startTLState != null) {
            updateTLState(startTLState); // Set text field that represents our initial state.
        } else {
            Log.w(WARN_TAG, "Unable to read arguments from Intent. Cannot set initial state.");
        }
    }

    @Override
    public void onApplicationStateChange(@Nullable ApplicationState newState) {
        Log.d(DEBUG_TAG, "Changing from state _ to _: " + applicationState + newState);

        updateTLState((TrafficLightApplicationState) newState); // Set a text field that represents our new state.
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
        mPathView.setText(str); // We only receive string messages representing a picked file.
    }

    /**
     * Update UI elements to new state of the TL application.
     * @param newTLState ApplicationState we change to
     */
    private void updateTLState(TrafficLightApplicationState newTLState) {
        if (newTLState != null) {
            mStatusView.setText(newTLState.toString());
        }
    }
}
