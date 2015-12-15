package ch.ethz.inf.vs.piremote.application;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import SharedConstants.ApplicationCsts;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;

public class ImageActivity extends AbstractClientActivity {

    private final String DEBUG_TAG = "# ImageApp #";

    private TextView pickedFile;
    private View progressView;
    private View imageView;
    private Button prevButton, nextButton;

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

        prevButton = (Button) findViewById(R.id.button_prev_image);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInt(ApplicationCsts.IMAGE_PREV);
            }
        });
        nextButton = (Button) findViewById(R.id.button_next_image);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInt(ApplicationCsts.IMAGE_NEXT);
            }
        });
        
        // shows picked file
        pickedFile = (TextView) findViewById(R.id.picked_path);

        // spinning wheel
        progressView = findViewById(R.id.view_progress);
        imageView = findViewById(R.id.view_image);
    }

    @Override
    protected void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {

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
    protected void showProgress(boolean show) {
        /*progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        imageView.setVisibility(show ? View.GONE : View.VISIBLE);*/
    }
}
