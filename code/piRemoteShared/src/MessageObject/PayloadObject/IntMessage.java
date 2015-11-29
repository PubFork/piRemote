package MessageObject.PayloadObject;

import java.io.Serializable;

/**
 * Created by sandro on 11.11.15.
 * Payload for Message between Server and Client.
 */
public class IntMessage extends Payload implements Serializable {
    public int i;

    public IntMessage(int i) {
        this.i = i;
    }
}
