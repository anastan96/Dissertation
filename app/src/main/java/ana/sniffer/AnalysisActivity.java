package ana.sniffer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AnalysisActivity extends AppCompatActivity {

    ListView analysisListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        getSupportActionBar().setTitle("Chart selection");
        getSupportActionBar().setSubtitle("Select the type of chart that you want to be displayed");

        analysisListView = (ListView)findViewById(R.id.analysisListView);

        final ArrayList<String> choices = new ArrayList<String>();
        choices.add("Traffic sent and received");
        choices.add("Flow Frequency");
        choices.add("Throughput");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, choices);

        analysisListView.setAdapter(arrayAdapter);

        analysisListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        showInfo(choices.get(position), "Number of bytes sent/received");
                        break;
                    case 1:
                        showInfo(choices.get(position), "The flow frequency describes a number of unique flows for each application.  Each flow can be identified by certain values. For TCP and UDP packets the identifiers are: protocol, source IP, destination IP, source port and destination port.");
                        break;
                    case 2:
                        showInfo(choices.get(position), "The throughput describes how much information is processed in a given time.");
                    default:
                        showInfo("", "Try again");
                        break;
                }

                Log.d("AppName", "long pressed");
                Log.d("AppName", position+"");

                return true;
            }
        });

        analysisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(AnalysisActivity.this, ChartPlotter.class);

                switch (position)
                {
                    case 0:
                        i.putExtra("content", "quantity");
                        break;
                    case 1:
                        i.putExtra("content", "flow_frequency");
                        break;
                    case 2:
                        i.putExtra("content", "throughput");
                        break;
                }
                i.putExtra("app_name", getIntent().getExtras().getString("app_name"));
                startActivity(i);
            }
        });
    }

    private void showInfo(String title, String details)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AnalysisActivity.this);
        builder1.setTitle(title);
        builder1.setMessage(details);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
