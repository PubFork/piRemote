package ch.ethz.inf.vs.piremote.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.TrafficLightApplicationState;
import SharedConstants.ApplicationCsts.ApplicationState;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientApplication;

/**
 * Created by andrina on 19/11/15.
 *
 * Application for demonstration purposes.
 */
public class TrafficLightApplication extends AbstractClientApplication {

    // UI references
    private Button mBackButton;
    private Button mPickButton;
    private TextView mPathView;
    private ToggleButton mRedButton;
    private ToggleButton mOrangeButton;
    private ToggleButton mGreenButton;

    private final String DEBUG_TAG = "# TLApp #";
    private final String ERROR_TAG = "# TLApp ERROR #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "Starting up.");

        setContentView(R.layout.activity_traffic_light);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        application.setActivity(this); // TODO: NULL

        mBackButton = (Button) findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                // TODO
                // serverStateChange to NONE ?
            }
        });

        mPickButton = (Button) findViewById(R.id.button_pick);
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                // TEST ONLY
                setContentView(R.layout.overlay_file_picker);
                // TEST ONLY
                sendIntMessage(ApplicationCsts.TL_PICK_FILE);
            }
        });

        // Keep track of the text field to change the output text when a file was picked.
        mPathView = (TextView) findViewById(R.id.picked_path);

        mRedButton = (ToggleButton) findViewById(R.id.button_red);
        mRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendIntMessage(ApplicationCsts.GO_RED);
            }
        });

        mOrangeButton = (ToggleButton) findViewById(R.id.button_orange);
        mOrangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendIntMessage(ApplicationCsts.GO_ORANGE);
            }
        });

        mGreenButton = (ToggleButton) findViewById(R.id.button_green);
        mGreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendIntMessage(ApplicationCsts.GO_GREEN);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "Exiting.");
    }

    @Override
    public void onApplicationStateChange(ApplicationState newState) {
        Log.d(DEBUG_TAG, "Changing to state: " + newState);

        // Test whether the applicationState is set: Cast and also switch/case statement cannot handle null objects.
        if (applicationState != null) {
            // Untoggle the button that represents our old state.
            TrafficLightApplicationState oldTLState = (TrafficLightApplicationState) applicationState;
            switch (oldTLState) {
                case RED:
                    mRedButton.setChecked(false);
                    break;
                case ORANGE:
                    mOrangeButton.setChecked(false);
                    break;
                case GREEN:
                    mGreenButton.setChecked(false);
                    break;
            }
        }

        // Test whether the newState is set: Cast and also switch/case statement cannot handle null objects.
        if (newState != null) {
            // Toggle the button that represents our new state.
            TrafficLightApplicationState newTLState = (TrafficLightApplicationState) newState;
            switch (newTLState) {
                case RED:
                    mRedButton.setChecked(true);
                    break;
                case ORANGE:
                    mOrangeButton.setChecked(true);
                    break;
                case GREEN:
                    mGreenButton.setChecked(true);
                    break;
            }
        }
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
        mPathView.setText(str); //
    }

    /**
     * Send a constant int message to the server.
     * @param i Message Payload
     */
    private void sendIntMessage(int i) {
        sendInt(i);
    }
}
