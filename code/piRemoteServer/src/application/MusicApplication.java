package application;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.MusicApplicationState;
import core.AbstractApplication;
import core.ApplicationFactory;
import javafx.application.Application;

import java.io.File;
import java.util.UUID;

/**
 *
 */
public class MusicApplication extends AbstractApplication {
    @Override
    public void onApplicationStart() {
        System.out.println("MusicApplication: Initialised.");
        // TODO: Get actual state from mpd
        changeApplicationState(MusicApplicationState.MUSIC_STOPPED);
    }

    @Override
    public void onApplicationStop() {
        System.out.println("MusicApplication: Closing client, commencing with music playback.");
    }

    @Override
    public void onApplicationStateChange(ApplicationState newState) {
        if(getApplicationState() != null) {

            System.out.printf("MusicApplication: Changing state from: %s\nTo: %s\n",
                    getApplicationState().toString(), newState.toString());

            if (getApplicationState().equals(newState)) {
                System.out.printf("MusicApplication: Warning: Ignoring attempt to change to current state: %s\n",
                        getApplicationState().toString());
            } else if (newState.equals(MusicApplicationState.MUSIC_PLAYING)) {
                if (getApplicationState().equals(MusicApplicationState.MUSIC_STOPPED)) {
                    // No active playback of music, start playback.
                    System.out.printf("MusicApplication: Starting playback.\n");
                    //TODO
                } else {
                    // Commence playback, we are currently paused.
                    System.out.printf("MusicApplication: Commencing playback.\n");
                    //TODO
                }
            } else if (newState.equals(MusicApplicationState.MUSIC_PAUSED)) {
                if (getApplicationState().equals(MusicApplicationState.MUSIC_PLAYING)) {
                    // Currently playing back music, pause playback
                    System.out.printf("MusicApplication: Pausing playback.\n");
                    //TODO
                }
            } else {
                // New state is to stop playback.
                System.out.printf("MusicApplication: Stopping playback.\n");
                // TODO
            }
        }
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        System.out.printf("MusicApplication: Picked file %s. Requesting close.\n", file.getName());

        // Add a header required by client application for sending it to the correct field.
        StringBuilder sendFileName = new StringBuilder();
        sendFileName.append("FILE:");
        sendFileName.append(file.getName());

        // Send it back to the client.
        sendString(sendFileName.toString());
        closeFilePicker(senderUUID);
    }

    @Override
    public void onReceiveInt(int i, UUID senderUUID) {
        if(i == ApplicationCsts.MUSIC_PICK_FILE){
            System.out.println("MusicApplication: Initializing file selection.");
            pickFile(System.getProperty("user.home"),senderUUID);
        } else if (getApplicationState().equals(MusicApplicationState.MUSIC_STOPPED)) {
            switch (i) {
                case ApplicationCsts.MUSIC_PLAY:
                    changeApplicationState(MusicApplicationState.MUSIC_PLAYING);
                    break;
                case ApplicationCsts.MUSIC_PAUSE:
                case ApplicationCsts.MUSIC_STOP:
                case ApplicationCsts.MUSIC_GET_CURRENT:
                case ApplicationCsts.MUSIC_NEXT:
                case ApplicationCsts.MUSIC_PREV:
                    System.out.println("MusicApplication: Ignoring invalid command/state combination.");
                    // Do nothing for these operations.
                    break;
                // else allow setting all the switches
                default:
                    System.out.println("MusicApplication: Ignoring invalid command/state combination.");
                    break;
            }
        } else {
            switch(i) {
                case ApplicationCsts.MUSIC_PLAY:
                    changeApplicationState(MusicApplicationState.MUSIC_PLAYING);
                    break;
                case ApplicationCsts.MUSIC_PAUSE:
                    changeApplicationState(MusicApplicationState.MUSIC_PAUSED);
                    break;
                case ApplicationCsts.MUSIC_STOP:
                    changeApplicationState(MusicApplicationState.MUSIC_STOPPED);
                    break;
                case ApplicationCsts.MUSIC_STATUS:
                    // exec "mpc"
                    // send back to UUID
                    break;
                case ApplicationCsts.MUSIC_GET_CURRENT:
                    // exec "mpc current"
                    // send back to UUID
                    break;
                case ApplicationCsts.MUSIC_NEXT:
                    // exec "mpc next"
                    // no explicit broadcast
                    break;
                case ApplicationCsts.MUSIC_PREV:
                    // exec "mpc previous"
                    // no explicit broadcast
                    break;
                case ApplicationCsts.MUSIC_VOLUME_UP:
                    // exec "mpc volume +2"
                    // broadcast to all clients
                    break;
                case ApplicationCsts.MUSIC_VOLUME_DOWN:
                    // exec "mpc volume -2"
                    // broadcast to all clients
                    break;
                case ApplicationCsts.MUSIC_LOOP:
                    // exec "mpc loop"
                    // broadcast to all clients
                    break;
                case ApplicationCsts.MUSIC_SINGLE:
                    // exec "mpc single"
                    // broadcast to all clients
                    break;
                case ApplicationCsts.MUSIC_SHUFFLE:
                    // exec "mpc shuffle"
                    // broadcast to all clients
                    break;
                case ApplicationCsts.MUSIC_CONSUME:
                    // exec "mpc consume"
                    // broadcast to all clients
                    break;
                case ApplicationCsts.MUSIC_GET_PLAYLIST:
                    // exec "mpc playlist"
                    // send back to client
                    break;
                default:
                    System.out.printf("MusicApplication: Received unknown value %d\n", i);
            }
        }
    }

    @Override
    public void onReceiveDouble(double d, UUID senderUUID) {
        System.out.format("MusicApplication: Received double %f from client %s\n", d, senderUUID.toString());
    }

    @Override
    public void onReceiveString(String str, UUID senderUUID) {
        System.out.format("MusicApplication: Received string %s from client %s\n", str, senderUUID.toString());
    }


}
