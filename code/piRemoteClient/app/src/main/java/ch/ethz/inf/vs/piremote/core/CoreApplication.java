package ch.ethz.inf.vs.piremote.core;

import android.app.Application;
import android.util.Log;

import java.util.List;

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
    private AbstractClientActivity currentActivity = null; // Reference to the activity that is currently in the foreground.

    private final String DEBUG_TAG = "# AndroidApp #";
    private final String ERROR_TAG = "# AndroidApp ERROR #";
    private final String VERBOSE_TAG = "# AndroidApp VERBOSE #";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(DEBUG_TAG, "Starting up.");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(DEBUG_TAG, "Exiting.");
    }

    public synchronized void setActivity(AbstractClientActivity activity) {
        this.currentActivity = activity;
    }

    protected synchronized void processMessage(Message msg) {
        if (currentActivity != null) {
            currentActivity.processMessageFromThread(msg);
        }
    }

    protected synchronized void startAbstractActivity(State state) {
        if (currentActivity != null) {
            currentActivity.startActivityFromThread(state);
        }
    }

    protected synchronized void updateFilePicker(List<String> paths) {
        if (currentActivity != null) {
            currentActivity.updateFilePickerFromThread(paths);
        }
    }

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
