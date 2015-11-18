package core.network;

import MessageObject.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Fabian on 13.11.15.
 */
public class ServerSenderThread implements Runnable {

    public final static BlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();
    private ServerSocket serverSocket;
    private Thread senderThread;
    private HashMap<UUID, NetworkInfo> sessionTable;
    private ObjectOutputStream outputStream;

    public ServerSenderThread(ServerSocket socket, HashMap sTable){
        serverSocket = socket;
        sessionTable = sTable;


        senderThread = new Thread(this);
        senderThread.start();
    }


    @Override
    public void run() {

        try {
            Message toSend = sendingQueue.take();
            // TODO: how to send on ServerSocket to a specific location/client?
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
