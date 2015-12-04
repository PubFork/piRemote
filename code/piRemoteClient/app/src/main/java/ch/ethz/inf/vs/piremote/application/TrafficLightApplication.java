package ch.ethz.inf.vs.piremote.application;

import android.util.Log;

import java.io.File;

import SharedConstants.ApplicationCsts.ApplicationState;
import ch.ethz.inf.vs.piremote.core.AbstractClientApplication;

/**
 * Created by andrina on 19/11/15.
 *
 * Application for demonstration purposes.
 */
public class TrafficLightApplication extends AbstractClientApplication {

    public final String AID = "TrafficLightAppl";

    @Override
    public void onApplicationStart(ApplicationState startState) {
        Log.d(AID, "Starting up, going to state "+startState);
    }

    @Override
    public void onApplicationStop() {
        Log.d(AID, "Exiting.");
    }

    @Override
    public void onApplicationStateChange(ApplicationState newState) {
        Log.d(AID, "Changing to state " + newState);
    }

    @Override
    public void onReceiveInt(int i) {

    }

    @Override
    public void onReceiveDouble(double d) {

    }

    @Override
    public void onReceiveString(String str) {

    }
}
