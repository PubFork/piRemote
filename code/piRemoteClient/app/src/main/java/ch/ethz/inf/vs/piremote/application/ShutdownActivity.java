package ch.ethz.inf.vs.piremote.application;

import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import SharedConstants.ApplicationCsts;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;

public class ShutdownActivity extends AbstractClientActivity {

    private final String DEBUG_TAG = "# ShutdownApp #";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutdown);

        Button mShutdownButton = (Button) findViewById(R.id.button_shutdown);
        mShutdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                Log.d(DEBUG_TAG, "Clicked button: " + v.toString());
                sendInt(ApplicationCsts.SHUTODNW_BUTTON_PRESSED);
            }
        });
    }

    @Override
    protected void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {

    }

    @Override
    protected void onReceiveInt(int i) {

    }

    @Override
    protected void onReceiveDouble(double d) {

    }

    @Override
    protected void onReceiveString(String str) {

    }
}
