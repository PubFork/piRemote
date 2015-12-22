package application;

import SharedConstants.ApplicationCsts;
import core.AbstractApplication;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by sandro on 22.12.15.
 */
public class ShutdownApplication extends AbstractApplication{
    @Override
    public void onApplicationStart() {
        changeApplicationState(ApplicationCsts.ShutdownApplicationState.SHUTDOWN_APPLICATION_STATE);
    }

    @Override
    public void onApplicationStop() {

    }

    @Override
    public void onApplicationStateChange(ApplicationCsts.ApplicationState newState) {

    }

    @Override
    public void onFilePicked(File file, UUID senderUUID) {

    }

    @Override
    public void onReceiveInt(int i, UUID senderUUID) {
        if(i==ApplicationCsts.SHUTODNW_BUTTON_PRESSED){
            try {
                System.out.println("Shutdown: Shutting down!");
                Runtime.getRuntime().exec("sudo poweroff");
            } catch (IOException e) {
                e.printStackTrace();
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
