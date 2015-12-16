package ch.ethz.inf.vs.piremote.application;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import SharedConstants.ApplicationCsts;
import ch.ethz.inf.vs.piremote.R;

/**
 * Created by Fabian on 16/12/15.
 */
public class ImageFragment extends Fragment {

    private onClickAction mCallback;
    private Button prevButton, nextButton, pickButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        View view = inflater.inflate(R.layout.image_only_pick, container, false);

        prevButton = (Button) view.findViewById(R.id.button_prev_image);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onButtonPressed(ApplicationCsts.IMAGE_PREV);
            }
        });

        nextButton = (Button) view.findViewById(R.id.button_next_image);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onButtonPressed(ApplicationCsts.IMAGE_NEXT);
            }
        });

        pickButton = (Button) view.findViewById(R.id.button_pick);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onButtonPressed(ApplicationCsts.IMAGE_PICK_FILE);
            }
        });

        return view;
    }
}
