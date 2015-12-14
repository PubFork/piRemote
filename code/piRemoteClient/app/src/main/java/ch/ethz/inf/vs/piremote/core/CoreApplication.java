package ch.ethz.inf.vs.piremote.core;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import MessageObject.Message;
import StateObject.State;

/**
 * Created by andrina on 06/12/15.
 * The ClientCore should run in the background and be able to update the current activity.
 * Therefore, we define our own Android Application class where we can start our background thread.
 */
public class CoreApplication extends Application {

    // All activities that are components of piRemote extend the AbstractClientActivity.
    @Nullable
    private AbstractClientActivity currentActivity = null; // Reference to the activity that is currently in the foreground.

    private final String VERBOSE_TAG = "# AndroidApp VERBOSE #";

    /**
     * Lets an AbstractClientActivity register itself to give the background thread access to the UI.
     * @param activity to be registered
     */
    protected synchronized void setCurrentActivity(@Nullable AbstractClientActivity activity) {
        Log.v(VERBOSE_TAG, "Set current activity from _ to _: " + currentActivity + activity);
        this.currentActivity = activity;
    }

    /**
     * Lets an AbstractClientActivity unregister itself from receiving UI updates.
     * @param activity to be unregistered, if currently registered
     */
    protected synchronized void resetCurrentActivity(AbstractClientActivity activity) {
        Log.v(VERBOSE_TAG, "Reset current activity from _: " + activity);
        if (currentActivity == activity) {
            this.currentActivity = null;
        }
    }

    synchronized void processMessage(@NonNull Message msg) {
        Log.v(VERBOSE_TAG, "Process message on current activity: " + currentActivity);
        if (currentActivity != null) {
            currentActivity.processMessageFromThread(msg);
        }
    }

    synchronized void startAbstractActivity(@NonNull State state) {
        Log.v(VERBOSE_TAG, "Start abstract activity on current activity: " + currentActivity);
        if (currentActivity != null) {
            currentActivity.startActivityFromThread(state);
        }
    }

    synchronized void updateFilePicker(String[] paths) {
        Log.v(VERBOSE_TAG, "Update file picker on current activity: " + currentActivity);
        if (currentActivity != null) {
            currentActivity.updateFilePickerFromThread(paths);
        }
    }

    synchronized void closeFilePicker() {
        Log.v(VERBOSE_TAG, "Update file picker on current activity: " + currentActivity);
        if (currentActivity != null) {
            currentActivity.closeFilePickerFromThread();
        }
    }

    @Nullable
    AbstractClientActivity getCurrentActivity() {
        return currentActivity;
    }
}
