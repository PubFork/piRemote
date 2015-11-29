package NetworkConstants;

public class NetworkConstants {
    public static final long INTERVAL = 1500;
    public static final long ALLOWED_DROPS = 4;
    public static final long TIMEOUT = ALLOWED_DROPS * INTERVAL;

    public static final int PACKETSIZE = 8000;
}
