RADIO_PI:

With the RadioPi Application a client is able to broadcast a song from the RaspberryPi via radio.
Please note, that the RaspberryPi has to be set up with the right executable beforehand. You can use the included binary for the Pi2 or compile it yourself from the included sources for the Pi1.
At the location where you run the server, create a subfolder called radiopi2473/ where you put the binary pifm executable and also put there the encoded music files.
For more informations about the transmitter see http://www.icrobotics.co.uk/wiki/index.php/Turning_the_Raspberry_Pi_Into_an_FM_Transmitter or the README.
Also please don't create interferences with licenses broadcasters and make sure you are familiar with government regulations about radio emissions.

In the client app, you can pick the file you want to play. By pressing the broadcast button, you select the frequency you want to transmit on.
Play starts the transmission, stop stops its. The rest happens in the background.