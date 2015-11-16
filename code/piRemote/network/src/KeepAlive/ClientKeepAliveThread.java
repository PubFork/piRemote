package KeepAlive;

import Sender.ClientSenderThread;

import java.lang.Thread;

/**
 * created by fabian on 13.11.15
 */

public class ClientKeepAliveThread implements Runnable {

    // private ClientCore clientCore needed for mainQueue
    private ClientSenderThread senderThread;

    public ClientKeepAliveThread(ClientSenderThread sender) {
        senderThread = sender;
    }

    @Override
    public void run() {

    }
}