package ana.sniffer;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class AppsSelect extends AppCompatActivity {

    MyApplication myapp;
    ListView lv;
    List<String> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_select);

        myapp = (MyApplication)getApplication();
        apps = new ArrayList<String>();

        final PackageManager pm = getPackageManager();

        List<ApplicationInfo> appInfo = new ArrayList<ApplicationInfo>();


        for(String key: myapp.ffcd.freqCountPerApp.keySet())
        {
            apps.add(key);
            Log.d("AppName ", key);
        }


        lv = (ListView)findViewById(R.id.appList);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, apps);

        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(AppsSelect.this, AnalysisActivity.class);
                intent.putExtra("app_name", ((TextView)view.findViewById(android.R.id.text1)).getText());
                startActivity(intent);
            }
        });
    }


}
