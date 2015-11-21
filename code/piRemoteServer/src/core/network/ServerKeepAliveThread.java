package core.network;

import MessageObject.Message;
import core.ServerCore;

import java.lang.Thread;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * created by fabian on 13.11.15
 */

public class ServerKeepAliveThread implements Runnable {

    private ServerDispatcherThread dispatcherThread;
    private HashMap<UUID,NetworkInfo> sessionTable;
    private Thread keepAliveThread;

    private long INTERVAL = 1000;

    public ServerKeepAliveThread(ServerDispatcherThread dispatcherThread, HashMap sessionTable) {
        this.dispatcherThread = dispatcherThread;
        this.sessionTable = sessionTable;

        keepAliveThread = new Thread(this);
        keepAliveThread.start();
    }

    @Override
    public void run() {
        while (ServerNetwork.isRunning()) {
            try {
                wait(INTERVAL);
                for (Map.Entry<UUID, NetworkInfo> entry : sessionTable.entrySet()) {
                    ServerSenderThread.getSendingQueue().add(new Message(entry.getKey(), ServerCore.getState().getServerState(), ServerCore.getState().getApplicationState()));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}