package ch.ethz.inf.vs.piremote.application;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import SharedConstants.ApplicationCsts;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;

public class ImageActivity extends AbstractClientActivity implements ImageFragment.onClickAction {

    private final String DEBUG_TAG = "# ImageApp #";

    private TextView pickedFile;

    private ImageFragment imageFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // pick file button
        Button pickButton = (Button) findViewById(R.id.button_pick);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.IMAGE_PICK_FILE);
            }
        });
        
        // shows picked file
        pickedFile = (TextView) findViewById(R.id.picked_path);

        // spinning wheel
        mProgressView = findViewById(R.id.view_progress);

        // Fragment
        imageFragment = new ImageFragment();
        imageFragment.setArguments(getIntent().getExtras());
    }

    @Override
    protected void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        updateImageState((ApplicationCsts.ImageApplicationState) newState);
    }

    @Override
    protected void onReceiveInt(int i) {
        Log.d(DEBUG_TAG, "Received an int: " + i);
    }

    @Override
    protected void onReceiveDouble(double d) {
        Log.d(DEBUG_TAG, "Received an double: " + d);
    }

    @Override
    protected void onReceiveString(String str) {
        Log.d(DEBUG_TAG, "Received a string: " + str);
        pickedFile.setText(str);
    }

    @Override
    public void onButtonPressed(int state){
        sendInt(state);
    }

    public void updateImageState(ApplicationCsts.ImageApplicationState newState) {
        if (newState != null) {
            switch (newState) {
                case IMAGE_DISPLAYED:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
                    break;
                case IMAGE_NOT_DISPLAYED:
                    getSupportFragmentManager().beginTransaction().remove(imageFragment).commit();
                default:
                    break;
            }
        }
    }
}
