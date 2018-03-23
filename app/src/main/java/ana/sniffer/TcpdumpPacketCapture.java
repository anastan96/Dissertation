package ana.sniffer;

/**
 * Created by anast on 21/12/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class TcpdumpPacketCapture {
    private static Activity activity;
    private static Shell.Interactive rootTcpdumpShell;
    private static ProgressDialog progressBox;
    private static boolean isInitialised = false;
    public static void initialiseCapture(Activity _activity) {
        activity = _activity;
        progressBox = new ProgressDialog(activity);
        progressBox.setTitle("Initialising Capture");
        progressBox.setMessage("Please wait while packet capture is initialised...");
        progressBox.setIndeterminate(true);
        progressBox.setCancelable(false);
        progressBox.show();
        if (rootTcpdumpShell != null) {
            if(!isInitialised)
                throw new RuntimeException("rootTcpdump shell: not null, initialized:false");
            startTcpdumpCapture();
            progressBox.dismiss();
        }
        else {
            rootTcpdumpShell = new Shell.Builder().
                    useSU().
                    setWantSTDERR(false).
                    setMinimalLogging(true).
                    open(new Shell.OnCommandResultListener() {
                        @Override
                        public void onCommandResult(int commandVal, int exitVal, List<String> out) {
                            //Callback checking successful shell start.
                            if (exitVal == Shell.OnCommandResultListener.SHELL_RUNNING) {
                                isInitialised = true;
                                progressBox.setMessage("Starting packet capture..");
                                startTcpdumpCapture();
                                progressBox.dismiss();
                            }
                            else {
                                progressBox.setMessage("There was an error starting root shell. Please grant root permissions or try again.");
                            }
                        }
                    });
        }
    }

    private static void startTcpdumpCapture() {
        try{
            List<String> out = Shell.SU.run("ps | grep tcpdump.bin");
            if(out.size() > 0){
                return;
            }

            Shell.SU.run("/data/data/ana.sniffer/files/tcpdump.bin -w /data/data/ana.sniffer/files/Captures/" + Utils.getCurrentTimeStamp() + ".pcap");
        }
        catch(Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static int stopTcpdumpCapture(Activity _activity){
        int retVal = 0;

        try{
            List<String> out = Shell.SU.run("ps | grep tcpdump.bin");

            for(String x : out) {
                String[] temp = x.split("\\s+");
                Integer pid =  Integer.valueOf(temp[1]);
                Log.d("AppName: ", pid + " -pid");
                List<String> exitOutput =  Shell.SU.run("kill -s 1 " + pid.toString());

            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            //retVal = -1;
            throw ex;
        }

        if(new File(Environment.getExternalStorageDirectory().toString() + "/networkTraffic.pcap").exists())
            Log.d("AppName", Environment.getExternalStorageDirectory().toString()+ "/networkTraffic.pcap FOUND");

        if(new File(Environment.getExternalStorageDirectory().toString() + "/networkTraffic.pcap").canRead())
            Log.d("AppName", Environment.getExternalStorageDirectory().toString() + "/networkTraffic.pcap READABLE");
        else
            Log.d("AppName", Environment.getExternalStorageDirectory().toString() + "/networkTraffic.pcap NOT READABLE");

        return retVal;

    }
}
