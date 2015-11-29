package MessageObject.PayloadObject;

import SharedConstants.ApplicationCsts.ApplicationState;

import java.io.Serializable;

/**
 * Created by andrina on 28/11/15.
 * Payload for Message from Client to Server.
 */
public class ApplicationStateChange extends Payload implements Serializable {
    public ApplicationState newApplicationState;

    public ApplicationStateChange(ApplicationState newApplicationState) {
        this.newApplicationState = newApplicationState;
    }
}
