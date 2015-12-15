package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;
import SharedConstants.ApplicationCsts.RadioPiApplicationState;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by FR4NK-W on 12.12.15.
 * Client side of RadioPi2473 application
 */
public class RadioPi extends AbstractApplication {
    Process p;
    String soundFile = "./radiopi2473/music/star_wars.wav";
    String frequency = "96.9"; // use 96.9 or gge
    //     String clientCurrentUnset = "./radiopi2473/music/";

    @Override
    public void onApplicationStart() {
        System.out.println("Radio Pi: Starting up.");
        changeApplicationState(RadioPiApplicationState.RADIO_INIT);
    }

    @Override
    public void onApplicationStop() {
        System.out.println("Radio Pi: Will now stop.");
    }

    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        String str;
        String executable = "pifm";
        if(newState.equals(RadioPiApplicationState.RADIO_PLAY)) {
            String frequency = "96.9"; // use 96.9 or gge
            if (soundFile!=null) {
                try {
                    System.out.println("Executing:" + "sudo ./radiopi2473/" + executable + " " + soundFile + " " + frequency);
                    p = Runtime.getRuntime().exec("sudo ./radiopi2473/" + executable + " " + soundFile + " " + frequency);
                    System.out.println("Radio Pi: Broadcasting");
                } catch (IOException e) {
                    System.out.println("Radio Pi: Broadcast failed");
                    e.printStackTrace();
                }
            }
            str = "Play";
        } else if(newState.equals(RadioPiApplicationState.RADIO_INIT)) {
            str = "Init";
        } else if(newState.equals(RadioPiApplicationState.RADIO_STOP)) {
            if (p!=null) {
                p.destroy();
                try {
                    p.waitFor(); // wait for the child to terminate
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            str = "Stop";
        } else {
            throw new RuntimeException("RadioPiApplication read unknown state!" + newState.toString());
        }
        System.out.println("Radio Pi: New state is: "+str);
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        System.out.println("Radio Pi: Picked file: "+file.getName()+". Requesting close.");
        sendString(file.getName(), senderUUID);
        closeFilePicker(senderUUID);
    }

    @Override
    public void onReceiveInt(int cst, UUID senderUUID) {
        if(cst == ApplicationCsts.RADIO_PICK_FILE){
            pickFile("/home/pi/piremote/radiopi2473",senderUUID);
            return;
        }

        ApplicationCsts.ApplicationState newState;
        switch (cst){
            case ApplicationCsts.RADIO_GO_PLAY:
                newState = RadioPiApplicationState.RADIO_PLAY;
                break;
            case ApplicationCsts.RADIO_GO_INIT:
                newState = RadioPiApplicationState.RADIO_INIT;
                break;
            case ApplicationCsts.RADIO_GO_STOP:
                newState = RadioPiApplicationState.RADIO_STOP;
                break;
            default:
                return;
        }
        changeApplicationState(newState);
    }

    @Override
    public void onReceiveDouble(double d, UUID senderUUID) {

    }

    @Override
    public void onReceiveString(String str, UUID senderUUID) {
        if (str.contains(":")) {
            System.out.println("Received String: " + str);
            String[] fileFreq = str.split(":");
            //soundFile = fileFreq[0];
            frequency = fileFreq[1];
            System.out.println("Radio Pi: file and frequency set");
        } else {
            System.out.println("Received String, set soundFile to : " + str);
            soundFile = str;
        }
    }
}
