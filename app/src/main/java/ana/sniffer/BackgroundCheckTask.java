package ana.sniffer;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by anast on 29/01/2018.
 */

public class BackgroundCheckTask{
    static HashMap<Long, String> backgroundCheck = new HashMap<Long, String>();
    static HashMap<String, List<Long>> foregroundApp = new HashMap<String, List<Long>>();

    public static String getTopAppName(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String strName = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                strName = getLollipopFGAppPackageName(context);
            } else {
                strName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strName;
    }

    private static void isBackground(){
        List<Long> timestamps;
        for (Iterator it = backgroundCheck.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Long key = (Long)entry.getKey();
            String value = entry.getValue().toString();
            for(Iterator iterator = LinkPacketsToPackage.appName.entrySet().iterator();iterator.hasNext();){
                Map.Entry entry2 = (Map.Entry) iterator.next();
                String value2 = entry2.getValue().toString();
                if(value.equals(value2)){
                    if((timestamps = foregroundApp.get(value2)) == null){
                        timestamps = new ArrayList<Long>();
                    }
                    timestamps.add(key);
                    foregroundApp.put(value2, timestamps);
                    backgroundCheck.remove(key);
                }
            }
        }
    }


    private static String getLollipopFGAppPackageName(Context ctx) {

        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService("usagestats");
            long milliSecs = 5 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, date.getTime() - milliSecs, date.getTime());
            if (queryUsageStats.size() > 0) {
                Log.i("LPU", "queryUsageStats size: " + queryUsageStats.size());
            }
            long recentTime = 0;
            String recentPkg = "";
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                if (i == 0 && !"ana.sniffer".equals(stats.getPackageName())) {
                    Log.i("LPU", "PackageName: " + stats.getPackageName() + " " + stats.getLastTimeStamp());
                    backgroundCheck.put(stats.getLastTimeStamp(), stats.getPackageName());

                }
                if (stats.getLastTimeStamp() > recentTime) {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }
            }
            return recentPkg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
