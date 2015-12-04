package ch.ethz.inf.vs.piremote.application;

import android.content.Intent;
import android.util.Log;

import SharedConstants.ApplicationCsts.TrafficLightApplicationState;
import SharedConstants.ApplicationCsts.ApplicationState;
import ch.ethz.inf.vs.piremote.core.AbstractClientApplication;

/**
 * Created by andrina on 19/11/15.
 *
 * Application for demonstration purposes.
 */
public class TrafficLightApplication extends AbstractClientApplication {

    public final String AID = "TrafficLightApp";

    /**
     * If the application is already running on the server, we need to adapt to the dictated state.
     * @param startState initial ApplicationState
     */
    @Override
    public void onApplicationStart(ApplicationState startState) {
        Log.d(AID, "Starting up, going to state " + startState);

        // Create Intent to adapt UI for the new application.
        Intent startApplication = new Intent(activity.getBaseContext(), TrafficLightActivity.class);
        activity.startActivity(startApplication); // sets activity to current

        // Toggle the button that represents our state.
        TrafficLightApplicationState startTLState = (TrafficLightApplicationState) startState;
        switch (startTLState) {
            case RED:
                ((TrafficLightActivity) activity).mRedButton.setChecked(true);
                break;
            case ORANGE:
                ((TrafficLightActivity) activity).mOrangeButton.setChecked(true);
                break;
            case GREEN:
                ((TrafficLightActivity) activity).mGreenButton.setChecked(true);
                break;
        }
    }

    @Override
    public void onApplicationStop() {
        Log.d(AID, "Exiting.");
    }

    @Override
    public void onApplicationStateChange(ApplicationState newState) {
        Log.d(AID, "Changing to state " + newState);

        // Untoggle the button that represents our old state.
        TrafficLightApplicationState oldTLState = (TrafficLightApplicationState) applicationState;
        switch (oldTLState) {
            case RED:
                ((TrafficLightActivity) activity).mRedButton.setChecked(false);
                break;
            case ORANGE:
                ((TrafficLightActivity) activity).mOrangeButton.setChecked(false);
                break;
            case GREEN:
                ((TrafficLightActivity) activity).mGreenButton.setChecked(false);
                break;
        }

        // Toggle the button that represents our new state.
        TrafficLightApplicationState newTLState = (TrafficLightApplicationState) newState;
        switch (newTLState) {
            case RED:
                ((TrafficLightActivity) activity).mRedButton.setChecked(true);
                break;
            case ORANGE:
                ((TrafficLightActivity) activity).mOrangeButton.setChecked(true);
                break;
            case GREEN:
                ((TrafficLightActivity) activity).mGreenButton.setChecked(true);
                break;
        }
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
