package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;

import java.io.File;
import java.util.UUID;

/**
 * Created by sandro on 08.12.15.
 */
public class VideoApplication extends AbstractApplication {
    @Override
    public void onApplicationStart() {
        System.out.println("VideoApplication: Starting up.");
        changeApplicationState(ApplicationCsts.VideoApplicationState.VIDEO_STOPPED);
    }

    @Override
    public void onApplicationStop() {
        System.out.println("VideoApplication: Will now stop.");
        // TODO: Stop running process (if any)
    }

    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        System.out.println("VideoApplication: Shall change state to "+newState.toString());
        if(getApplicationState() == null) return; // First time do nothing

        if(getApplicationState().equals(getApplicationState()))
            System.out.println("VideoApplication: Warning: Ignoring attempt to change to current state: "
                    + getApplicationState().toString());
        if(getApplicationState().equals(ApplicationCsts.VideoApplicationState.VIDEO_STOPPED)){
            if(newState.equals(ApplicationCsts.VideoApplicationState.VIDEO_PLAYING)){
                // TODO: Start playing
            }else{
                System.out.println("VideoApplication: Warning: Invalid transition stopped -> paused");
            }
        }else{
            // We are playing or paused
            if(newState.equals(ApplicationCsts.VideoApplicationState.VIDEO_STOPPED)){
                // TODO: Stop process
            }else{
                // TODO: Press space in running process
            }
        }
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        System.out.println("VideoApplication: File picked: "+file.getPath());
        // TODO!
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
                        // TODO!
                        break;
                    case ApplicationCsts.VIDEO_JUMP_FORWARD:
                        // TODO!
                        break;
                    case ApplicationCsts.VIDEO_SPEED_SLOWER:
                        // TODO!
                        break;
                    case ApplicationCsts.VIDEO_SPEED_FASTER:
                        // TODO!
                        break;
                    case ApplicationCsts.VIDEO_VOLUME_INCREASE:
                        // TODO!
                        break;
                    case ApplicationCsts.VIDEO_VOLUME_DECREASE:
                        // TODO!
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
}
