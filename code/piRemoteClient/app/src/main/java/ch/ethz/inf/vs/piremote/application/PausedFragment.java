package ch.ethz.inf.vs.piremote.application;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import SharedConstants.ApplicationCsts;
import ch.ethz.inf.vs.piremote.R;

/**
 * This fragment is used to control a paused video. It provides the same functionality as the PlayFragment. The only significant difference is
 * that there is a play button instead of a pause button
 */
public class PausedFragment extends Fragment {

    private final String DEBUG_TAG = "# VideoApp #";
    private onClickAction mCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //This is implemented by the video activity to enable communication between fragment and activity
    public interface onClickAction {
        void onButtonPressed(int state);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mCallback = (onClickAction) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement onClickAction");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paused, container, false);

        //All buttons get a listener
        ImageButton mPause = (ImageButton) view.findViewById(R.id.button_play_pause);
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_PLAY);
            }
        });

        ImageButton mStop = (ImageButton) view.findViewById(R.id.button_stop);
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_STOP);
            }
        });

        ImageButton mBack = (ImageButton) view.findViewById(R.id.button_previous);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_JUMP_BACK);
            }
        });
        ImageButton mForward = (ImageButton) view.findViewById(R.id.button_next);
        mForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_JUMP_FORWARD);
            }
        });

        ImageButton mLeapBack = (ImageButton) view.findViewById(R.id.button_leapback);
        mLeapBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_LEAP_BACK);
            }
        });
        ImageButton mLeapForward = (ImageButton) view.findViewById(R.id.button_leapforward);
        mLeapForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_LEAP_FORWARD);
            }
        });

        ImageButton mFaster = (ImageButton) view.findViewById(R.id.button_faster);
        mFaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_SPEED_FASTER);
            }
        });

        ImageButton mSlower = (ImageButton) view.findViewById(R.id.button_slower);
        mSlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_SPEED_SLOWER);
            }
        });

        ImageButton mLouder = (ImageButton) view.findViewById(R.id.button_louder);
        mLouder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_VOLUME_INCREASE);
            }
        });

        ImageButton mSofter = (ImageButton) view.findViewById(R.id.button_softer);
        mSofter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_VOLUME_DECREASE);
            }
        });

        ImageButton mTogglesubs = (ImageButton) view.findViewById(R.id.button_tobblesubs);
        mTogglesubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                mCallback.onButtonPressed(ApplicationCsts.VIDEO_TOGGLE_SUBTITLES);
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

