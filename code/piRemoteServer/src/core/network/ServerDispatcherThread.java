package core.network;

import MessageObject.Message;
import MessageObject.PayloadObject.Payload;
import SharedConstants.ApplicationCsts;
import SharedConstants.CoreCsts;
import com.sun.corba.se.spi.activation.Server;
import core.ServerCore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * created by fabian on 13.11.15
 */

public class ServerDispatcherThread implements Runnable {

    // private ServerKeepAliveThread keepAliveThread;
    private List<NetworkInfo> morgueQueue = new ArrayList<>();
    private Thread serverDispatcher;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private HashMap<UUID, NetworkInfo> sessionTable;
    private BlockingQueue<Message> sendingQueue;

    public ServerDispatcherThread(ServerSocket socket, HashMap sTable, ServerSenderThread senderThread) {

        serverSocket = socket;
        sessionTable = sTable;
        sendingQueue = senderThread.getSendingQueue();

        serverDispatcher = new Thread(this);
        serverDispatcher.start();
    }

    @Override
    public void run() {

        // TODO: How to read from ServerSocket?
        // Message receivedMessage = null; // need to be the received Message!

        try {
            clientSocket = serverSocket.accept();
            InetAddress ip = clientSocket.getInetAddress();
            int port = clientSocket.getPort();

            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

            // TODO: what about first connection message? it is not of type Message! or yes?
            Message receivedMessage = (Message) input.readObject();
            UUID uuid = receivedMessage.getUuid();

            // TODO: check morgeQueue

            // check the sessionTable
            if (!sessionTable.containsKey(uuid)) {
                NetworkInfo clientInfo = new NetworkInfo(ip, port, System.currentTimeMillis());
                sessionTable.put(uuid, clientInfo);
            } else {
                // update lastSeen
                sessionTable.get(uuid).lastSeen = System.currentTimeMillis();
            }



            // TODO: first check if it is a FilePickerRequest

            // put message on mainQueue from Core
            ServerCore.mainQueue.put(receivedMessage);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}