package ch.ethz.inf.vs.piremote.application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import SharedConstants.ApplicationCsts;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;

public class ImageActivity extends AbstractClientActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
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

    @Override
    protected void showProgress(boolean show) {

    }
}
