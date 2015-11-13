package Dispatcher;

import Sender.SenderThread;
import java.net.Socket;

/**
 * Created by fabian on 13.11.15.
 */
public class AbstractDispatcherThread extends Thread {
    // or maybe implements Runnable?


    public Socket socket;
    public SenderThread senderThread;
}
