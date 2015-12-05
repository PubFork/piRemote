package ch.ethz.inf.vs.piremote.application;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import SharedConstants.ApplicationCsts;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractActivity;

public class TrafficLightActivity extends AbstractActivity {

    // UI references
    private Button mBackButton;
    private Button mPickButton;
    private TextView mPathView;
    ToggleButton mRedButton;
    ToggleButton mOrangeButton;
    ToggleButton mGreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_light);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application.setActivity(this); // TODO: NULL

        mBackButton = (Button) findViewById(R.id.button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                // serverStateChange to NONE ?
            }
        });

        mPickButton = (Button) findViewById(R.id.button_pick);
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntMessage(ApplicationCsts.TL_PICK_FILE);
            }
        });

        // Keep track of the text field to change the output text when a file was picked.
        mPathView = (TextView) findViewById(R.id.picked_path);

        mRedButton = (ToggleButton) findViewById(R.id.button_red);
        mRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntMessage(ApplicationCsts.GO_RED);
            }
        });

        mOrangeButton = (ToggleButton) findViewById(R.id.button_orange);
        mOrangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntMessage(ApplicationCsts.GO_ORANGE);
            }
        });

        mGreenButton = (ToggleButton) findViewById(R.id.button_green);
        mGreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntMessage(ApplicationCsts.GO_GREEN);
            }
        });
    }

    /**
     * Send a constant int message to the server.
     * @param i Message Payload
     */
    private void sendIntMessage(int i) {
        application.sendInt(i);
    }
}
