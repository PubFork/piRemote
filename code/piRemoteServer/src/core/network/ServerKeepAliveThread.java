package core.network;

import java.lang.Thread;
import java.util.HashMap;

/**
 * created by fabian on 13.11.15
 */

public class ServerKeepAliveThread implements Runnable {

    private ServerDispatcherThread dispatcherThread;
    private HashMap sessionTable;
    private Thread keepAliveThread;

    public ServerKeepAliveThread(ServerDispatcherThread dispatcherThread, HashMap sessionTable) {
        this.dispatcherThread = dispatcherThread;
        this.sessionTable = sessionTable;

        keepAliveThread = new Thread(this);
        keepAliveThread.start();
    }

    @Override
    public void run() {

    }
}