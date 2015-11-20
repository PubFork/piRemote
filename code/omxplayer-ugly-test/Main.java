import java.io.*;

/**
 * Created by sandro on 20.11.15.
 * Extremely ugly hack to check if I can manipulate omxplayer from Java (Answer: Yes I can)
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("/usr/bin/omxplayer","/home/pi/Sync/Musik/3 bc/Aaron Parks-Peaceful Warrior.mp3");
        builder.redirectErrorStream(true);
        Process omx = builder.start();
        if(omx!=null){
            OutputStream stdin = omx.getOutputStream ();
            InputStream stderr = omx.getErrorStream ();
            InputStream stdout = omx.getInputStream ();

            BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

            long t0,t1;
            t0=System.currentTimeMillis();
            do{
                t1=System.currentTimeMillis();
            }while (t1-t0<3000);

            for(int i=0;i<15;i++) {
                t0=System.currentTimeMillis();
                do{
                    t1=System.currentTimeMillis();
                }while (t1-t0<1000);
                if(i<7){
                    writer.write("-");
                    System.out.println("-");
                }else{
                    writer.write("+");
                    System.out.println("+");
                }
                writer.flush();
            }
            reader.close();
            writer.close();
            omx.waitFor();
        }
    }
}
