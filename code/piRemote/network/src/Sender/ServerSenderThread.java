package Sender;

import MessageObject.Message;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Fabian on 13.11.15.
 */
public class ServerSenderThread implements Runnable {

    public final static BlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();
    private ServerSocket socket;
    private Thread senderThread;
    private HashMap<UUID, Object> sessionTable;

    public ServerSenderThread(ServerSocket socket, HashMap sTable){
        this.socket = socket;
        senderThread = new Thread(this);
        sessionTable = sTable;


        senderThread.start();
    }


    @Override
    public void run() {

    }
}
