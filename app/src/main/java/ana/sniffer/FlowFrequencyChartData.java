package ana.sniffer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by anast on 21/02/2018.
 */

public class FlowFrequencyChartData {

    public HashMap<String, List<FlowFrequency>> freqPerApp;
    public HashMap<String, List<Integer>> freqCountPerApp;


    public FlowFrequencyChartData(){
        this.freqPerApp = new HashMap<String, List<FlowFrequency>>();
        this.freqCountPerApp = new HashMap<String, List<Integer>>();
    }

    public FlowFrequencyChartData(JSONArray ja)
    {
        freqCountPerApp = new HashMap<String, List<Integer>>();
        List<Integer> counts;


        for(int i=0; i<ja.length(); i++){
            try {
                JSONObject jo = ja.getJSONObject(i);
                String countStr[] = jo.getString("frequency_counts").split(" ");
                counts = new ArrayList<Integer>();

                for(int j=0; j<countStr.length; j++)
                    counts.add(Integer.parseInt(countStr[j]));

                freqCountPerApp.put(jo.getString("app_name"), counts);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addFlowFrequencyToApp(String appName, FlowFrequency fq)
    {
        List<FlowFrequency> tmp = this.freqPerApp.get(appName);
        tmp.add(fq);
        this.freqPerApp.put(appName, tmp);
    }

    public void addApp(String appName)
    {
        if(!freqPerApp.containsKey(appName)){
            freqPerApp.put(appName, new ArrayList<FlowFrequency>());
        }
    }


    public JSONArray toJSON()
    {
        JSONArray ja = new JSONArray();
        JSONObject jo;
        for (String key: freqCountPerApp.keySet()) {
            List<Integer> counts = freqCountPerApp.get(key);
            String countStr = "";
            for (int i = 0; i < counts.size(); i++)
                countStr += counts.get(i) + " ";

            jo = new JSONObject();
            try {
                jo.put("app_name", key);
                jo.put("frequency_counts", countStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ja.put(jo);
        }

        return ja;
    }

    public void countFrequenciesPerApp(){
        for (String key: freqPerApp.keySet()){
            int c = 1;
            List<FlowFrequency> counts = freqPerApp.get(key);
            List<Integer> countFrequencies = new ArrayList<Integer>();

            while(!counts.isEmpty())
            {
                c = Collections.frequency(counts, counts.get(0));
                Log.d("AppName: ", c + " -count frequencies");
                counts.removeAll(Collections.singleton(counts.get(0)));
                countFrequencies.add(c);
            }
            freqCountPerApp.put(key, countFrequencies);
        }
    }

}
