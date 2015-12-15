package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;

import java.io.*;
import java.util.UUID;

/**
 * Created by Fabian on 14.12.15.
 * Requires geeqie to be installed on the server.
 */
public class ImageApplication extends AbstractApplication{

    String imagePath = "";
    ProcessBuilder processBuilder = null;

    @Override
    public void onApplicationStart() {
        System.out.println("ImageApplication: Starting up");
        changeApplicationState(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED);
    }

    @Override
    public void onApplicationStop() {
        System.out.println("ImageApplication: Will now stop.");
        sendToProcess("-q");
    }


    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        if(getApplicationState() == null) return; // First time do nothing

        System.out.println("ImageApplication: Shall change state from "+getApplicationState().toString()+" to "+newState.toString());

        if(getApplicationState().equals(newState)) {
            System.out.println("ImageApplication: Warning: Ignoring attempt to change to current state: "
                    + getApplicationState().toString());
            return;
        }
        if (newState.equals(ApplicationCsts.ImageApplicationState.IMAGE_DISPLAYED)) {
            // As above .equals() if statement has not triggered, we know that we shall start up
            startProcess(imagePath);
        } else {
            // Terminate geeqie
            sendToProcess("-q");
        }
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        closeFilePicker(senderUUID);
        System.out.println("ImageApplication: File picked: "+file.getPath());

        changeApplicationState(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED);

        // Run new image
        imagePath = file.getAbsolutePath();
        sendString(file.getName());
        changeApplicationState(ApplicationCsts.ImageApplicationState.IMAGE_DISPLAYED);
    }

    @Override
    public void onReceiveInt(int i, UUID senderUUID) {
        if (i == ApplicationCsts.IMAGE_PICK_FILE) {
            System.out.println("ImageApplication: Initializing file pick.");
            pickFile(System.getProperty("user.home"),senderUUID);
        } else {
            switch (i) {
                case ApplicationCsts.IMAGE_HIDE:
                    changeApplicationState(ApplicationCsts.ImageApplicationState.IMAGE_DISPLAYED);
                    break;
                case ApplicationCsts.IMAGE_SHOW:
                    changeApplicationState(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED);
                    break;
                case ApplicationCsts.IMAGE_NEXT:
                    sendToProcess("-n");
                    break;
                case ApplicationCsts.IMAGE_PREV:
                    sendToProcess("-b");
                    break;
                default:
                    System.out.println("ImageApplication: Warning: Ignoring invalid value: " + Integer.toString(i));
                    break;
            }
        }
    }

    @Override
    public void onReceiveDouble(double d, UUID senderUUID) { }

    @Override
    public void onReceiveString(String str, UUID senderUUID) { }


    void startProcess(String path) {
        processBuilder = new ProcessBuilder("geeqie","-r", "-f", path);
        try {
            Process prc = processBuilder.start();
            prc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void sendToProcess(String flag){
        if(applicationState.equals(ApplicationCsts.ImageApplicationState.IMAGE_DISPLAYED)) {
            processBuilder = new ProcessBuilder("geeqie", "-r", flag);
            try {
                Process prc = processBuilder.start();
                prc.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("ImageApplication: Warning: Didn't send flag \""+flag+"\" to process because it ain't running.");
        }
    }
}
