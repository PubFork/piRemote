package ch.ethz.inf.vs.piremote.application;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.VideoApplicationState;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;

public class VideoActivity extends AbstractClientActivity {

    private final String DEBUG_TAG = "# VideoApp #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");

        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO VIDEO APP: Set up UI references
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "ONDESTROY: Exiting.");
    }

    @Override
    protected void onApplicationStateChange(ApplicationState newState) {
        Log.d(DEBUG_TAG, "Changing from state _ to _: " + applicationState + newState);

        // Test whether the newState is set: Cast and also switch/case statement cannot handle null objects.
        if (newState != null) {
            // Do what ever needs to be done to represents our new state.
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
