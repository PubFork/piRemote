package Sender;

import MessageObject.Message;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * created by fabian on 13.11.15
 */

public class ClientSenderThread implements Runnable {

    public final static BlockingQueue<Message> sendingQueue = new LinkedBlockingQueue<Message>();
    private Thread senderThread;
    private Socket socket;
    private DataOutputStream outputStream;

    public ClientSenderThread(Socket socket) {
        this.socket = socket;

        try {
            // get the output stream of the socket
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        senderThread = new Thread(this);
        senderThread.start();
    }


    @Override
    public void run() {

        try {
            Message messageToSend = sendingQueue.take();

            /**
             * TODO: prepare message to send (marshalling)
             * suggestion using JSON-Objects
             */


            JSONObject header = new JSONObject();
            JSONObject payload = new JSONObject();
            JSONObject message = new JSONObject();

            header.put("UUID", messageToSend.getUuid().toString());
            header.put("ServerState", messageToSend.getServerState().name());

            if (messageToSend.hasApplicationState()) {
                header.put("ApplicationState", messageToSend.getApplicationState().toString());
            }

            if (messageToSend.hasPayload()) {
                payload.put("PayloadType", messageToSend.getPayload().getClass().getName());
            }


            // outputStream.write();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}