package ana.sniffer;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by anast on 03/12/2017.
 */

public class NetdumpService extends Service {

    private static Timer timer = new Timer();
    private Context ctx;
    private Netdump ntd;
    public boolean rootAllowed = true;
    private long elapsed;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        rootAllowed = intent.getBooleanExtra("rootAllowed", true);

        Log.d("AppName", "Service Started");

        return Service.START_REDELIVER_INTENT;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = getApplicationContext();
        ntd = new Netdump();

        Netdump.startDump(false);
        startService();
    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            Log.d("AppName", "Service Task");

            Netdump.startDump(true);
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(ctx, (String)msg.obj, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
