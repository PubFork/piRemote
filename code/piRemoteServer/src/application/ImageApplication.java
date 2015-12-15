package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;

import java.io.*;
import java.util.UUID;

/**
 * Created by Fabian on 14.12.15.
 */
public class ImageApplication extends AbstractApplication implements ProcessListener{

    String imagePath = "";
    ProcessBuilder processBuilder = null;
    Process fehProcess = null;
    OutputStream fehStdin = null;
    InputStream fehStderr = null;
    InputStream fehStdout = null;
    BufferedReader fehReader = null;
    BufferedWriter fehWriter = null;
    ProcessExitDetector processExitDetector = null;

    @Override
    public void onApplicationStart() {
        System.out.println("ImageApplication: Starting up");
        changeApplicationState(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED);
    }

    @Override
    public void onApplicationStop() {
        System.out.println("ImageApplication: Will now stop.");
        requestProcessStop();
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
            // The listener determined that the process terminated, nothing to do here.
        }
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        closeFilePicker(senderUUID);
        System.out.println("ImageApplication: File picked: "+file.getPath());

        // Stop current image and busy wait
        requestProcessStop();
        while(!getApplicationState().equals(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED));

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
            if (getApplicationState().equals(ApplicationCsts.ImageApplicationState.IMAGE_DISPLAYED)
                || getApplicationState().equals(ApplicationCsts.ImageApplicationState.IMAGE_NOT_DISPLAYED)) {
                switch (i) {
                    case ApplicationCsts.IMAGE_HIDE:
                        requestProcessStop(); // This will trigger the listener which will set the state
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
        // TODO: Set up tmp lns to path
        processBuilder = new ProcessBuilder("feh","-FY", "-D", "1", "/tmp/feh1", "/tmp/feh2");
        processBuilder.redirectErrorStream(true);
        try {
            fehProcess = processBuilder.start();
            processExitDetector = new ProcessExitDetector(fehProcess);
            processExitDetector.addProcessListener(this);
            processExitDetector.start();
            if(fehProcess != null) {
                fehStdin = fehProcess.getOutputStream();
                fehStderr = fehProcess.getErrorStream();
                fehStdout = fehProcess.getInputStream();

                fehReader = new BufferedReader(new InputStreamReader(fehStdout));
                fehWriter = new BufferedWriter(new OutputStreamWriter(fehStdin));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void requestProcessStop() {
        if(fehProcess != null){
            fehProcess.destroy();
            fehProcess = null;
            // Now wait for the listener to trigger and change state
        }
    }

    @Override
    public void onProcessExit(Process process) {
        System.out.println("ImageApplication: Player exited.");
        try {
            fehReader.close();
            fehWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fehReader = null;
        fehWriter = null;
        processExitDetector.removeProcessListener(this);
        processExitDetector = null;
        fehProcess=null;
        changeApplicationState(ApplicationCsts.VideoApplicationState.VIDEO_STOPPED);
    }
}
