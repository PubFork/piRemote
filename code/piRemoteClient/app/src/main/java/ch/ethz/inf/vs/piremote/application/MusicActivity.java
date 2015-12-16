package ch.ethz.inf.vs.piremote.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.MusicApplicationState;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;
import ch.ethz.inf.vs.piremote.core.AppConstants;

public class MusicActivity extends AbstractClientActivity {
    private TextView mTextViewCurrentSong;
    private TextView mTextViewPathView;
    private TextView mTextViewVolume;
    private TextView mTextViewPlaylist;

    private Button mPickButton;
    private Button mButtonShowPlaylist;

    private ImageButton mImageButtonPlayPause;
    private ImageButton mImageButtonStop;
    private ImageButton mImageButtonNextSong;
    private ImageButton mImageButtonPrevSong;
    private ImageButton mImageButtonVolumeUp;
    private ImageButton mImageButtonVolumeDown;
    private ImageButton mImageButtonLoopToggle;
    private ImageButton mImageButtonShuffle;
    private ImageButton mImageButtonUpdateState;

    private SeekBar mSeekBarVolume;

    private final String INFO_TAG = "# Music #";
    private final String DEBUG_TAG = "# Music DEBUG #";
    private final String ERROR_TAG = "# Music ERROR #";
    private final String WTF_TAG = "# Music WTF #";
    private final String WARN_TAG = "# Music WARN #";
    private final String VERBOSE_TAG = "# Music VERBOSE #";

    // These constants are fixed to the output of mpc
    private final int volumeStart = 7;
    private final int volumeEnd = 10;
    private final int loopStart = 22;
    private final int loopEnd = 25;
    private final int shuffleStart = 36;
    private final int shuffleEnd = 39;
    private final int singleStart = 50;
    private final int singleEnd = 53;
    private final int consumeStart = 65;
    private final int consumeEnd = 68;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "Starting up.");

        setContentView(R.layout.activity_music);

        registerTextViews();
        registerButtons();

        mSeekBarVolume = (SeekBar) findViewById(R.id.seekBar_musicVolume);
        mSeekBarVolume.setMax(100);

        mSeekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        MusicApplicationState initMusicState = (MusicApplicationState) getIntent().getSerializableExtra(AppConstants.EXTRA_STATE);

        // Set the initial state if intent returns not null.
        if (initMusicState != null) {
            updateMusicState(initMusicState);
        } else {
            Log.w(WARN_TAG, "Unable to read arguments from Intent. State not set.");
        }

        mImageButtonUpdateState.performClick();
    }

    /**
     * Overrides super in logging and calling a private function to set the state.
     *
     * @param newState ApplicationState we change to
     */
    @Override
    protected void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        Log.d(DEBUG_TAG, String.format("Changing state from %s to %s: ", applicationState, newState));
        updateMusicState((ApplicationCsts.MusicApplicationState) newState);
    }

    /**
     * Not used.
     *
     * @param i Message Payload
     */
    @Override
    protected void onReceiveInt(int i) {
        Log.d(DEBUG_TAG, "Received an int: " + i);
    }

    /**
     * Not used.
     *
     * @param d Message Payload
     */
    @Override
    protected void onReceiveDouble(double d) {
        Log.d(DEBUG_TAG, "Received a double: " + d);
    }

    /**
     * Method used for setting different text views in the UI. Those are "playlist", "filepicker",
     * "current playing song" and playback modifiers plus volume
     *
     * @param str Message Payload
     */
    @Override
    protected void onReceiveString(String str) {
        Log.d(DEBUG_TAG, "Received a string: " + str);

        if (str.startsWith(ApplicationCsts.MUSIC_PREFIX_SONG)) {
            // Set the current song
            mTextViewCurrentSong.setText(str.substring(ApplicationCsts.MUSIC_PREFIX_SONG.length()));

        } else if (str.startsWith(ApplicationCsts.MUSIC_PREFIX_EXTRA)) {
            // Get the default output sent by mpc
            String playbackSettings = str.substring(ApplicationCsts.MUSIC_PREFIX_EXTRA.length());
            String temp;

            // Set the volume view
            temp = playbackSettings.substring(volumeStart, volumeEnd);
            if (temp.contains(" ")) {
                mTextViewVolume.setText(temp.substring(1) + "%");
            } else if (temp.contains("  ")) {
                mTextViewVolume.setText(temp.substring(2) + "%");
            } else {
                mTextViewVolume.setText(temp + "%");
            }
            //mSeekBarVolume.setProgress(Integer.getInteger(temp));

            // Set the state of the loop button
            if (playbackSettings.substring(loopStart, loopEnd).contains("on")) {
                changeButtonFunction(mImageButtonLoopToggle, R.drawable.ic_repeat_black_48dp, ApplicationCsts.MUSIC_SINGLE);
            } else if (playbackSettings.substring(singleStart, singleEnd).contains("on")) {
                changeButtonFunction(mImageButtonLoopToggle, R.drawable.ic_repeat_one_black_48dp, ApplicationCsts.MUSIC_NOLOOPING);
            } else {
                changeButtonFunction(mImageButtonLoopToggle, R.drawable.ic_not_interested_black_48dp, ApplicationCsts.MUSIC_LOOP);
            }

            // Set the state of the shuffle switch
            temp = playbackSettings.substring(shuffleStart, shuffleEnd);
            Log.wtf(WTF_TAG, temp);
            if (temp.contains("on")) {
                changeButtonFunction(mImageButtonShuffle, R.drawable.ic_shuffle_black_48dp, ApplicationCsts.MUSIC_SHUFFLE_OFF);
            } else {
                changeButtonFunction(mImageButtonShuffle, R.drawable.ic_not_interested_black_48dp, ApplicationCsts.MUSIC_SHUFFLE_ON);
            }
        } else if (str.startsWith(ApplicationCsts.MUSIC_PREFIX_PLAYLIST)) {
            // Fill the scrollable playlist window with the current playlist
            mTextViewPlaylist.setText(str.substring(ApplicationCsts.MUSIC_PREFIX_PLAYLIST.length()));

        } else if (str.startsWith(ApplicationCsts.MUSIC_PREFIX_FILESELECTION)) {
            // Show the currently picked file
            mTextViewPathView.setText(str.substring(ApplicationCsts.MUSIC_PREFIX_FILESELECTION.length()));

        } else if (str.startsWith(ApplicationCsts.MUSIC_PREFIX_STATUS)) {
            String status = str.substring(ApplicationCsts.MUSIC_PREFIX_STATUS.length());
            String[] statusLines = status.split("\n");

            if (statusLines.length > 1) {

                if (statusLines[1].contains("paused")) {
                    mTextViewCurrentSong.setText("Playback paused.");
                } else {
                    mTextViewCurrentSong.setText(statusLines[0]);
                }

                onReceiveString(ApplicationCsts.MUSIC_PREFIX_EXTRA + statusLines[2]);
            } else {
                mTextViewCurrentSong.setText("Playback stopped.");
                onReceiveString(ApplicationCsts.MUSIC_PREFIX_EXTRA + statusLines[0]);
            }

        } else {
            //TODO: Remove from productin app, merely used for general output for anything not specfically assigned.
            mTextViewPlaylist.setText(str);
        }
    }

    /**
     * Overrides parent method. This application doesn't implement multiple views.
     *
     * @param show
     */
    @Override
    protected void showProgress(boolean show) {
        return;
    }

    /**
     * Update the view slightly depending on the current application state.
     *
     * @param newMusicState
     */
    private void updateMusicState(MusicApplicationState newMusicState) {
        if (newMusicState != null) {
            if (newMusicState == MusicApplicationState.MUSIC_PAUSED) {
                mTextViewCurrentSong.setText("Playback paused");
                changeButtonFunction(mImageButtonPlayPause, R.drawable.ic_play_arrow_black_48dp, ApplicationCsts.MUSIC_PLAY);
            } else if (newMusicState == MusicApplicationState.MUSIC_STOPPED) {
                mTextViewCurrentSong.setText("Playback stopped");
                changeButtonFunction(mImageButtonPlayPause, R.drawable.ic_play_arrow_black_48dp, ApplicationCsts.MUSIC_PLAY);
            } else {
                changeButtonFunction(mImageButtonPlayPause, R.drawable.ic_pause_black_48dp, ApplicationCsts.MUSIC_PAUSE);
            }
        }
    }

    /**
     * Internal method setting up all textviews.
     */
    private void registerTextViews() {
        mTextViewCurrentSong = (TextView) findViewById(R.id.textView_musicCurrentSong);
        mTextViewPathView = (TextView) findViewById(R.id.textView_musicFilePicker);
        mTextViewVolume = (TextView) findViewById(R.id.textView_musicCurrentVolume);
        mTextViewPlaylist = (TextView) findViewById(R.id.textView_musicPlaylist);
    }

    /**
     * Internal method setting up all buttons and assigning them a default action.
     */
    private void registerButtons() {
        mPickButton = (Button) findViewById(R.id.button_musicFilePicker);
        mButtonShowPlaylist = (Button) findViewById(R.id.button_musicShowPlaylist);

        mImageButtonPlayPause = (ImageButton) findViewById(R.id.imageButton_musicPlayPause);
        mImageButtonStop = (ImageButton) findViewById(R.id.imageButton_musicStop);
        mImageButtonNextSong = (ImageButton) findViewById(R.id.imageButton_musicNextSong);
        mImageButtonPrevSong = (ImageButton) findViewById(R.id.imageButton_musicPreviousSong);
        mImageButtonVolumeUp = (ImageButton) findViewById(R.id.imageButton_musicVolumeUp);
        mImageButtonVolumeDown = (ImageButton) findViewById(R.id.imageButton_musicVolumeDown);
        mImageButtonLoopToggle = (ImageButton) findViewById(R.id.imageButton_musicLoopToggle);
        mImageButtonShuffle = (ImageButton) findViewById(R.id.imageButton_musicShuffle);
        mImageButtonUpdateState = (ImageButton) findViewById(R.id.imageButton_updateState);

        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_PICK_FILE);
            }
        });

        mButtonShowPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_GET_PLAYLIST);
            }
        });

        mImageButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_PLAY);
            }
        });

        mImageButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_STOP);
            }
        });

        mImageButtonNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_NEXT);
            }
        });

        mImageButtonPrevSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_PREV);
            }
        });

        mImageButtonVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_VOLUME_UP);
            }
        });

        mImageButtonVolumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_VOLUME_DOWN);
            }
        });

        mImageButtonLoopToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_LOOP);
            }
        });

        mImageButtonShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_SHUFFLE_ON);
            }
        });

        mImageButtonUpdateState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(ApplicationCsts.MUSIC_STATUS);
            }
        });
    }


    /**
     * Internal method to quickly change a button's layout and functionality.
     *
     * @param button   Button to change.
     * @param drawable Drawable that should be shown.
     * @param constant New int the button should send upon pressing.
     */
    private void changeButtonFunction(ImageButton button, int drawable, final int constant) {
        button.setOnClickListener(null);
        button.setImageResource(drawable);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "Clicked ImageButton: " + v.toString());
                sendInt(constant);
            }
        });
    }

}