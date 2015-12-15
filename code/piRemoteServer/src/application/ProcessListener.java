package application;

import java.util.EventListener;

/**
 * Taken from https://beradrian.wordpress.com/2008/11/03/detecting-process-exit-in-java/
 * Process listener to inherit from when dealing with Process Exit detector
 */

public interface ProcessListener extends EventListener {
    void onProcessExit(Process process);
}