package ch.ethz.inf.vs.piremote.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.VideoApplicationState;
import SharedConstants.CoreCsts;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;
import ch.ethz.inf.vs.piremote.core.AppConstants;

/**
 * Activity for the video application on the client.
 */
public class VideoActivity extends AbstractClientActivity {

    // UI references
    private Button mBackButton;
    private Button mPickButton;
    private TextView mPathView;

    private final String DEBUG_TAG = "# VideoApp #";
    private final String WARN_TAG = "# VideoApp WARN #";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");

        //setContentView(R.layout.activity_video);
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
                sendInt(ApplicationCsts.VIDEO_PICK_FILE);
            }
        });

        // Keep track of the text field to change the output text when a file was picked.
        mPathView = (TextView) findViewById(R.id.picked_path);
        // TODO VIDEO APP: Set up UI references

        ApplicationState startState = (ApplicationState) getIntent().getSerializableExtra(AppConstants.EXTRA_STATE);
        // Test whether the startState is set: Cast and also switch/case statement cannot handle null objects.
        if (startState != null) {
            // Set initial start state.
            VideoApplicationState newVideoState = (VideoApplicationState) startState;
            switch (newVideoState) {
                case VIDEO_PAUSED:
                    // TODO VIDEO APP
                    break;
                case VIDEO_PLAYING:
                    // TODO VIDEO APP
                    break;
                case VIDEO_STOPPED:
                    // TODO VIDEO APP
                    break;
                default:
                    break;
            }
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
    protected void onApplicationStateChange(ApplicationState newState) {
        Log.d(DEBUG_TAG, "Changing from state _ to _: " + applicationState + newState);

        // Test whether the applicationState is set: Cast and also switch/case statement cannot handle null objects.
        if (applicationState != null) {
            // Reset from old state.
            VideoApplicationState oldVideoState = (VideoApplicationState) applicationState;
            switch (oldVideoState) {
                case VIDEO_PAUSED:
                    // TODO VIDEO APP
                    break;
                case VIDEO_PLAYING:
                    // TODO VIDEO APP
                    break;
                case VIDEO_STOPPED:
                    // TODO VIDEO APP
                    break;
                default:
                    break;
            }
        }

        // Test whether the newState is set: Cast and also switch/case statement cannot handle null objects.
        if (newState != null) {
            // Change to new state.
            VideoApplicationState newVideoState = (VideoApplicationState) newState;
            switch (newVideoState) {
                case VIDEO_PAUSED:
                    // TODO VIDEO APP
                    break;
                case VIDEO_PLAYING:
                    // TODO VIDEO APP
                    break;
                case VIDEO_STOPPED:
                    // TODO VIDEO APP
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onReceiveInt(int i) {
        Log.d(DEBUG_TAG, "Received an int: " + i);
    }

    @Override
    protected void onReceiveDouble(double d) {
        Log.d(DEBUG_TAG, "Received a double: " + d);
    }

    @Override
    protected void onReceiveString(String str) {
        Log.d(DEBUG_TAG, "Received a string: " + str);
    }
}
