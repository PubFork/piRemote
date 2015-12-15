package ch.ethz.inf.vs.piremote.application;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.RadioPiApplicationState;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;
import ch.ethz.inf.vs.piremote.core.AppConstants;

/**
 * Created by FR4NK-W on 12/12/15.
 */
public class RadioPiActivity extends AbstractClientActivity {

    // UI references
    private TextView mPathView;
    private TextView mFreqView;
    private TextView mStatusView;
    private View mProgressView;
    private View mRadioPiView;

    private final String DEBUG_TAG = "# RPApp #";
    private final String WARN_TAG = "# RPApp WARN #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");

        setContentView(R.layout.activity_radiopi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button mPickButton = (Button) findViewById(R.id.button_pick);
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.RADIO_PICK_FILE);
            }
        });

        // Keep track of the text field to change the output text when a file was picked.
        mPathView = (TextView) findViewById(R.id.picked_path);
        mFreqView = (TextView) findViewById(R.id.frequency);

        mStatusView = (TextView) findViewById(R.id.text_status);

        Button mStopButton = (Button) findViewById(R.id.button_stop);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.RADIO_GO_STOP);
            }
        });

        Button mInitButton = (Button) findViewById(R.id.button_init);
        mInitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());

                String filePath = mPathView.getText().toString();
                String freq = mFreqView.getText().toString();
                freq = String.valueOf(Math.max(Double.valueOf(freq), 87.5)); // freq in valid range
                freq = String.valueOf(Math.min(Double.valueOf(freq), 108.0));
                String stringBuild = filePath + ":" + freq;

                Log.d(DEBUG_TAG, "String sent: " + stringBuild);
                sendString(stringBuild);
                sendInt(ApplicationCsts.RADIO_GO_INIT);
            }
        });

        Button mPlayButton = (Button) findViewById(R.id.button_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.RADIO_GO_PLAY);
            }
        });

        mProgressView = findViewById(R.id.view_progress);
        mRadioPiView = findViewById(R.id.view_radio_pi);

        ApplicationCsts.RadioPiApplicationState startRPState = (ApplicationCsts.RadioPiApplicationState) getIntent().getSerializableExtra(AppConstants.EXTRA_STATE);
        // Test whether the startState is set: Cannot update text from null objects.
        if (startRPState != null) {
            updateRPState(startRPState); // Set text field that represents our initial state.
        } else {
            Log.w(WARN_TAG, "Unable to read arguments from Intent. Cannot set initial state.");
        }
    }

    @Override
    public void onApplicationStateChange(@Nullable ApplicationState newState) {
        Log.d(DEBUG_TAG, "Changing from state _ to _: " + applicationState + newState);

        updateRPState((RadioPiApplicationState) newState); // Set a text field that represents our new state.
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

        sendString(str);
        Log.d(DEBUG_TAG, "Sent file path to server, ready to go for play");
    }

    @Override
    protected void showProgress(boolean show) {
        // Shows the progress UI and hides the RadioPi screen.
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mRadioPiView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * Update UI elements to new state of the RP application.
     * @param newRPState ApplicationState we change to
     */
    private void updateRPState(RadioPiApplicationState newRPState) {
        if (newRPState != null) {
            mStatusView.setText(newRPState.toString());
        }
    }
}
