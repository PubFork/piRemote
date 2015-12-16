package application;

import SharedConstants.ApplicationCsts;
import SharedConstants.ApplicationCsts.ApplicationState;
import SharedConstants.ApplicationCsts.MusicApplicationState;
import core.AbstractApplication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.UUID;

/**
 *
 */
public class MusicApplication extends AbstractApplication {

    /**
     * Initialisation of the music application.
     */
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

    /**
     * Destruction of the music application.
     */
    @Override
    public void onApplicationStop() {
        System.out.println("MusicApplication: Closing client, commencing with music playback.");
    }

    /**
     * Combination of state transitions possible for the server application.
     * @param newState Next state to transition into.
     */
    @Override
    public void onApplicationStateChange(@NotNull ApplicationState newState) {
        if(getApplicationState() != null) {
            // Don't transition to a new state if our current state is not set explicitly.

            System.out.printf("MusicApplication: Changing state %s -> %s\n",
                    getApplicationState().toString(), newState.toString());

            if (getApplicationState().equals(newState)) {
                System.out.printf("MusicApplication: Warning: Ignoring attempt to change to current state: %s\n",
                        getApplicationState().toString());

            } else if (newState.equals(MusicApplicationState.MUSIC_PLAYING)) {

                if (getApplicationState().equals(MusicApplicationState.MUSIC_STOPPED)) {
                    // No active playback of music, start playback.
                    System.out.printf("MusicApplication: Starting playback.\n");
                } else {
                    // Commence playback, we are currently paused.
                    System.out.printf("MusicApplication: Commencing playback.\n");
                }

            } else if (newState.equals(MusicApplicationState.MUSIC_PAUSED)) {

                if (getApplicationState().equals(MusicApplicationState.MUSIC_PLAYING)) {
                    // Currently playing back music, pause playback
                    System.out.printf("MusicApplication: Pausing playback.\n");
                }

            } else {
                // New state is to stop playback.
                System.out.printf("MusicApplication: Stopping playback.\n");
            }
        }
    }

    /**
     * This method is so far a placeholder that could be extended to work on the current playlist.
     * @param file File to be played next.
     * @param senderUUID Sender that selected the file.
     */
    @Override
    public void onFilePicked(@NotNull File file, @NotNull UUID senderUUID) {
        System.out.printf("MusicApplication: Picked file %s. Requesting close.\n", file.getName());

        // Send string back to the client and request close.
        sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_FILESELECTION, file.getName()));
        closeFilePicker(senderUUID);
    }

    /**
     * Main method using predefined constants to execute local commands.
     * @param i Predefined constant mapped to a specific command to be run.
     * @param senderUUID Sender that sent the command.
     */
    @Override
    public void onReceiveInt(int i, UUID senderUUID) {
        if(i == ApplicationCsts.MUSIC_PICK_FILE){
            System.out.println("MusicApplication: Initializing file selection.");
            pickFile(System.getProperty("user.home"),senderUUID);
        } else {
            switch(i) {
                case ApplicationCsts.MUSIC_PLAY:
                    changeApplicationState(MusicApplicationState.MUSIC_PLAYING);
                    executeCommand("mpc play", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_SONG, executeCommand("mpc current", null)));
                    break;
                case ApplicationCsts.MUSIC_PAUSE:
                    if (!getApplicationState().equals(MusicApplicationState.MUSIC_STOPPED)) {
                        changeApplicationState(MusicApplicationState.MUSIC_PAUSED);
                        executeCommand("mpc pause", null);
                    } else {
                        System.out.println("MusicApplication: Ignoring invalid command/state combination.");
                    }
                    break;
                case ApplicationCsts.MUSIC_STOP:
                    if (!getApplicationState().equals(MusicApplicationState.MUSIC_STOPPED)) {
                        changeApplicationState(MusicApplicationState.MUSIC_STOPPED);
                        executeCommand("mpc stop", null);
                    } else {
                        System.out.println("MusicApplication: Ignoring invalid command/state combination.");
                    }
                    break;
                case ApplicationCsts.MUSIC_STATUS:
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_STATUS, executeCommand("mpc", null)), senderUUID);
                    break;
                case ApplicationCsts.MUSIC_GET_CURRENT:
                    if (!getApplicationState().equals(MusicApplicationState.MUSIC_STOPPED)) {
                        sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_SONG, executeCommand("mpc current", null)), senderUUID);
                    } else {
                        System.out.println("MusicApplication: Ignoring invalid command/state combination.");
                    }
                    break;
                case ApplicationCsts.MUSIC_NEXT:
                    if (!getApplicationState().equals(MusicApplicationState.MUSIC_STOPPED)) {
                        executeCommand("mpc next", null);
                        sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_SONG, executeCommand("mpc current", null)));
                    } else {
                        System.out.println("MusicApplication: Ignoring invalid command/state combination.");
                    }
                    break;
                case ApplicationCsts.MUSIC_PREV:
                    if (!getApplicationState().equals(MusicApplicationState.MUSIC_STOPPED)) {
                        executeCommand("mpc prev", null);
                        sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_SONG, executeCommand("mpc current", null)));
                    } else {
                        System.out.println("MusicApplication: Ignoring invalid command/state combination.");
                    }
                    break;
                case ApplicationCsts.MUSIC_VOLUME_UP:
                    executeCommand("mpc volume +1", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_VOLUME_DOWN:
                    executeCommand("mpc volume -1", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_LOOP:
                    executeCommand("mpc repeat on", null);
                    executeCommand("mpc single off", null);
                    sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
                    break;
                case ApplicationCsts.MUSIC_SINGLE:
                    executeCommand("mpc repeat on", null);
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

    /**
     * Not used by the application.
     * @param d
     * @param senderUUID
     */
    @Override
    public void onReceiveDouble(double d, @NotNull UUID senderUUID) {
        System.out.format("MusicApplication: Received double %f from client %s\n", d, senderUUID.toString());
    }

    /**
     * Used by the application to set an absolute volume.
     * @param str String to process.
     * @param senderUUID Sender of the string.
     */
    @Override
    public void onReceiveString(String str, @NotNull UUID senderUUID) {
        if (str.contains(ApplicationCsts.MUSIC_PREFIX_VOLUME)) {
            String newVolume = str.substring(ApplicationCsts.MUSIC_PREFIX_VOLUME.length());
            executeCommand("mpc volume " + newVolume, null);
            sendString(createMessage(ApplicationCsts.MUSIC_PREFIX_EXTRA, executeCommand("/bin/bash -c", "mpc | tail -n1")));
        } else {
            System.out.format("MusicApplication: Received string %s from client %s\n", str, senderUUID.toString());
        }
    }

    /**
     * This method is used to execute a local command on the server and return the output of it.
     * @param strCommand Command to execute.
     * @param strParams Command passed as extra, to be appended as is
     * @return Result of the command.
     */
    @NotNull
    private String executeCommand(@NotNull String strCommand, String strParams) {
        ProcessBuilder processBuilder = new ProcessBuilder(buildCommand(strCommand, strParams));
        processBuilder.redirectErrorStream(true);

        Process mpcProcess;
        InputStream mpcStdout;
        BufferedReader mpcReader;
        String result = "NULL";

        try {
            // Start the process and get STDOUT from it.
            mpcProcess = processBuilder.start();
            mpcStdout = mpcProcess.getInputStream();
            mpcReader = new BufferedReader(new InputStreamReader(mpcStdout));
            mpcProcess.waitFor();

            // Build the returned string of the command.
            StringBuilder resultBuilder = new StringBuilder();

            while ((result = mpcReader.readLine()) != null) {
                // Build the returned string up as it was returned. This removes newline characters between lines.
                resultBuilder.append(result);
                resultBuilder.append("\n");
            }

            if (resultBuilder.length() > 0) {
                // Delete the last newline character set during the while-loop.
                resultBuilder.deleteCharAt(resultBuilder.length() - 1);
            } else {
                resultBuilder.append("NULL");
            }

            result = resultBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Internal method to quickly build a string with a header used to set the UI of the client.
     * @param header Header identifying which parts of the UI are to be set.
     * @param content Value the UI elements will be set to.
     * @return Returns a concatenated string.
     */
    @NotNull
    private String createMessage(@NotNull String header, @NotNull String content) {
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
    @NotNull
    private String[] buildCommand(@NotNull String command, @Nullable String raw) {
        String[] params = command.split(" ");

        if (raw == null) {
            // No bash command to execute, return the split string.
            return params;
        } else {
            // Bash command to be executed, create new array of strings one element larger and append raw unformatted.
            int count = params.length;
            String[] result = new String[count + 1];
            int index = 0;


            for (; index < count; ++index) {
                result[index] = params[index];
            }

            result[index] = raw;
            return result;
        }
    }

}