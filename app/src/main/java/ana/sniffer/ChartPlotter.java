package ana.sniffer;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ChartPlotter extends AppCompatActivity {

    String contentType;
    RelativeLayout rl;
    View chart = null;
    MyApplication myApp;
   // FlowFrequencyChartData flowFrequencyChartData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_plotter);

        myApp = (MyApplication)getApplicationContext();

        rl = (RelativeLayout) findViewById(R.id.rl);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        contentType = getIntent().getStringExtra("content");

       if(contentType.equals("flow_frequency")){
            chart = new BarChart(getApplicationContext());
         }
         else if(contentType.equals("throughput"))
         {
             chart = new BarChart(getApplicationContext());
         }
         else if(contentType.equals("quantity")){
             chart = new BarChart(getApplicationContext());
         }

         /*
        try {
            data = new JSONArray(getIntent().getStringExtra("jsonDATA"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        rl.addView(chart, lp);

        if(chart == null)
            Toast.makeText(getApplicationContext(), "Null chart!", Toast.LENGTH_LONG).show();

         else
             setData(getIntent().getExtras().getString("app_name"));
    }

    private void setData(String appName){
        contentType = getIntent().getStringExtra("content");
        if(contentType.equals("flow_frequency")){
            ArrayList<BarEntry> values = new ArrayList<>();

           // flowFrequencyChartData = new FlowFrequencyChartData();
            // myApp.ffcd = new FlowFrequencyChartData();

            Log.d("AppName: ", myApp.ffcd.freqCountPerApp.size() + " size from chart plotter ffcd");

            List<Integer> counts = myApp.ffcd.freqCountPerApp.get(appName);

            for(int i = 0; i < counts.size(); i++)
                values.add(new BarEntry(i, counts.get(i)));

            BarDataSet set = new BarDataSet(values, appName);
            set.setColors(ColorTemplate.MATERIAL_COLORS);
            set.setDrawValues(true);

            BarData barData = new BarData(set);
            ((BarChart)chart).setData(barData);
            chart.invalidate();
            ((BarChart)chart).animateY(500);
        } else if(contentType.equals("quantity")){
            ArrayList<BarEntry> values = new ArrayList<>();

            Long var1 = myApp.qcd.squantityPerApp.get(appName);
            Long var2 = myApp.qcd.rquantityPerApp.get(appName);

            if(var1 != null){
                values.add(new BarEntry(1, var1));}
            else {
                values.add(new BarEntry(1, 0));
            }

            if(var2 != null){
             values.add(new BarEntry(3, var2));}
            else {
                values.add(new BarEntry(3, 0));
            }

            BarDataSet set = new BarDataSet(values, appName);
            set.setColors(ColorTemplate.MATERIAL_COLORS);
            set.setDrawValues(true);

            BarData barData = new BarData(set);
            ((BarChart) chart).setData(barData);
            chart.invalidate();
            ((BarChart) chart).animateY(500);


        } else if(contentType.equals("throughput")){
            ArrayList<BarEntry> values = new ArrayList<>();

            values.add(new BarEntry(1, myApp.tcd.throughputData.get(appName)));

            BarDataSet set = new BarDataSet(values, appName);
            set.setColors(ColorTemplate.MATERIAL_COLORS);
            set.setDrawValues(true);

            BarData barData = new BarData(set);
            ((BarChart)chart).setData(barData);
            chart.invalidate();
            ((BarChart)chart).animateY(500);
        }

    }

    protected void plotChart()
    {
        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(0, 12));
        values.add(new BarEntry(5, 15));
        BarDataSet barDataSet = new BarDataSet(values,"App");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setDrawValues(true);

        BarData barData = new BarData(barDataSet);
        ((BarChart)chart).setData(barData);
        chart.invalidate();
        ((BarChart)chart).animateY(500);
    }
}
