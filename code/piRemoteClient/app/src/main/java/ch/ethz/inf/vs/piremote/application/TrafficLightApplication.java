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

    public final String AID = "TrafficLightApp";

    // UI references
    private Button mBackButton;
    private Button mPickButton;
    private TextView mPathView;
    ToggleButton mRedButton;
    ToggleButton mOrangeButton;
    ToggleButton mGreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_light);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        application.setActivity(this); // TODO: NULL

        mBackButton = (Button) findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                // serverStateChange to NONE ?
            }
        });

        mPickButton = (Button) findViewById(R.id.button_pick);
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntMessage(ApplicationCsts.TL_PICK_FILE);
            }
        });

        // Keep track of the text field to change the output text when a file was picked.
        mPathView = (TextView) findViewById(R.id.picked_path);

        mRedButton = (ToggleButton) findViewById(R.id.button_red);
        mRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntMessage(ApplicationCsts.GO_RED);
            }
        });

        mOrangeButton = (ToggleButton) findViewById(R.id.button_orange);
        mOrangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntMessage(ApplicationCsts.GO_ORANGE);
            }
        });

        mGreenButton = (ToggleButton) findViewById(R.id.button_green);
        mGreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntMessage(ApplicationCsts.GO_GREEN);
            }
        });
    }

    /**
     * If the application is already running on the server, we need to adapt to the dictated state.
     * @param startState initial ApplicationState
     */
    @Override
    public void onApplicationStart(ApplicationState startState) {
        Log.d(AID, "Starting up, going to state " + startState);

        // Create Intent to adapt UI for the new application.
/*
        Intent startApplication = new Intent(activity.getBaseContext(), TrafficLightActivity.class);
        activity.startActivity(startApplication); // sets activity to current
*/

        // Test whether the startState is set: Cast and also switch/case statement cannot handle null objects.
        if (startState == null) {
            return;
        }

        // Toggle the button that represents our state.
        TrafficLightApplicationState startTLState = (TrafficLightApplicationState) startState;
        switch (startTLState) {
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

    @Override
    public void onApplicationStop() {
        Log.d(AID, "Exiting.");
    }

    @Override
    public void onApplicationStateChange(ApplicationState newState) {
        Log.d(AID, "Changing to state " + newState);

        // Test whether the applicationState is set: Cast and also switch/case statement cannot handle null objects.
        if (applicationState == null) {
            return;
        }

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

        // Test whether the newState is set: Cast and also switch/case statement cannot handle null objects.
        if (newState == null) {
            return;
        }

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

    @Override
    public void onReceiveInt(int i) {

    }

    @Override
    public void onReceiveDouble(double d) {

    }

    @Override
    public void onReceiveString(String str) {

    }

    /**
     * Send a constant int message to the server.
     * @param i Message Payload
     */
    private void sendIntMessage(int i) {
        sendInt(i);
    }
}
