package ch.ethz.inf.vs.piremote.core;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import MessageObject.Message;
import StateObject.State;

/**
 * Created by andrina on 06/12/15.
 * The ClientCore should run in the background and be able to update the current activity.
 * Therefore, we define our own Android Application class where we can start our background thread.
 */
public class CoreApplication extends Application {

    private Thread coreThread;

    // All activities that are components of piRemote extend the AbstractClientActivity.
    @Nullable
    private AbstractClientActivity currentActivity = null; // Reference to the activity that is currently in the foreground.

    private final String DEBUG_TAG = "# AndroidApp #";
    private final String VERBOSE_TAG = "# AndroidApp VERBOSE #";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(DEBUG_TAG, "ONCREATE: Starting up.");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(DEBUG_TAG, "ONTERMINATE: Exiting.");
    }

    public synchronized void setCurrentActivity(AbstractClientActivity activity) {
/*
        if (activity instanceof FilePicker) {
            return; // Don't register the file picker as the activity to be updated.
        }
*/
        Log.v(VERBOSE_TAG, "Set current activity from _ to _: " + currentActivity + activity);
        this.currentActivity = activity;
    }

    public synchronized void resetCurrentActivity(AbstractClientActivity activity) {
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

    synchronized void updateFilePicker(ArrayList<String> paths) {
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
    public AbstractClientActivity getCurrentActivity() {
        return currentActivity;
    }

    public Thread getCoreThread() {
        return coreThread;
    }

    public void setCoreThread(Thread coreThread) {
        this.coreThread = coreThread;
    }
}
