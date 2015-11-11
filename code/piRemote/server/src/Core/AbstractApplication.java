package Core;

import SharedConstants.ApplicationCsts;

/**
 * Created by sandro on 11.11.15.
 * Parent class for all applications' server part
 */
public abstract class AbstractApplication {

    protected static ApplicationCsts.ApplicationState applicationState;

    public static ApplicationCsts.ApplicationState getApplicationState(){
        return applicationState;
    }

    public abstract void onApplicationStart(ApplicationCsts.ApplicationState initialState);   // Called right after creation of the application
    public abstract void onApplicationStop();                                                 // Called right before the destruction of the application
    public abstract void onApplicationStateChange(ApplicationCsts.ApplicationState newState); // Called when the application shall switch to another state
    public abstract void onFilePicked(String path);                                           // Called when the FilePicker on the client sent a file pick message
    public abstract void onReceiveInt(int i);                                                 // Called when an int    message arrived
    public abstract void onReceiveDouble(double d);                                           // Called when a  double message arrived
    public abstract void onReceiveString(String str);                                         // Called when a  string message arrived
}
