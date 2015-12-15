package ch.ethz.inf.vs.piremote.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.MusicApplicationState;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;
import ch.ethz.inf.vs.piremote.core.AppConstants;

public class MusicActivity extends AbstractClientActivity {

    /*
    Temporary dump of serverside commands:
    Get Volume: amixer get PCM | tail -n1 | awk -F " " '{print $4}'
    Set Volume absolute: amixer set PCM X%
    Set Volume relative: amixer set PCM YdB+-

    Get current song: mpc current

    Get different fields for sliders: mpc | tail -n1 | awk -F " " '{print $X}'
    Where X = 4 (loop), X = 6 (Shuffle), X = 8 (Single), X = 10 (Consume)

    Get all infos in one command:
    mpc | tail -n1 | awk -F " " '{s="";for (i=2;i<=NF;i+=2) {s=s?s FS $i:$i} print s}'
     */


    private TextView mCurrentSong;
    private TextView mPathView;
    private TextView mVolume;
    private View mProgressView;
    private View mMusicView;

    private final String INFO_TAG = "# Music #";
    private final String DEBUG_TAG = "# Music DEBUG #";
    private final String ERROR_TAG = "# Music ERROR #";
    private final String WTF_TAG = "# Music WTF #";
    private final String WARN_TAG = "# Music WARN #";
    private final String VERBOSE_TAG = "# Music VERBOSE #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");


        setContentView(R.layout.activity_music);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Button mPickButton = (Button) findViewById(R.id.button_musicFilePicker);
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.TRAFFIC_PICK_FILE);
            }
        });

        // Keep track of the text field to change the output text when a file was picked.
        mPathView = (TextView) findViewById(R.id.textView_musicFilePicker);
        mCurrentSong = (TextView) findViewById(R.id.textView_musicCurrentSong);
        mVolume = (TextView) findViewById(R.id.textView_musicCurrentVolume);

        Button mButtonPlay = (Button) findViewById(R.id.button_musicPlay);
        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_PLAY);
            }
        });

        Button mButtonPause = (Button) findViewById(R.id.button_musicPause);
        mButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_PAUSE);
            }
        });

        Button mButtonStop = (Button) findViewById(R.id.button_musicStop);
        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_STOP);
            }
        });

        Button mButtonNextSong = (Button) findViewById(R.id.button_musicNextSong);
        mButtonNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_NEXT);
            }
        });

        Button mButtonPrevSong = (Button) findViewById(R.id.button_musicPreviousSong);
        mButtonPrevSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_PREV);
            }
        });

        Button mButtonVolumeUp = (Button) findViewById(R.id.button_musicVolumeUp);
        mButtonVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_VOLUME_UP);
            }
        });

        Button mButtonVolumeDown = (Button) findViewById(R.id.button_musicVolumeDown);
        mButtonVolumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_VOLUME_DOWN);
            }
        });

        Button mButtonShowPlaylist = (Button) findViewById(R.id.button_musicShowPlaylist);
        mButtonShowPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_GET_PLAYLIST);
            }
        });

        Switch mSwitchLoop = (Switch) findViewById(R.id.switch_musicLoop);
        mSwitchLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_LOOP);
            }
        });

        Switch mSwitchSingleLoop = (Switch) findViewById(R.id.switch_musicSingleLoop);
        mSwitchSingleLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_SINGLE);
            }
        });

        Switch mSwitchShuffle = (Switch) findViewById(R.id.switch_musicShuffle);
        mSwitchShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_SHUFFLE);
            }
        });

        Switch mSwitchConsume = (Switch) findViewById(R.id.switch_musicConsume);
        mSwitchConsume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_CONSUME);
            }
        });

        mProgressView = findViewById(R.id.music_viewProgress);
        mMusicView = findViewById(R.id.music_ViewControls);

        MusicApplicationState initMusicState = (MusicApplicationState) getIntent().getSerializableExtra(AppConstants.EXTRA_STATE);

        // Set the initial state if intent returns not null.
        if (initMusicState != null) {
            updateMusicState(initMusicState);
        } else {
            Log.w(WARN_TAG, "Unable to read arguments from Intent. State not set.");
        }
    }

    @Override
    protected void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        Log.d(DEBUG_TAG, String.format("Changing from state _%s to %s: ", applicationState, newState));

        updateMusicState((ApplicationCsts.MusicApplicationState) newState); // Set a text field that represents our new state.

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

        // TODO: Set to different textViews based on pseudo-header received.
        // We can receive a song-update
        // We can receive a status-update of the server
        // We can receive an update of mpd playback setttins (like volume, loop, ...)
        // Filepicker also sending here?
    }

    @Override
    protected void showProgress(boolean show) {
        // Shows the progress UI and hides the traffic light screen.
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mMusicView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateMusicState(ApplicationCsts.MusicApplicationState newMusicState) {
        if (newMusicState != null) {
            //mStatusView.setText(newMusicState);
        }
    }
}