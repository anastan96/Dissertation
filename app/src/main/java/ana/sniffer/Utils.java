package ana.sniffer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anast on 13/02/2018.
 */

public class Utils {
    public static void setupCapturesStorage()
    {
        File f = new File("/data/data/ana.sniffer/files/Captures/");
        if(!f.exists())
        {
            f.mkdirs();
        }
    }

    public static String getLocalIP(Context cx){
        WifiManager wm =(WifiManager) cx.getSystemService(Context.WIFI_SERVICE);
        String localIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return localIP;
    }

    public static String getCurrentTimeStamp(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        return ts;
    }
}
