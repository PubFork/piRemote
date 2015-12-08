package ch.ethz.inf.vs.piremote.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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

        defaultActivityView = R.layout.activity_traffic_light;
        setContentView(defaultActivityView);
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

        ApplicationState startState = (ApplicationState) getIntent().getSerializableExtra(AppConstants.EXTRA_STATE);
        // Test whether the startState is set: Cast and also switch/case statement cannot handle null objects.
        if (startState != null) {
            // Toggle the button that represents our start state.
            TrafficLightApplicationState newTLState = (TrafficLightApplicationState) startState;
            mStatusView.setText(newTLState.toString());
        } else {
            Log.w(WARN_TAG, "Unable to read arguments from Intent. Cannot set initial state.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "ONDESTROY: Exiting.");
    }

    @Override
    public void onApplicationStateChange(@Nullable ApplicationState newState) {
        Log.d(DEBUG_TAG, "Changing from state _ to _: " + applicationState + newState);

        // Test whether the newState is set: Cast and also switch/case statement cannot handle null objects.
        if (newState != null) {
            // Toggle the button that represents our new state.
            TrafficLightApplicationState newTLState = (TrafficLightApplicationState) newState;
            mStatusView.setText(newTLState.toString());
        }
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
        mPathView.setText(str); //
    }

}
