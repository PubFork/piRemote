# PiRemote

## 1 Introduction

PiRemote is a simple API for building remote control-like applications, allowing one or multiple Android devices to control a server running on a Raspberry Pi or a PC. It was designed to serve simple purposes that one would expect from ordinary remote controls. PiRemote is not suitable for tasks such as streaming data across devices, transferring large files or running tasks with high load or runtime.

We provide a few sample applications to illustrate the purpose and the functionality of our software:
- TrafficLight tests the network communication and consistency.
- VideoApplication plays music and videos and provides basic remote control functionality, including speed change and subtitle toggling. It requires the server to run Linux with a graphical environment and either omxplayer or mplayer.
- MusicApplication plays music and manages a playlist. It requires the server to run Linux with mpd and mpc.
- RadioPi uses a pin of the Raspberry Pi to stream music over FM. Note that using this application may not be legal in some countries.
- ImageApplication displays a slideshow on the main screen. Users can navigate back and forth. It requires the server to run Linux with a graphical environment and geeqie.

In the following sections we will explain how to get PiRemote running and how to write your own PiRemote applications.

## 2 Compiling PiRemote
PiRemote consists of 3 parts: shared, server and client. We use IntelliJ Idea for shared and server and Android Studio for client.
- Setting up the IntelliJ Idea projects: After import, go to File -> Project Structure. We use Project SDK 1.8 and language level 7.
- Setting up the Android Studio project: After import, go to File -> Project Structure and verify that Sdk Version is API 18 or higher. Source and Target Compatibility should be at least 1.7.

Once you have all three projects set up, your are ready to compile:
- First, compile piRemoteShared. Build the piRemoteShared.jar artifact. In IntelliJ:
	- Setup your IntelliJ project (do this once): File -> Project Structure -> Artifacts -> '+' -> JAR -> From modules with dependencies... -> OK -> Apply -> OK
	- Every time an update is required: Build -> Build Artifacts... -> Build
	- The .jar can be found in "./out/artifacts/piRemoteShared_jar/piRemoteShared.jar"
- Then, this file needs to be included in the two projects:
	- IntelliJ (server): File -> Project Structure -> Libraries -> '+' -> Java -> Select JAR -> OK -> Apply -> OK
	- Android Studio (client): Create directory "libs" in "app" (./app/libs/), copy JAR there (it should be autodetected).
- You may now run the server on your PC or build the piRemoteServer.jar artifact as in the first step. On the Raspberry Pi you may run the jar file using: java -jar piRemoteServer.jar

## 3 Our Sample Applications 
### 3.1 TrafficLight 
TrafficLightApplication does not do anything useful. It was built to test the protocol in the first place. You might find it useful for verifying connectivity and consistency across all devices.

- Colors (red / orange / green) should be consistent within seconds across all connected devices.
- The file name indicated to the right of the file picker is broadcasted once at every pick action. Devices connecting at a later point will not receive the broadcast.

### 3.2 VideoApplication
The VideoApplication is a wrapper for omxplayer and mplayer. It can be used to play videos (ideally h264) and music. VideoApplication runs both on standard Linux PCs and Raspberry Pi. When opening a file, it looks for /usr/bin/omxplayer. If not found, it assumes that it is running on a PC and falls back to /usr/bin/mplayer.

Features (the following commands can be sent to both supported players):
- Play / Pause / Stop
- Seek back / forward (2 step sizes)
- Volume increase / decrease
- Speed increase / decrease
- Toggle subtitles (on / off)

In order to get the VideoApplication running, you have to do the following steps:
- Install either omxplayer or mplayer (for best performance, use omxplayer on Raspberries and mplayer on PCs). The executables must be located at /usr/bin/ (if necessary, create a symlink or change the path in piRemoteServer -> VideoApplication.java -> startProcess()).
- If you want to use VideoApplication with omxplayer, create a text file ~/.omxplayer (meaning: create a file called .omxplayer in the home folder of the user that will run PiRemote). In the text file, paste the contents from appendix A.
- If you want to use VideoApplication with mplayer, create a key config file for mplayer in the default location, typically ~/.mplayer/input.conf (~/ being the home folder of the user that will run PiRemote). In the text file, paste the contents from appendix B.
- Above config file bind the players' keys to a schema that PiRemote can talk to. If you want to use different locations, you need to modify the source code.

#### Known issues
- A client connecting to the server after playback has started will not see the filename of the media currently being played. This is normal.
- On Raspberry Pi 1 omxplayer takes a while to start up. Please give it some time (up to several seconds).
- mplayer seems to crash after a while when a large file is played. If you have a solution, please send us a pull request.

#### VideoApplication Appendix A: ~/.omxplayer
In order to get this running with omxplayer, you MUST create a file ~/.omxplayer with the following content (note that PAUSE: has a space behind it):
     DECREASE_SPEED:1
     INCREASE_SPEED:2
     REWIND:<
     FAST_FORWARD:>
     SHOW_INFO:z
     PREVIOUS_AUDIO:j
     NEXT_AUDIO:k
     PREVIOUS_CHAPTER:i
     NEXT_CHAPTER:o
     PREVIOUS_SUBTITLE:n
     NEXT_SUBTITLE:m
     TOGGLE_SUBTITLE:s
     DECREASE_SUBTITLE_DELAY:d
     INCREASE_SUBTITLE_DELAY:f
     EXIT:q
     PAUSE:
     DECREASE_VOLUME:-
     INCREASE_VOLUME:+
     SEEK_BACK_SMALL:x
     SEEK_FORWARD_SMALL:c
     SEEK_BACK_LARGE:y
     SEEK_FORWARD_LARGE:v
     STEP:p
     TOGGLE_SUBTITLE:s

#### VideoApplication Appendix B: ~/.mplayer/input.conf
     ' seek +30
     " seek -30
     & seek +600
     % seek -600

### 3.3 MusicApplication
The MusicApplication is basically an extension to 'mpc' (a media player client for 'mpd', the music player daemon) in which we send known ints/strings from the client via button presses to the server and therefore execute mpc locally and control mpd that way.

The MusicApplication allows reading the current playlist, showing the currently playing song, read the current volume, get state information on the playback modifiers "shuffle", "loop", "single", and obviously the state of playback, pause or no playback. One can further skip a track ahead or go one back and set the volume with the slider or buttons. For synchronization reasons there is also a button to get the current state from the server and update the interface accordingly.
One could use a local thread and "mpc wait" (should be the command) which returns as soon as the next song is selected and allows updating the current song string even if not update has been explicitly requested by broadcasting it. This has not been implemented though for concurrency reasons. (The thread might commence and interrupt another threads state, thus crashing the clients).

One could also use the unused type "double" to set the playing song's approximate position of time.

The MusicApplication requires one to have set up mpd and mpc such that mpc can be called without any extraneous information. If that is not possible a change to commands is necessary. Further it will require a loaded playlist or a playlist named 'default' to exist (which will be loaded on application startup).

The MusicApplication's view should be set in most cases on all clients to the same view.

As the MusicApplication uses mpc for communication one can disconnect from the server and it will continue playing, even when changing to other applications. There is no change to the state of mpd upon disconnect/changing to another application.

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

### 3.4 RadioPi
With the RadioPiApplication a client is able to broadcast a song from the Raspberry Pi via radio.
Please note, that the Raspberry Pi has to be set up with the right executable beforehand. You can use the included binary for the Pi2 or compile it yourself from the included sources for the Pi1.
At the location where you run the server, create a subfolder called radiopi2473/ where you put the binary pifm executable and also the encoded music files.
For more information about the transmitter see http://www.icrobotics.co.uk/wiki/index.php/Turning_the_Raspberry_Pi_Into_an_FM_Transmitter or the README.
Also please don't create interferences with license broadcasters and make sure you are familiar with government regulations about radio emissions.

You can pick the file you want to play in the client app. By pressing the broadcast button, you select the frequency you want to transmit on.
Play starts and stop stops the transmission, respectively. The rest happens in the background.

### 3.5 ImageApplication
With the ImageApplication a client is able to show images on the Raspberry Pi. Please note, that the Raspberry Pi has to be connected to a external screen. The PiRemote Server must be started from the graphical environment (not ssh).
The Raspberry Pi itself uses the ‘geeqie image viewer’ to display the images. For more information about ‘geeqie’ see http://geeqie.sourceforge.net

For the best experience, users should pick the first image in a folder. Then they can navigate through the images with the arrows on the app.

On slow devices such as the Raspberry Pi 1, requests can queue up if buttons are pressed to fast. PiRemote will not be responsive until the queue is processed completely.

## 4 How To Write An Application Using The PiRemote API
### 4.1 Introduction To The PiRemote API
PiRemote provides a simple API for writing remote control-like applications. You can write a program allowing you to control your Raspberry Pi or Linux PC from one or multiple Android devices. A PiRemote application consists of a ServerApplication and a ClientApplication. The server runs on your Pi or PC, the client is part of your PiRemote Android App. The PiRemote Core takes care of network communication and state consistency such that you can focus on the actual use of your remote.

Every application has its own ApplicationState when it is running. PiRemote guarantees that all connected devices will have a consistent view of this state within a few seconds. You can use ApplicationState to communicate fundamental state information, for example whether or not a slideshow is running (you might want to hide controls on your client when no slideshow is running).

In order to communicate between your server and client you can send ints, doubles and Strings. It's very useful to define int constants in the Shared part which is included in both the Java (server) and the Android (client) projects. Those constants are just send once when you fire them and PiRemote will not ensure consistency.

There is a tool for browsing files and directories on the server, called the File Picker. A file pick is initiated by your server application and happens in the Core part (hidden from your application) until a file was picked by the client. Then, the server application will be notified. By default, the File Picker does not show hidden files. To change this behaviour, go to piRemoteServer -> src -> core -> AbstractApplication -> pickFile(...) -> [...].makeOffer([...], false); and change the last false to true.

In order to implement your own application follow these steps:

- piRemoteShared:
   - In ApplicationCsts, create an enum that implements ApplicationState and define your ApplicationState constants there.
   - Still in ApplicationCsts, define any integer constants that you will use to communicate additional information, e.g. you might want to define a PLAY_BUTTON_PRESSED constant that the client sends to the server. For convention, we recommend that you start your constants with the name of your application.
   - In CoreCsts -> ServerState, register your application by adding a new constant to the enum. The constant comes with a String which will be displayed at the application chooser.
   - Now build the artifact piRemoteShared.jar and import it in piRemoteServer and piRemoteClient (see the section about compiling for instructions).

- piRemoteServer and piRemoteClient: First, make sure that you have imported the artifact from the step above. Then, follow the instructions below to implement your server and client part, respectively.

### 4.2 PiRemote Network Communication
The communication between the server (i.e. the Raspberry Pi) and the client (i.e. Android application) is stateless and happens with UDP-packets.
The network works in a simple way:

- The Dispatcher-Thread receives the messages over a socket. It manages the messages and either directly sends some messages back to the client/server or forwards the message to the client.
- The KeepAlive-Thread continuously sends messages to the client/server and recognizes when a client/server does not respond within a certain time.
- The Sender-Thread is in charge of picking messages from a queue and sending them to the determined recipient.

In fact, the network is actually hidden from the user. To send a message to the server, the application goes over the core by calling a function sendMessage().

### 4.3 Writing The Server Application
Again: Make sure that you have imported the piRemoteShared.jar containing your constants as described above.

- Create a new Java class in piRemoteServer -> src -> application with the name of your application.

- In core -> ApplicationFactory, add your ServerState constant. Just take the code from an existing application and adjust it for your constant / class name.

- Your new class must extend AbstractApplication. Implement all necessary methods:
 - onApplicationStart(): Called right after creation of the application. You must set the initial application state here using changeApplicationState (see below).
 - onApplicationStop(): Called right before the destruction of the application. Place your last words here.
 - onApplicationStateChange(ApplicationState newState): Called right before the application switches to another state. Within this method, getApplicationState() holds the old state and newState the new one.
 - onFilePicked(File file, UUID senderUUID): Called when the FilePicker on a client sent a file pick message on a file (not a directory). The UUID of the sender is given as an argument and can be used to reply something to that particular client.
 - onReceiveInt / -Double / -String(T content, UUID senderUUID): Called when a message arrived. For example, onReceiveInt(int i, UUID, senderUUID) is called on the server after sendInt(...) was called on a client. The UUID of the sender is given as an argument and can be used to reply something to that particular client.

- In order to talk to your client, you may use the following methods provided by AbstractApplication:
 - getApplicationState(): This returns the current ApplicationState on the server. Within the method onApplicationStateChange(...) this returns the old ApplicationState.
 - changeApplicationState(ApplicationState newState): Use this to ask the core to change the ApplicationState. All connected clients will be informed and onApplicationStateChange(...) will be called both on the server and on every client.
 - pickFile(String path, UUID destination): This shall be called by the server application to initiate a FilePick scenario on a particular client. path: Root path (must be a directory) to start the file pick with. destination: UUID of the client to send the offer to. Consider the following example scenario: You set the client up to send your constant CHOOSE_VIDEO_BUTTON_PRESSED when the user wants to pick a video to play. Then this constant is received on the server in onReceiveInt which also delivers the UUID of the sender. You can now make the File Picker appear on the client using this function and giving it a start path and the UUID you got from onReceiveInt(...).
 - closeFilePicker(UUID destination): Should be called to make the file pick overlay disappear in the client's UI
 - sendInt / -Double / -String(T content [, UUID destinationUUID] ): Use these 6 methods to send information to the client. There is now guarantee that the message will be delivered. If you omit the destinationUUID, your message will be delivered to all connected clients.

Do not override other methods in AbstractApplication unless you know what you are doing. Nevertheless, feel free to define your own fields, methods etc. You can spawn new threads and create further classes, but make sure to avoid data races.

### 4.4 Writing The Client Application
Again: Make sure that you have imported the piRemoteShared.jar containing your constants as described above.

- Create a new activity in the "application" package and let it inherit from the AbstractClientActivity.

- In core -> AbstractClientActivity -> startAbstractActivity(), add your ServerState constant to both switch statements. Just take the code from an existing application and adjust it for your constant / class name.

- In your new class, override all abstract methods from AbstractClientActivity:
	- onApplicationStateChange(ApplicationState newState): Called just before an application switches to another state. Update the UI. The application has access to both, the old (applicationState field) and the new ApplicationState (given as the argument). There is no need to update ApplicationState in onApplicationStateChange() since the Core will do it after this method returns.
	- onReceiveInt(int i): Called when the server sends an IntMessage.
	- onReceiveDouble(double d): As above for double.
	- onReceiveString(String str): As above for String.

- Implement protected void onCreate(Bundle savedInstanceState) which gets called when your ClientActivity starts up.

- Implement the application to your liking just like you normally would. You may use the following methods provided by AbstractClientActivity to talk to your server app:
	- sendInt(int i): Use to send the integer i to the server
	- sendDouble(double d): As above for double.
	- sendString(String str): As above for String.

Unless you know what you are doing, do not override other methods in AbstractClientActivity.
