package ch.ethz.inf.vs.piremote.application;

import android.support.v4.app.Fragment;
import android.app.FragmentManager;
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
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;
import ch.ethz.inf.vs.piremote.core.AppConstants;
import ch.ethz.inf.vs.piremote.application.StoppedFragment;
import ch.ethz.inf.vs.piremote.application.PlayFragment;


/**
 * Activity for the video application on the client.
 */
public class VideoActivity extends AbstractClientActivity implements PlayFragment.onClickAction,PausedFragment.onClickAction{

    // UI references
    private TextView mPathView;
    private View mProgressView;
    private View mVideoView;
    private StoppedFragment mStoppedFragment;
    private PlayFragment mPlayFragment;
    private PausedFragment mPausedFragment;

    private final String DEBUG_TAG = "# VideoApp #";
    private final String WARN_TAG = "# VideoApp WARN #";

    @Override
    public void onButtonPressed(int state){
        sendInt(state);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");

        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button mPickButton = (Button) findViewById(R.id.button_pick);
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

        mProgressView = findViewById(R.id.view_progress);
        mVideoView = findViewById(R.id.view_video);

        mStoppedFragment = new StoppedFragment();
        mStoppedFragment.setArguments(getIntent().getExtras());

        mPlayFragment = new PlayFragment();
        mPlayFragment.setArguments(getIntent().getExtras());

        mPausedFragment = new PausedFragment();
        mPausedFragment.setArguments(getIntent().getExtras());

        VideoApplicationState startVideoState = (VideoApplicationState) getIntent().getSerializableExtra(AppConstants.EXTRA_STATE);
        // Test whether the startState is set: Nothing to do on null objects.
        if (startVideoState != null) {
            updateVideoState(null, startVideoState); // Set initial start state.
        } else {
            Log.w(WARN_TAG, "Unable to read arguments from Intent. Cannot set initial state.");
        }
    }

    @Override
    protected void onApplicationStateChange(ApplicationState newState) {
        Log.d(DEBUG_TAG, "Changing from state _ to _: " + applicationState + newState);

        updateVideoState((VideoApplicationState) applicationState, (VideoApplicationState) newState);
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

    @Override
    protected void showProgress(boolean show) {
        // Shows the progress UI and hides the video screen.
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mVideoView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * Update UI elements to new state of the video application.
     * @param newVideoState ApplicationState we change to
     */
    private void updateVideoState(VideoApplicationState oldVideoState, VideoApplicationState newVideoState) {
        // Test whether the oldState is set: Switch/case statement cannot handle null objects.
        if (oldVideoState != null) {
            switch (oldVideoState) { // Reset from old state.
                case VIDEO_PAUSED:
                    // TODO VIDEO APP
                    break;
                case VIDEO_PLAYING:
                    // TODO VIDEO APP
                    break;
                case VIDEO_STOPPED:
                    //TODO video app
                    break;
                default:
                    break;
            }
        }

        // Test whether the newState is set: Switch/case statement cannot handle null objects.
        if (newVideoState != null) {
            switch (newVideoState) { // Change to new state.
                case VIDEO_PAUSED:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPausedFragment).commit();
                    break;
                case VIDEO_PLAYING:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPlayFragment).commit();
                    break;
                case VIDEO_STOPPED:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mStoppedFragment).commit();
                    break;
                default:
                    break;
            }
        }
    }
}
