package ch.ethz.inf.vs.piremote.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

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

    private TextView mTextViewCurrentSong;
    private TextView mTextViewPathView;
    private TextView mTextViewVolume;
    private View mMusicView;

    private Button mPickButton;
    private Button mButtonPlay;
    private Button mButtonPause;
    private Button mButtonStop;
    private Button mButtonNextSong;
    private Button mButtonPrevSong;
    private Button mButtonVolumeUp;
    private Button mButtonVolumeDown;
    private Button mButtonShowPlaylist;

    private Switch mSwitchLoop;
    private Switch mSwitchSingleLoop;
    private Switch mSwitchShuffle;
    private Switch mSwitchConsume;

    private final String INFO_TAG = "# Music #";
    private final String DEBUG_TAG = "# Music DEBUG #";
    private final String ERROR_TAG = "# Music ERROR #";
    private final String WTF_TAG = "# Music WTF #";
    private final String WARN_TAG = "# Music WARN #";
    private final String VERBOSE_TAG = "# Music VERBOSE #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "Starting up.");

        setContentView(R.layout.activity_music);

        registerTextViews();
        registerButtons();
        registerSwitches();

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
        Log.d(DEBUG_TAG, String.format("Changing state from %s to %s: ", applicationState, newState));
        updateMusicState((ApplicationCsts.MusicApplicationState) newState);
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
        if (str.startsWith(ApplicationCsts.MUSIC_PREFIX_SONG)) {
            mTextViewCurrentSong.setText(str.substring(ApplicationCsts.MUSIC_PREFIX_SONG.length()));
        } else if (str.startsWith(ApplicationCsts.MUSIC_PREFIX_EXTRA)) {
            String playbackSettings = str.substring(ApplicationCsts.MUSIC_PREFIX_EXTRA.length());
            // Get volume
            // Get loop
            // Get shuffle
            // Get single
            // Get consume
        } else {
            // Filepicker needed?
        }
    }

    @Override
    protected void showProgress(boolean show) {
        return;
    }

    private void updateMusicState(MusicApplicationState newMusicState) {
        if (newMusicState != null) {
            if (newMusicState == MusicApplicationState.MUSIC_PAUSED) {
                mTextViewCurrentSong.setText("Playback paused");
            } else if (newMusicState == MusicApplicationState.MUSIC_STOPPED) {
                mTextViewCurrentSong.setText("Playback stopped");
            }
        }
    }

    private void registerTextViews() {
        mTextViewCurrentSong = (TextView) findViewById(R.id.textView_musicCurrentSong);
        mTextViewPathView = (TextView) findViewById(R.id.textView_musicFilePicker);
        mTextViewVolume = (TextView) findViewById(R.id.textView_musicCurrentVolume);
    }

    private void registerButtons() {
        mPickButton = (Button) findViewById(R.id.button_musicFilePicker);
        mButtonPlay = (Button) findViewById(R.id.button_musicPlay);
        mButtonPause = (Button) findViewById(R.id.button_musicPause);
        mButtonStop = (Button) findViewById(R.id.button_musicStop);
        mButtonNextSong = (Button) findViewById(R.id.button_musicNextSong);
        mButtonPrevSong = (Button) findViewById(R.id.button_musicPreviousSong);
        mButtonVolumeUp = (Button) findViewById(R.id.button_musicVolumeUp);
        mButtonVolumeDown = (Button) findViewById(R.id.button_musicVolumeDown);
        mButtonShowPlaylist = (Button) findViewById(R.id.button_musicShowPlaylist);

        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.TRAFFIC_PICK_FILE);
            }
        });

        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_PLAY);
            }
        });

        mButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_PAUSE);
            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_STOP);
            }
        });

        mButtonNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_NEXT);
            }
        });

        mButtonPrevSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_PREV);
            }
        });

        mButtonVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_VOLUME_UP);
            }
        });

        mButtonVolumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_VOLUME_DOWN);
            }
        });

        mButtonShowPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_GET_PLAYLIST);
            }
        });
    }

    private void registerSwitches() {
        mSwitchLoop = (Switch) findViewById(R.id.switch_musicLoop);
        mSwitchSingleLoop = (Switch) findViewById(R.id.switch_musicSingleLoop);
        mSwitchShuffle = (Switch) findViewById(R.id.switch_musicShuffle);
        mSwitchConsume = (Switch) findViewById(R.id.switch_musicConsume);

        mSwitchLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_LOOP);
            }
        });

        mSwitchSingleLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_SINGLE);
            }
        });

        mSwitchShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_SHUFFLE);
            }
        });

        mSwitchConsume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_CONSUME);
            }
        });
    }
}