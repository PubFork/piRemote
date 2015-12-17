HOW TO WRITE A CLIENT APPLICATION:

1. Create a new activity and let it inherit from the AbstractClientActivity.
2. Override all abstract functions: 
	a. onApplicationStateChange(ApplicationState newState)
	b. onReceiveInt(int i)
	c. onReceiveDouble(double d)
	d. onReceiveString(String str)
3. Implement the application to your liking just like you normally would.