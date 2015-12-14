package ch.ethz.inf.vs.piremote.application;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.RadioPiApplicationState;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ch.ethz.inf.vs.piremote.R;
import ch.ethz.inf.vs.piremote.core.AbstractClientActivity;
import ch.ethz.inf.vs.piremote.core.AppConstants;

/**
 * Created by FR4NK-W on 12/12/15.
 */
public class RadioPiActivity extends AbstractClientActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    }

    @Override
    public void onApplicationStateChange(@Nullable ApplicationState newState) {

    }

    @Override
    public void onReceiveInt(int i) {
    }

    @Override
    public void onReceiveString(String str) {
    }

    @Override
    protected void showProgress(boolean show) {
    }

    /**
     * Update UI elements to new state of the RP application.
     * @param newRPState ApplicationState we change to
     */
    private void updateTLState(RadioPiApplicationState newRPState) {
    }
}
