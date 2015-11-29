package MessageObject.PayloadObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sandro on 11.11.15.
 * Payload for Message from Server to Client.
 */
public class Offer extends Payload implements Serializable {
    public List<String> paths = new LinkedList<>();

    public Offer(List<String> paths) {
        this.paths = paths;
    }
}
