package ch.ethz.inf.vs.piremote.core;

import java.io.File;
import java.util.List;
import java.util.UUID;

import MessageObject.Message;
import SharedConstants.ApplicationCsts;

/**
 * Created by andrina on 19/11/15.
 *
 * This abstract client application provides a way to access all applications on the client part in a uniform manner.
 */
public abstract class AbstractApplication {

    protected static ApplicationCsts.ApplicationState applicationState;

    public ApplicationCsts.ApplicationState getApplicationState() {
        return applicationState;
    }

    public void processMessage(Message msg) {
    }

    protected boolean checkApplicationState(Message msg) {
        if (applicationState == null || msg.getApplicationState() == null) {
            return false;
        }
        return msg.getApplicationState().equals(applicationState);
    }

    protected void changeApplicationState(ApplicationCsts.ApplicationState newState) {
    }

    protected void pickFile(List<String> paths) {
    }

    protected void closeFilePicker() {
    }

    public abstract void onApplicationStart();                                                // Called right after creation of the application
    public abstract void onApplicationStop();                                                 // Called right before the destruction of the application
    public abstract void onApplicationStateChange(ApplicationCsts.ApplicationState newState); // Called right BEFORE application switches to another state
    public abstract void onFilePicked(File file);                                             // Called when the FilePicker on the server sent a file pick close
    public abstract void onReceiveInt(int i);                                                 // Called when an int    message arrived
    public abstract void onReceiveDouble(double d);                                           // Called when a  double message arrived
    public abstract void onReceiveString(String str);                                         // Called when a  string message arrived
}
