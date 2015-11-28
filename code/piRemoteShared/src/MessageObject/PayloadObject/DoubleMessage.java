package MessageObject.PayloadObject;

/**
 * Created by sandro on 11.11.15.
 * Payload for Message between Server and Client.
 */
public class DoubleMessage extends Payload{
    public double d;

    public DoubleMessage(double d) {
        this.d = d;
    }
}
