package core.network;

import ConnectionManagement.Connection;
import MessageObject.Message;
import core.ServerCore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * created by fabian on 13.11.15
 */

public class ServerDispatcherThread implements Runnable {

    // private ServerKeepAliveThread keepAliveThread;
    private static BlockingQueue<Session> morgueQueue;
    private Thread serverDispatcher;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private HashMap<UUID, NetworkInfo> sessionTable;
    private BlockingQueue<Message> sendingQueue;

    private final Thread dispatcherThread;

    /**
     * The Dispatcher receives on the ServerSocket at
     * @param port
     */
    public ServerDispatcherThread(int port, HashMap sTable, ServerSenderThread senderThread) {
        this.morgueQueue = new LinkedBlockingQueue<>();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sessionTable = sTable;
        sendingQueue = senderThread.getQueue();

        this.dispatcherThread = new Thread(this);
        // dispatcherThread.start();
    }

    @Override
    public void run() {

        while (ServerNetwork.isRunning()) {

            try {
                clientSocket = serverSocket.accept();

                InetAddress ip = clientSocket.getInetAddress();
                int port = clientSocket.getPort();

                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

                while (!morgueQueue.isEmpty()) {
                    sessionTable.remove(morgueQueue.take());
                }

                if (input.readObject() instanceof Message) {
                    Message receivedMessage = (Message) input.readObject();
                    UUID uuid = receivedMessage.getUuid();
                    AtomicLong currentTime = new AtomicLong(System.currentTimeMillis());

                    // check the sessionTable
                    if (!sessionTable.containsKey(uuid)) {
                        NetworkInfo clientInfo = new NetworkInfo(ip, port, currentTime);
                        sessionTable.put(uuid, clientInfo);
                    } else {
                        // update lastSeen
                        sessionTable.get(uuid).updateLastSeen(currentTime);
                    }

                    // (TODO: first check if it is a FilePickerRequest) is handled by ServerCore

                    // put message on mainQueue from Core
                    ServerCore.mainQueue.put(receivedMessage);

                } else if (input.readObject() instanceof Connection) {
                    Connection connection = (Connection) input.readObject();

                    if (connection.getConnection() == Connection.Connect.CONNECT) {
                        // I would only do that if it is a connectRequest
                        UUID uuid = new UUID(1, 1);
                        uuid = uuid.randomUUID();

                        AtomicLong currentTime = new AtomicLong(System.currentTimeMillis());
                        NetworkInfo clientInfo = new NetworkInfo(ip, port, currentTime);
                        sessionTable.put(uuid, clientInfo);

                        sendingQueue.add(new Message(uuid, ServerCore.getState().getServerState(), ServerCore.getState().getApplicationState()));
                    } else if (connection.getConnection() == Connection.Connect.DISCONNECT) {
                        // TODO: remove from sessionTable -> include UUID in diconnection?
                        UUID uuid = connection.getUuid();
                        sessionTable.remove(uuid);
                        // Session session = new Session(uuid, sessionTable.get(uuid));
                        // morgueQueue.add(session);
                    }

                }

            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns direct reference to the morgueQueue.
     * @return Dirent reference to morgueQueue.
     */
    public BlockingQueue<Session> getQueue() {
        return morgueQueue;
    }

    /**
     * Returns direct reference to the dispatcherThread.
     * @return Direct reference to dispatcherThread.
     */
    public Thread getThread() {
        return dispatcherThread;
    }
}