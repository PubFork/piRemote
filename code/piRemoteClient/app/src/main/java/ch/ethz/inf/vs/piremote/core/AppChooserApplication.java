package ch.ethz.inf.vs.piremote.core;

import android.content.Intent;

import java.io.File;

import SharedConstants.ApplicationCsts.ApplicationState;

/**
 * Created by andrina on 28/11/15.
 *
 * The Application Chooser is also represented by an application.
 */
public class AppChooserApplication extends AbstractClientApplication {

    @Override
    public void onApplicationStart(ApplicationState startState) {

        // Create Intent to adapt UI for the new application.
        Intent startApplication = new Intent(activity.getBaseContext(), AppChooserActivity.class);
        activity.startActivity(startApplication);
    }

    @Override
    public void onApplicationStop() {

    }

    @Override
    public void onApplicationStateChange(ApplicationState newState) {

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
