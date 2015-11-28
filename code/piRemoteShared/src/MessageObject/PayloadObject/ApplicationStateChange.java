package MessageObject.PayloadObject;

import SharedConstants.ApplicationCsts.ApplicationState;

/**
 * Created by andrina on 28/11/15.
 * Payload for Message from Client to Server.
 */
public class ApplicationStateChange extends Payload{
    public ApplicationState newApplicationState;

    public ApplicationStateChange(ApplicationState newApplicationState) {
        this.newApplicationState = newApplicationState;
    }
}
