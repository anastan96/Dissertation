package ana.sniffer;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by anast on 08/01/2018.
 */

public class Netdump {
    public Netdump()
    {
        File f = new File("/sdcard/netdump/");
        if(f.isDirectory() && f.exists())
            return;

        createStorageDir();
    }

    public static void startDump(boolean append){
        final String path = "/sdcard/netdump/";

        if(!append)
            Shell.SU.run("busybox netstat -pnt | grep tcp > " + path + "netstat");
        else
            Shell.SU.run("busybox netstat -pnt | grep tcp >> " + path + "netstat");
    }

    private void createStorageDir()
    {
        List<String> out = Shell.SU.run("mkdir -p /sdcard/netdump/");
    }
}
