package core.network;

import java.lang.Thread;

/**
 * created by fabian on 13.11.15
 */

public class ServerKeepAliveThread implements Runnable {

    private ServerDispatcherThread dispatcherThread;
    private Thread keepAliveThread;

    public ServerKeepAliveThread(ServerDispatcherThread dispatcherThread) {
        this.dispatcherThread = dispatcherThread;

        keepAliveThread = new Thread(this);
        keepAliveThread.start();
    }

    @Override
    public void run() {

    }
}