NETWORK:

The communication between the server (i.e. the Raspberry Pi) and the client (i.e. Android application) is stateless and happens with UDP-packets. 
The network works in a simple way:

	1. The Dispatcher-Thread receives the messages over a socket. It manages the messages and either directly send some messages back to the client/server or forward the message to the client.
	
	2. The KeepAlive-Thread continuously sends messages to the client/server and recognises when a client/server does not respond within a certain time.

	3. The Sender-Thread is responsible to send the messages to the corresponding client. It keeps picking messages from a queue and sends it.


In fact, the network is actually hidden from the user. To send a message to the server, the application goes over the core by calling a function sendMessage().