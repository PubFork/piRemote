package core.network;

import java.lang.Thread;

/**
 * created by fabian on 13.11.15
 */

public class ServerKeepAliveThread implements Runnable {

    private ServerDispatcherThread dispatcherThread;

    public ServerKeepAliveThread(ServerDispatcherThread dispatcherThread) {
        this.dispatcherThread = dispatcherThread;
    }

    @Override
    public void run() {

    }
}