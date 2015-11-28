package MessageObject.PayloadObject;

/**
 * Created by sandro on 11.11.15.
 * Payload for Message between Server and Client.
 */
public class StringMessage extends Payload {
    public String str;

    public StringMessage(String str) {
        this.str = str;
    }
}
