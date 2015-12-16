package application;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.MusicApplicationState;
import core.AbstractApplication;

import java.io.*;
import java.util.UUID;

/**
 *
 */
public class MusicApplication extends AbstractApplication {
    @Override
    public void onApplicationStart() {
        System.out.println("MusicApplication: Initialised.");

        // Set the default application state based on the actual current state of mpd.
        String currentState = executeCommand("mpc", null);
        if (currentState.contains("playing")) {
            changeApplicationState(MusicApplicationState.MUSIC_PLAYING);
        } else if (currentState.contains("paused")) {
            changeApplicationState(MusicApplicationState.MUSIC_PAUSED);
        } else {
            changeApplicationState(MusicApplicationState.MUSIC_STOPPED);
        }
    }

    @Override
    public void onApplicationStop() {
        System.out.println("MusicApplication: Closing client, commencing with music playback.");
    }

    @Override
    public void onApplicationStateChange(ApplicationState newState) {
        if(getApplicationState() != null) {

            System.out.printf("MusicApplication: Changing state %s -> %s\n",
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

        // Send a headified string back to the client.
        sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_FILESELECTION, file.getName()));
        closeFilePicker(senderUUID);
    }

    @Override
    public void onReceiveInt(int i, UUID senderUUID) {
        if(i == ApplicationCsts.MUSIC_PICK_FILE){
            System.out.println("MusicApplication: Initializing file selection.");
            pickFile(System.getProperty("user.home")+"/piremote",senderUUID);
        } else if (getApplicationState().equals(MusicApplicationState.MUSIC_STOPPED)) {
            switch (i) {
                case ApplicationCsts.MUSIC_PLAY:
                    // Set the application state to playing and start playing music
                    changeApplicationState(MusicApplicationState.MUSIC_PLAYING);
                    executeCommand("mpc play", null);

                    // Broadcast to each client the currently playing song's name
                    String toSend = createMessage(ApplicationCsts.MUSIC_PREFIX_SONG, executeCommand("mpc current", null));
                    System.out.printf("Sending %s\n", toSend);
                    sendString(toSend);
                    break;
                case ApplicationCsts.MUSIC_PAUSE:
                case ApplicationCsts.MUSIC_STOP:
                case ApplicationCsts.MUSIC_GET_CURRENT:
                case ApplicationCsts.MUSIC_NEXT:
                case ApplicationCsts.MUSIC_PREV:
                    System.out.println("MusicApplication: Ignoring invalid command/state combination.");
                    break;
                case ApplicationCsts.MUSIC_STATUS:
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_STATUS, executeCommand("mpc", null)), senderUUID);
                    break;
                case ApplicationCsts.MUSIC_VOLUME_UP:
                    executeCommand("mpc volume +2", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_VOLUME_DOWN:
                    executeCommand("mpc volume -2", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_LOOP:
                    executeCommand("mpc repeat on", null);
                    executeCommand("mpc single off", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_SINGLE:
                    executeCommand("mpc repeat off", null);
                    executeCommand("mpc single on", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_NOLOOPING:
                    executeCommand("mpc repeat off", null);
                    executeCommand("mpc single off", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_SHUFFLE_ON:
                    executeCommand("mpc random on", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_SHUFFLE_OFF:
                    executeCommand("mpc random off", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_GET_PLAYLIST:
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_PLAYLIST, executeCommand("mpc playlist", null)), senderUUID);
                    break;
                default:
                    System.out.println("MusicApplication: Ignoring invalid command/state combination.");
                    break;
            }
        } else {
            switch(i) {
                case ApplicationCsts.MUSIC_PLAY:
                    // Set the application state to playing and start playing music
                    changeApplicationState(MusicApplicationState.MUSIC_PLAYING);
                    executeCommand("mpc play", null);

                    // Broadcast to each client the currently playing song's name
                    String toSend = createMessage(ApplicationCsts.MUSIC_PREFIX_SONG, executeCommand("mpc current", null));
                    System.out.printf("Sending %s\n", toSend);
                    sendString(toSend);
                    break;
                case ApplicationCsts.MUSIC_PAUSE:
                    changeApplicationState(MusicApplicationState.MUSIC_PAUSED);
                    executeCommand("mpc pause", null);
                    break;
                case ApplicationCsts.MUSIC_STOP:
                    changeApplicationState(MusicApplicationState.MUSIC_STOPPED);
                    executeCommand("mpc stop", null);
                    break;
                case ApplicationCsts.MUSIC_STATUS:
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_STATUS, executeCommand("mpc", null)), senderUUID);
                    break;
                case ApplicationCsts.MUSIC_GET_CURRENT:
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_SONG, executeCommand("mpc current", null)), senderUUID);
                    break;
                case ApplicationCsts.MUSIC_NEXT:
                    executeCommand("mpc next", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_SONG, executeCommand("mpc current", null)));
                    break;
                case ApplicationCsts.MUSIC_PREV:
                    executeCommand("mpc prev", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_SONG, executeCommand("mpc current", null)));
                    break;
                case ApplicationCsts.MUSIC_VOLUME_UP:
                    executeCommand("mpc volume +2", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_VOLUME_DOWN:
                    executeCommand("mpc volume -2", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_LOOP:
                    executeCommand("mpc repeat on", null);
                    executeCommand("mpc single off", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_SINGLE:
                    executeCommand("mpc repeat off", null);
                    executeCommand("mpc single on", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_NOLOOPING:
                    executeCommand("mpc repeat off", null);
                    executeCommand("mpc single off", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_SHUFFLE_ON:
                    executeCommand("mpc random on", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_SHUFFLE_OFF:
                    executeCommand("mpc random off", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_GET_PLAYLIST:
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_PLAYLIST, executeCommand("mpc playlist", null)), senderUUID);
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

    /**
     * This method is used to execute a local command on the server and have the output returned.
     * @param strCommand Command to execute.
     * @param strParams Command passed as extra, to be appended as is
     * @return Result of the command.
     */
    private String executeCommand(String strCommand, String strParams) {
        ProcessBuilder processBuilder = new ProcessBuilder(buildCommand(strCommand, strParams));
        processBuilder.redirectErrorStream(true);

        // Logging of command to exec
        System.out.printf("Music: Executing command %s\n", processBuilder.command().toString());

        Process mpcProcess;
        InputStream mpcStdout;
        BufferedReader mpcReader;

        String result = null;

        try {
            mpcProcess = processBuilder.start();
            mpcStdout = mpcProcess.getInputStream();
            mpcReader = new BufferedReader(new InputStreamReader(mpcStdout));
            mpcProcess.waitFor();

            StringBuilder resultBuilder = new StringBuilder();
            while ((result = mpcReader.readLine()) != null) {
                resultBuilder.append(result);
                resultBuilder.append("\n");
            }
            resultBuilder.deleteCharAt(resultBuilder.length()-1);
            result = resultBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Executed command, got back: %s\n", result);

        return result;
    }

    /**
     * Internal method to quickly build a string with a header used to set the UI of the client.
     * @param header Header identifying which parts of the UI are to be set.
     * @param content Value the UI elements will be set to.
     * @return Returns a concatenated string.
     */
    private String createMessage(String header, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append(header);
        builder.append(content);

        return builder.toString();
    }

    /**
     * Internal method returning an array of command-strings that can be processed.
     * @param command Command that is to be split up.
     * @param raw Raw command that is to be seen as one string. No splitting.
     * @return Array of strings.
     */
    private String[] buildCommand(String command, String raw) {
        String[] params = command.split(" ");
        if (raw == null) {
            return params;
        } else {
            int count = params.length;
            int index = 0;
            String[] result = new String[count + 1];

            for (; index < params.length; ++index) {
                result[index] = params[index];
            }

            result[index] = raw;
            return result;
        }
    }

}