package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;

import java.io.*;
import java.util.UUID;

/**
 * Created by sandro on 08.12.15.
 * Application for playing videos and music (not optimized for music content though)
 * In order to get this running, you MUST create a file /home/pi/.omxplayer with the following content:
     DECREASE_SPEED:1
     INCREASE_SPEED:2
     REWIND:<
     FAST_FORWARD:>
     SHOW_INFO:z
     PREVIOUS_AUDIO:j
     NEXT_AUDIO:k
     PREVIOUS_CHAPTER:i
     NEXT_CHAPTER:o
     PREVIOUS_SUBTITLE:n
     NEXT_SUBTITLE:m
     TOGGLE_SUBTITLE:s
     DECREASE_SUBTITLE_DELAY:d
     INCREASE_SUBTITLE_DELAY:f
     EXIT:q
     PAUSE:
     DECREASE_VOLUME:-
     INCREASE_VOLUME:+
     SEEK_BACK_SMALL:x
     SEEK_FORWARD_SMALL:c
     SEEK_BACK_LARGE:y
     SEEK_FORWARD_LARGE:v
     STEP:p
 */
public class VideoApplication extends AbstractApplication {

    String pathToPlay = "";
    ProcessBuilder processBuilder = null;
    Process omxProcess = null;
    OutputStream omxStdin = null;
    InputStream omxStderr = null;
    InputStream omxStdout = null;
    BufferedReader omxReader = null;
    BufferedWriter omxWriter = null;

    @Override
    public void onApplicationStart() {
        System.out.println("VideoApplication: Starting up.");
        changeApplicationState(ApplicationCsts.VideoApplicationState.VIDEO_STOPPED);
    }

    @Override
    public void onApplicationStop() {
        System.out.println("VideoApplication: Will now stop.");
        stopProcess();
    }

    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        if(getApplicationState() == null) return; // First time do nothing

        System.out.println("VideoApplication: Shall change state from "+getApplicationState().toString()+" to "+newState.toString());

        if(getApplicationState().equals(newState)) {
            System.out.println("VideoApplication: Warning: Ignoring attempt to change to current state: "
                    + getApplicationState().toString());
            return;
        }
        if(getApplicationState().equals(ApplicationCsts.VideoApplicationState.VIDEO_STOPPED)){
            if(newState.equals(ApplicationCsts.VideoApplicationState.VIDEO_PLAYING)){
                startProcess(pathToPlay);
            }else{
                System.out.println("VideoApplication: Warning: Invalid transition stopped -> paused");
            }
        }else{
            // We are playing or paused
            if(newState.equals(ApplicationCsts.VideoApplicationState.VIDEO_STOPPED)){
                stopProcess();
            }else{
                sendToProcess(" ");
            }
        }
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        System.out.println("VideoApplication: File picked: "+file.getPath());
        changeApplicationState(ApplicationCsts.VideoApplicationState.VIDEO_STOPPED);
        pathToPlay = file.getAbsolutePath();
        changeApplicationState(ApplicationCsts.VideoApplicationState.VIDEO_PLAYING);
    }

    @Override
    public void onReceiveInt(int i, UUID senderUUID) {
        if(i == ApplicationCsts.VIDEO_PICK_FILE){
            System.out.println("VideoApplication: Initializing file pick.");
            pickFile("/home/sandro",senderUUID);
        }else{
            if(getApplicationState().equals(ApplicationCsts.VideoApplicationState.VIDEO_PLAYING)
                || getApplicationState().equals(ApplicationCsts.VideoApplicationState.VIDEO_PAUSED)){
                switch (i){
                    case ApplicationCsts.VIDEO_PLAY:
                        changeApplicationState(ApplicationCsts.VideoApplicationState.VIDEO_PLAYING);
                        break;
                    case ApplicationCsts.VIDEO_PAUSE:
                        changeApplicationState(ApplicationCsts.VideoApplicationState.VIDEO_PAUSED);
                        break;
                    case ApplicationCsts.VIDEO_STOP:
                        changeApplicationState(ApplicationCsts.VideoApplicationState.VIDEO_STOPPED);
                        break;
                    case ApplicationCsts.VIDEO_JUMP_BACK:
                        sendToProcess("x");
                        break;
                    case ApplicationCsts.VIDEO_JUMP_FORWARD:
                        sendToProcess("c");
                        break;
                    case ApplicationCsts.VIDEO_SPEED_SLOWER:
                        sendToProcess("2");
                        break;
                    case ApplicationCsts.VIDEO_SPEED_FASTER:
                        sendToProcess("1");
                        break;
                    case ApplicationCsts.VIDEO_VOLUME_INCREASE:
                        sendToProcess("+");
                        break;
                    case ApplicationCsts.VIDEO_VOLUME_DECREASE:
                        sendToProcess("-");
                        break;
                    default:
                        System.out.println("VideoApplication: Warning: Ignoring invalid value: "+Integer.toString(i));
                        break;
                }
            }else{
                System.out.println("VideoApplication: Warning: Ignoring invalid request: "+Integer.toString(i));
            }
        }
    }

    @Override
    public void onReceiveDouble(double d, UUID senderUUID) {

    }

    @Override
    public void onReceiveString(String str, UUID senderUUID) {

    }

    void startProcess(String path){
        if(omxProcess != null) stopProcess();
        processBuilder = new ProcessBuilder("/usr/bin/omxplayer", "--key-config /home/pi/.omxplayer",path);
        processBuilder.redirectErrorStream(true);
        try {
            omxProcess = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(omxProcess != null) {
            omxStdin = omxProcess.getOutputStream();
            omxStderr = omxProcess.getErrorStream();
            omxStdout = omxProcess.getInputStream();

            omxReader = new BufferedReader(new InputStreamReader(omxStdout));
            omxWriter = new BufferedWriter(new OutputStreamWriter(omxStdin));
        }
    }

    void stopProcess(){
        if(omxProcess != null){
            omxProcess.destroy();
            try {
                omxProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                try {
                    omxReader.close();
                    omxWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        omxProcess = null;
    }

    void sendToProcess(String what){
        try {
            omxWriter.write(what);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
