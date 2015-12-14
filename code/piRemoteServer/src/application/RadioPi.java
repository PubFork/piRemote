package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;

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

    @Override
    public void onApplicationStart() {
        System.out.println("TrafficLight: Starting up.");
        changeApplicationState(ApplicationCsts.TrafficLightApplicationState.TRAFFIC_ORANGE);
    }

    @Override
    public void onApplicationStop() {
        System.out.println("TrafficLight: Will now stop.");
    }

    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        String str;
        if(newState.equals(ApplicationCsts.TrafficLightApplicationState.TRAFFIC_GREEN)) {
            String executable = "pifm";
            String frequency = "99.3"; // use 96.9
            if (soundFile!=null) {
                try {
                    System.out.println("Executing:" + "sudo ./radiopi2473/" + executable + " " + soundFile + " " + frequency);
                    p = Runtime.getRuntime().exec("sudo ./radiopi2473/" + executable + " " + soundFile + " " + frequency);
                    System.out.println("RadioPi: Broadcasting");
                } catch (IOException e) {
                    System.out.println("RadioPi: Broadcast failed");
                    e.printStackTrace();
                }
            }
            str = "Green";
        } else if(newState.equals(ApplicationCsts.TrafficLightApplicationState.TRAFFIC_ORANGE)) {
            str = "Orange";
        } else if(newState.equals(ApplicationCsts.TrafficLightApplicationState.TRAFFIC_RED)) {
            if (p!=null) {
                p.destroy();
            }
            str = "Red";
        } else {
            throw new RuntimeException("TrafficApplication read unknown state!" + newState.toString());
        }
        System.out.println("RadioPi: New state is: "+str);
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        System.out.println("RadioPi: Picked file: "+file.getName()+". Requesting close.");
        sendString(file.getName(), senderUUID);
        closeFilePicker(senderUUID);
    }

    @Override
    public void onReceiveInt(int cst, UUID senderUUID) {
        if(cst == ApplicationCsts.TRAFFIC_PICK_FILE){
            System.out.println("RadioPi: Initializing file pick.");
            pickFile("/home/pi/piremote/radiopi2473",senderUUID);
            return;
        }

        ApplicationCsts.ApplicationState newState;
        switch (cst){
            case ApplicationCsts.TRAFFIC_GO_GREEN:
                newState = ApplicationCsts.TrafficLightApplicationState.TRAFFIC_GREEN;
                break;
            case ApplicationCsts.TRAFFIC_GO_ORANGE:
                newState = ApplicationCsts.TrafficLightApplicationState.TRAFFIC_ORANGE;
                break;
            case ApplicationCsts.TRAFFIC_GO_RED:
                newState = ApplicationCsts.TrafficLightApplicationState.TRAFFIC_RED;
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
        soundFile = str;
    }
}
