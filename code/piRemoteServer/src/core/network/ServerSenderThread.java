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
    private HashMap<UUID, NetworkInfo> sessionTable;
    private ObjectOutputStream outputStream;

    private final Thread senderThread;

    public ServerSenderThread(HashMap sTable){
        sessionTable = sTable;

        this.senderThread = new Thread(this);
        // senderThread.start();
    }


    @Override
    public void run() {

        Socket senderSocket = new Socket(); // unconnected Socket
        while (ServerNetwork.isRunning()) {
            try {
                Message toSend = sendingQueue.take();

                NetworkInfo networkInfo = sessionTable.get(toSend.getUuid());
                InetSocketAddress destination = new InetSocketAddress(networkInfo.getIp(), networkInfo.getPort());
                senderSocket.connect(destination);
                outputStream = new ObjectOutputStream(senderSocket.getOutputStream());
                outputStream.writeObject(toSend);
                outputStream.flush();
            } catch ( IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns direct reference to the sendingQueue.
     * @return Dirent reference to sendingQueue.
     */
    public BlockingQueue<Message> getQueue() {
        return sendingQueue;
    }

    /**
     * Returns direct reference to the senderThread.
     * @return Direct reference to senderThread.
     */
    public Thread getThread() {
        return senderThread;
    }

}
