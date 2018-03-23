package ana.sniffer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by anast on 15/01/2018.
 */

public class LinkPacketsToPackage {
    public static HashMap<String, String> addressAndName = new HashMap<String, String>();
    public static HashMap<String, String> appName = new HashMap<String, String>();

    public static void parseData() {
        //ParseTask pt = new ParseTask();
        //pt.execute();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/sdcard/netdump/netstat"));


            String line = br.readLine();

            while (line != null) {
                line = line.replaceAll("\\s+", " ");
                line = line.replaceAll("::ffff:", "");

                if(line.contains("/")) {
                    String output1[] = line.toString().split("/");  // numele
                    String output2[] = line.toString().split(" ")[4].split(":");    // adresa dest
                    String output3[] = line.toString().split(" ")[3].split(":");    // adresa dest

                    LinkPacketsToPackage.addressAndName.put(output2[0], output1[1]);
                    LinkPacketsToPackage.addressAndName.put(output3[0], output1[1]);
                    LinkPacketsToPackage.appName.put(output1[1], output1[1]);

                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

class ParseTask extends AsyncTask<Void, Void, Void>
{

    @Override
    protected Void doInBackground(Void... voids) {


        return null;
    }
}
