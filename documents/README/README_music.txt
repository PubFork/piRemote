MUSIC_APPLICATION:

This application is basically an extension to 'mpc' (a media player client for 'mpd', the music player daemon) in that we send known ints/strings from the client via button presses to the server and therefore execute mpc locally and control mpd that way.
This application allows reading the current playlist, showing the currently playing song, read the current volume, get state information on the playback modifiers "shuffle", "loop", "single", and obviously the state of playback, pause or no playback. One can further skip a track ahead or go one back and set the volume with the slider or buttons. For sychronisation reasons there is also a button that gets the current state from the server and sets the interface therefore.
One could use a local thread and "mpc wait" (should be the command) which returns once the next song is selected and allows updating the current song string even if not update has been explicitly requested by broadcasting it. This has not been implemented though for concurrency reasons (The thread might commence and interrupt another threads state, thus crashing the clients).
One could also use the unused type "double" to set the playing song's approximate position of time.

The application requires one to have set up mpd and mpc such that mpc can be called without any extraneous information. If that is not possible then a change to commands is necessary. Further it will require a loaded playlist or a playlist named 'default' to exist (This will be loaded on application startup).

The application's view should be set in most cases on all clients to the same view.

As this is application uses mpc for communication one can disconnect from the server and it will continue playing, even when changing to other applications. There is no change to the state of mpd upon disconnect/changing to another application.

A small how-to on Raspbian:
- sudo apt-get install mpd mpc tmux
- copy over mpd.conf to /etc/
- sudo service mpd restart
- mkdir /home/pi/piremote/music
- mpc update
- mpc ls | mpc add
- mpc save default
- run the server (e.g. java -jar piRemoteServer.jar)
- Connect to the server with the app
- Enjoy :)
