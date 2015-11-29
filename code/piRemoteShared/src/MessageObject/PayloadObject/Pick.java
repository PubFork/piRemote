package MessageObject.PayloadObject;

import java.io.Serializable;

/**
 * Created by sandro on 11.11.15.
 * Payload for Message from Client to Server.
 */
public class Pick extends Payload implements Serializable {
    public String path;

    public Pick(String path) {
        this.path = path;
    }
}
