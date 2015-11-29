package MessageObject.PayloadObject;

import java.io.Serializable;

/**
 * Created by sandro on 11.11.15.
 * Payload for Message between Server and Client.
 */
public class DoubleMessage extends Payload implements Serializable {
    public double d;

    public DoubleMessage() {}

    public DoubleMessage(double d) {
        this.d = d;
    }
}
