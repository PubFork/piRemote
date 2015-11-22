package core.network;

import MessageObject.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
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

    private final static BlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<>();
    private Thread senderThread;
    private HashMap<UUID, NetworkInfo> sessionTable;
    private ObjectOutputStream outputStream;

    public ServerSenderThread(HashMap sTable){
        sessionTable = sTable;

        senderThread = new Thread(this);
        senderThread.start();
    }


    @Override
    public void run() {

        Socket senderSocket = new Socket(); // unconnected Socket
        while (ServerNetwork.isRunning()) {
            try {
                Message toSend = sendingQueue.take();

                NetworkInfo networkInfo = sessionTable.get(toSend.getUuid());
                InetSocketAddress destination = new InetSocketAddress(networkInfo.ip, networkInfo.port);
                senderSocket.connect(destination);
                outputStream = new ObjectOutputStream(senderSocket.getOutputStream());
                outputStream.writeObject(toSend);
                outputStream.flush();
            } catch ( IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static BlockingQueue<Message> getSendingQueue() {
        return sendingQueue;
    }
}
