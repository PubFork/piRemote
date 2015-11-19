package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;
import java.io.File;
import java.util.UUID;

/**
 * Created by sandro on 11.11.15.
 */
public class TrafficLight extends AbstractApplication {
    @Override
    public void onApplicationStart() {
        System.out.println("TrafficLight: Starting up.");
        changeApplicationState(ApplicationCsts.TrafficLightApplicationState.GREEN);
    }

    @Override
    public void onApplicationStop() {

    }

    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {
        String str;
        if(newState.equals(ApplicationCsts.TrafficLightApplicationState.GREEN)) str = "Green";
        else if(newState.equals(ApplicationCsts.TrafficLightApplicationState.ORANGE)) str = "Orange";
        else if(newState.equals(ApplicationCsts.TrafficLightApplicationState.RED)) str = "Red";
        else throw new RuntimeException("TrafficApplication read unknown state!" + newState.toString());
        System.out.println("TrafficLight: New state is: "+str);
    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {
        System.out.println("TrafficLight: Picked file: "+file.getName()+". Requesting close.");
        closeFilePicker(senderUUID);
    }

    @Override
    public void onReceiveInt(int cst, UUID senderUUID) {
        if(cst == ApplicationCsts.TL_PICK_FILE){
            System.out.println("TrafficLight: Initializing file pick.");
            pickFile("/home/sandro",senderUUID);
            return;
        }

        ApplicationCsts.ApplicationState newState;
        switch (cst){
            case ApplicationCsts.GO_GREEN:
                newState = ApplicationCsts.TrafficLightApplicationState.GREEN;
                break;
            case ApplicationCsts.GO_ORANGE:
                newState = ApplicationCsts.TrafficLightApplicationState.ORANGE;
                break;
            case ApplicationCsts.GO_RED:
                newState = ApplicationCsts.TrafficLightApplicationState.RED;
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
