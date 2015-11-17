package ch.ethz.inf.vs.piremote.core.network;

import ch.ethz.inf.vs.piremote.core.network.ClientSenderThread;

import java.lang.Thread;

/**
 * created by fabian on 13.11.15
 */

public class ClientKeepAliveThread implements Runnable {

    // private ClientCore clientCore needed for mainQueue
    private ClientSenderThread senderThread;
    private Thread keepAliveThread;

    public ClientKeepAliveThread(ClientSenderThread sender) {
        senderThread = sender;

        keepAliveThread = new Thread(this);
        keepAliveThread.start();
    }

    @Override
    public void run() {

    }
}