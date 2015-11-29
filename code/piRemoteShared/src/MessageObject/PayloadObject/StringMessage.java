package MessageObject.PayloadObject;

import java.io.Serializable;

/**
 * Created by sandro on 11.11.15.
 * Payload for Message between Server and Client.
 */
public class StringMessage extends Payload implements Serializable {
    public String str;

    public StringMessage(String str) {
        this.str = str;
    }
}
