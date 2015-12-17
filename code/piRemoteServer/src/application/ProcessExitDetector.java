package application;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * // From https://beradrian.wordpress.com/2008/11/03/detecting-process-exit-in-java/ (modified)
 * Detects when a process is finished and invokes the associated listeners.
 */
public class ProcessExitDetector extends Thread {

    /** The process for which we have to detect the end. */
    private Process process;
    /** The associated listeners to be invoked at the end of the process. */
    private List<ProcessListener> listeners = new ArrayList<ProcessListener>();

    /**
     * Starts the detection for the given process
     * @param process the process for which we have to detect when it is finished
     */
    public ProcessExitDetector(Process process) {
        try {
            // test if the process is finished
            process.exitValue();
            throw new IllegalArgumentException("The process is already ended");
        } catch (IllegalThreadStateException exc) {
            this.process = process;
        }
    }

    /** @return the process that it is watched by this detector. */
    public Process getProcess() {
        return process;
    }

    public void run() {
        try {
            // drain input stream (from https://www.securecoding.cert.org/confluence/display/java/FIO07-J.+Do+not+let+external+processes+block+on+IO+buffers)
            InputStream is = process.getInputStream();
            int c;
            try {
                while ((c = is.read()) != -1) {
                    System.out.print((char) c); // Uncomment to get the player's output
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // wait for the process to finish
            process.waitFor();
            // invokes the listeners
            for(int i = 0; i < listeners.size(); i++){
                listeners.get(i).onProcessExit(process);
            }
        } catch (InterruptedException e) {
        }
    }

    /** Adds a process listener.
     * @param listener the listener to be added
     */
    public void addProcessListener(ProcessListener listener) {
        listeners.add(listener);
    }

    /** Removes a process listener.
     * @param listener the listener to be removed
     */
    public void removeProcessListener(ProcessListener listener) {
        listeners.remove(listener);
    }
}