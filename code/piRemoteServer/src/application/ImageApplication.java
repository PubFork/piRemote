package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;

import java.io.*;
import java.util.UUID;

/**
 * Created by Fabian on 14.12.15.
 */
public class ImageApplication extends AbstractApplication{

    String imagePath = "";
    ProcessBuilder processBuilder = null;
    Process fehProcess = null;
    OutputStream fehStdin = null;
    InputStream fehStderr = null;
    InputStream fehStdout = null;
    BufferedReader fehReader = null;
    BufferedWriter fehWriter = null;

    @Override
    public void onApplicationStart() {
        System.out.println("ImageApplication: Starting up");
        changeApplicationState(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED);
    }

    @Override
    public void onApplicationStop() {
        System.out.println("ImageApplication: Will now stop.");
        stopProcess();
    }


    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        System.out.println("ImageApplication: Shall change state from "+getApplicationState().toString()+" to "+newState.toString());

        if(getApplicationState().equals(newState)) {
            System.out.println("ImageApplication: Warning: Ignoring attempt to change to current state: "
                    + getApplicationState().toString());
            return;
        }
        if (newState.equals(ApplicationCsts.ImageApplicationState.IMAGE_DISPLAYED)) {
            if (getApplicationState().equals(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED)) {
                // no image is displayed --> start displaying
                startProcess(imagePath);
            } else {
                // a image is already displayed --> send next image to process
                sendToProcess(imagePath);
            }
        } else {
            // call to show nothing --> stop process
            stopProcess();
        }
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        System.out.println("ImageApplication: File picked: "+file.getPath());
        imagePath = file.getAbsolutePath();
    }

    @Override
    public void onReceiveInt(int i, UUID senderUUID) {
        if (i == ApplicationCsts.IMAGE_PICK_FILE) {
            System.out.println("ImageApplication: Initializing file pick.");
            pickFile("/home/sandro",senderUUID);
        } else {
            if (getApplicationState().equals(ApplicationCsts.ImageApplicationState.IMAGE_DISPLAYED)
                || getApplicationState().equals(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED)) {
                switch (i) {
                    case ApplicationCsts.IMAGE_HIDE:
                        changeApplicationState(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED);
                        break;
                    case ApplicationCsts.IMAGE_SHOW:
                        changeApplicationState(ApplicationCsts.ImageApplicationState.IMAGE_DISPLAYED);
                        break;
                    default:
                        System.out.println("ImageApplication: Warning: Ignoring invalid value: " + Integer.toString(i));
                        break;
                }
            } else {
                System.out.println("ImageApplication: Warning: Ignoring invalid value: " + Integer.toString(i));
            }
        }
    }

    @Override
    public void onReceiveDouble(double d, UUID senderUUID) { }

    @Override
    public void onReceiveString(String str, UUID senderUUID) { }


    void startProcess(String path) {
        if (fehProcess != null) stopProcess();

        processBuilder = new ProcessBuilder("/usr/bin/feh", path);
        // TODO: redirect error stream?
        try {
            fehProcess = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fehProcess != null) {
            fehStdin = fehProcess.getOutputStream();
            fehStderr = fehProcess.getErrorStream();
            fehStdout = fehProcess.getInputStream();

            fehReader = new BufferedReader(new InputStreamReader(fehStdout));
            fehWriter = new BufferedWriter(new OutputStreamWriter(fehStdin));
        }
    }

    void stopProcess() {
        if(fehProcess != null){
            fehProcess.destroy();
            try {
                fehProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                try {
                    fehReader.close();
                    fehWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        fehProcess = null;
    }

    void sendToProcess(String toSend) {
        try {
            fehWriter.write(toSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
