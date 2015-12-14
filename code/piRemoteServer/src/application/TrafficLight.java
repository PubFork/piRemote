package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;
import java.io.File;
import java.util.UUID;

/**
 * Created by sandro on 11.11.15.
 * Client side of TrafficLight application
 */
public class TrafficLight extends AbstractApplication {
    @Override
    public void onApplicationStart() {
        System.out.println("TrafficLight: Starting up.");
        changeApplicationState(ApplicationCsts.TrafficLightApplicationState.TRAFFIC_GREEN);
    }

    @Override
    public void onApplicationStop() {
        System.out.println("TrafficLight: Will now stop.");
    }

    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        String str;
        if(newState.equals(ApplicationCsts.TrafficLightApplicationState.TRAFFIC_GREEN)) str = "Green";
        else if(newState.equals(ApplicationCsts.TrafficLightApplicationState.TRAFFIC_ORANGE)) str = "Orange";
        else if(newState.equals(ApplicationCsts.TrafficLightApplicationState.TRAFFIC_RED)) str = "Red";
        else throw new RuntimeException("TrafficApplication read unknown state!" + newState.toString());
        System.out.println("TrafficLight: New state is: "+str);
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        System.out.println("TrafficLight: Picked file: "+file.getName()+". Requesting close.");
        sendString(file.getName());
        closeFilePicker(senderUUID);
    }

    @Override
    public void onReceiveInt(int cst, UUID senderUUID) {
        if(cst == ApplicationCsts.TRAFFIC_PICK_FILE){
            System.out.println("TrafficLight: Initializing file pick.");
            pickFile("/home/sandro",senderUUID);
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
        System.out.println("TrafficLight: Shall change state.");
        changeApplicationState(newState);
    }

    @Override
    public void onReceiveDouble(double d, UUID senderUUID) {

    }

    @Override
    public void onReceiveString(String str, UUID senderUUID) {

    }
}
