package ana.sniffer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog progressBox;
    HashMap<String, Long> squantityPerApp = new HashMap<String, Long>();;
    HashMap<String, Long> rquantityPerApp = new HashMap<String, Long>();;
    HashMap<String, Long> totalNumberOfPackets = new HashMap<String, Long>();
    MyApplication myapp;
    ListView capturesListView;
    File[] captureTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent srvI = new Intent(this, NetdumpService.class);
        startService(srvI);

        Utils.setupCapturesStorage();
        myapp = (MyApplication)getApplicationContext();

        Intent i = getIntent();

        Log.d("AppName", Environment.getExternalStorageDirectory().toString());

        final Button bt = (Button) findViewById(R.id.addNewCapture);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCapture(v);
            }
        });

        progressBox = new ProgressDialog(this);
        progressBox.setTitle("Initialising...");
        progressBox.setMessage("Requesting root permissions...");
        progressBox.setIndeterminate(true);
        progressBox.setCancelable(false);
        progressBox.show();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Boolean isRootAvailable = Shell.SU.available();
                Boolean processExists = false;
                String pid = null;
                if(isRootAvailable){
                    List<String> out = Shell.SH.run("ps | grep tcpdump.bin");
                    if(out.size() == 1){
                        processExists = true;
                        pid = (out.get(0).split("\\s+"))[1];
                    }
                    else if(out.size() == 0) {
                        if (loadTcpdumpFromAssets() != 0)
                            throw new RuntimeException("Copying TCPDUMP binary failed!");
                    }
                    else
                        throw new RuntimeException("Searching for running process returned unexpected result.");
                }

                final Boolean processExistsFinal = processExists;
                final String pidFinal = pid;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!isRootAvailable) {
                            Toast.makeText(getApplicationContext(), "Root permission denied or phone is not rooted!", Toast.LENGTH_LONG).show();

                        }
                        else {
                            if(processExistsFinal){
                                Toast.makeText(getApplicationContext(), "Tcpdump already running at pid: " + pidFinal, Toast.LENGTH_LONG).show();
                                bt.setTag(1);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Initialization Successful.", Toast.LENGTH_LONG).show();
                                bt.setTag(0);
                            }
                        }
                    }

                });
                progressBox.dismiss();
            }
        };
        new Thread(runnable).start();

        File f = new File("/data/data/ana.sniffer/files/Captures/");
        captureTitle = f.listFiles();

        capturesListView = (ListView) findViewById(R.id.capturesListView);

        ArrayList<String> myListView = new ArrayList<String>();

        for(int j = 0; j < captureTitle.length; j++){
            String[] output = captureTitle[j].toString().split("/");
            myListView.add(output[6]);
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myListView);

        capturesListView.setAdapter(arrayAdapter);

        capturesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPcapFile();
                Intent intent = new Intent();
                intent.setClass(view.getContext(), AppsSelect.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_captures) {

        } else if (id == R.id.nav_info) {
            // Alert dialog
            showInfo("About", "To initialize the capturing of the network traffic click on 'start capturing'. The list of the captured files is on the main activity. Once you click on a file, you can check the charts that were built using the gathered data.");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startCapture(View v){
        Button bt = (Button)findViewById(R.id.addNewCapture);
        bt.setEnabled(false);
        if((int)bt.getTag() == 1){
            TcpdumpPacketCapture.stopTcpdumpCapture(this);

            bt.setText("Start capture");
            bt.setTag(0);
            Toast.makeText(getApplicationContext(), "Packet capture stopped. Output stored in sdcard/networkTraffic.pcap.", Toast.LENGTH_LONG).show();

        }
        else if ((int)bt.getTag() == 0){
            TcpdumpPacketCapture.initialiseCapture(this);
            bt.setText("Stop  Capture");
            bt.setTag(1);
        }
        bt.setEnabled(true);
    }

    private int loadTcpdumpFromAssets() {
        int retval = 0;

        String rootDataPath = getApplicationInfo().dataDir + "/files";
        String filePath = rootDataPath + "/tcpdump.bin";
        File file = new File(filePath);
        AssetManager assetManager = getAssets();

        try {
            if (file.exists()) {
                Shell.SH.run("chmod 755 " + filePath);
                return retval;
            }
            new File(rootDataPath).mkdirs();
            retval = copyFileFromAsset(assetManager, "tcpdump.bin", filePath);
            // Mark the binary executable
            Shell.SH.run("chmod 755 " + filePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            retval = -1;
        }
        return retval;
    }

    private int copyFileFromAsset(AssetManager assetManager, String sourcePath, String destPath) {
        byte[] buff = new byte[1024];
        int len;
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(sourcePath);
            new File(destPath).createNewFile();
            out = new FileOutputStream(destPath);
            // write file
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    void openPcapFile() {
        ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("Loading...");
        pd.setMessage("Parsing pcap file...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        myapp.ffcd = new FlowFrequencyChartData();
        PcapParser obj = new PcapParser();

        obj.parseFile();


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("AppName: ", "parseData");
        LinkPacketsToPackage.parseData();


        HashMap<String, Integer> numberOfpacketsInterval = new HashMap<String, Integer>();

        myapp.qcd = new QuantityCharData();
        if (PcapParser.tcppk == null)
            Toast.makeText(getApplicationContext(), "Error opening file.", Toast.LENGTH_LONG).show();
        else {
            for (int i = 0; i < PcapParser.tcppk.size(); i++) {
                TCPPacket tcpPacket = PcapParser.tcppk.get(i);
                try {

                    //Get quantity data
                    String destinationIP = tcpPacket.dst_ip.toString().replace("/", "");
                    if (!Utils.getLocalIP(getApplicationContext()).equals(destinationIP)) {
                        if(LinkPacketsToPackage.addressAndName.containsKey(destinationIP)){
                            String nameApp = LinkPacketsToPackage.addressAndName.get(destinationIP);
                            if (squantityPerApp.containsKey(nameApp)) {
                                long dataIP = squantityPerApp.get(nameApp) + (tcpPacket).data.length;
                                squantityPerApp.put(nameApp, dataIP);
                            } else {
                                squantityPerApp.put(nameApp, (long) (tcpPacket).data.length);
                            }
                        }
                    }

                    if (Utils.getLocalIP(getApplicationContext()).equals(destinationIP)) {
                        if(LinkPacketsToPackage.addressAndName.containsKey(destinationIP)){
                            String nameApp = LinkPacketsToPackage.addressAndName.get(destinationIP);
                            if (rquantityPerApp.containsKey(nameApp)) {
                                long dataIP = rquantityPerApp.get(nameApp) + (tcpPacket).data.length;
                                rquantityPerApp.put(nameApp, dataIP);
                            } else {
                                rquantityPerApp.put(nameApp, (long) (tcpPacket).data.length);
                            }
                        }
                    }

                    //Get flow frequency data
                    if(LinkPacketsToPackage.addressAndName.containsKey(destinationIP)){
                        String nameTCP = LinkPacketsToPackage.addressAndName.get(destinationIP);
                        myapp.ffcd.addApp(nameTCP);
                        FlowFrequency flowFrequency = new FlowFrequency();
                        flowFrequency.appName = nameTCP;
                        flowFrequency.dst_port = tcpPacket.dst_port;
                        flowFrequency.src_port = tcpPacket.src_port;
                        flowFrequency.dst_address = tcpPacket.dst_ip;
                        flowFrequency.src_address = tcpPacket.src_ip;
                        flowFrequency.protocol = "TCP";
                        myapp.ffcd.addFlowFrequencyToApp(nameTCP, flowFrequency);

                        //Get throughput data
                        if(totalNumberOfPackets.containsKey(nameTCP)){
                            long totalNumber = totalNumberOfPackets.get(nameTCP) + 1;
                            totalNumberOfPackets.put(nameTCP, totalNumber);
                        } else{
                            totalNumberOfPackets.put(nameTCP, (long) 1);
                        }

                        if(numberOfpacketsInterval.containsKey(nameTCP)){
                            int numberPackets = numberOfpacketsInterval.get(nameTCP) + 1;
                            numberOfpacketsInterval.put(nameTCP, numberPackets);
                        } else {
                            numberOfpacketsInterval.put(nameTCP, 1);
                        }
                    }

                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }

        if (PcapParser.udppk == null)
            Toast.makeText(getApplicationContext(), "Error opening file.", Toast.LENGTH_LONG).show();
        else {
            for(int i=0; i<PcapParser.udppk.size();i++){
                UDPPacket udpPacket = PcapParser.udppk.get(i);
                try{
                    String destinationIP = udpPacket.dst_ip.toString().replace("/", "");
                    //Get quantity data
                    if(!Utils.getLocalIP(getApplicationContext()).equals(destinationIP)){
                        if(LinkPacketsToPackage.addressAndName.containsKey(destinationIP)){
                            String nameApp = LinkPacketsToPackage.addressAndName.get(destinationIP);
                            if(squantityPerApp.containsKey(nameApp)){
                                long dataIP = squantityPerApp.get(nameApp) + (udpPacket).data.length;
                                squantityPerApp.put(nameApp, dataIP);
                            } else {
                                squantityPerApp.put(nameApp,(long)(udpPacket).data.length);
                            }
                        }}

                    if(Utils.getLocalIP(getApplicationContext()).equals(destinationIP)){
                        if(LinkPacketsToPackage.addressAndName.containsKey(destinationIP)){
                            String nameApp = LinkPacketsToPackage.addressAndName.get(destinationIP);
                            if(rquantityPerApp.containsKey(nameApp)){
                                long dataIP = rquantityPerApp.get(nameApp) + (udpPacket).data.length;
                                rquantityPerApp.put(nameApp, dataIP);
                            } else {
                                rquantityPerApp.put(nameApp, (long)(udpPacket).data.length);
                            }
                        }}
                    //Get flow frequency data
                    if(LinkPacketsToPackage.addressAndName.containsKey(destinationIP)){
                        String nameUDP = LinkPacketsToPackage.addressAndName.get(destinationIP);
                        myapp.ffcd.addApp(nameUDP);
                        FlowFrequency flowFrequency = new FlowFrequency();
                        flowFrequency.appName = nameUDP;
                        flowFrequency.protocol = "UDP";
                        flowFrequency.src_address = udpPacket.src_ip;
                        flowFrequency.src_port = udpPacket.src_port;
                        flowFrequency.dst_address = udpPacket.dst_ip;
                        flowFrequency.dst_port = udpPacket.dst_port;
                        myapp.ffcd.addFlowFrequencyToApp(nameUDP, flowFrequency);

                        //Get throughput data
                        if(totalNumberOfPackets.containsKey(nameUDP)){
                            long totalNumber = totalNumberOfPackets.get(nameUDP) + 1;
                            totalNumberOfPackets.put(nameUDP, totalNumber);
                        } else{
                            totalNumberOfPackets.put(nameUDP, (long) 1);
                        }

                        if(numberOfpacketsInterval.containsKey(nameUDP)){
                            int numberPackets = numberOfpacketsInterval.get(nameUDP) + 1;
                            numberOfpacketsInterval.put(nameUDP, numberPackets);
                        } else {
                            numberOfpacketsInterval.put(nameUDP, 1);
                        }
                    }
                }
                catch (ClassCastException e){
                    e.printStackTrace();
                }
            }

        }

        myapp.tcd = new ThroughputChartData();
        for(String key1: numberOfpacketsInterval.keySet()){
            for(String key2: totalNumberOfPackets.keySet()){
                if(key1.equals(key2)){
                    Throughput throughput = new Throughput();
                    throughput.appName = key1;
                    throughput.totalNrOfPackets = totalNumberOfPackets.get(key2);
                    throughput.nrPacketsInterval = numberOfpacketsInterval.get(key1);
                    myapp.tcd.addThroughputToApp(throughput);
                }
            }
        }

        myapp.ffcd.countFrequenciesPerApp();

        for(String key: myapp.ffcd.freqCountPerApp.keySet()){
            Log.d("AppName: ", key + "Main");
        }

        myapp.qcd.rquantityPerApp = this.rquantityPerApp;
        myapp.qcd.squantityPerApp = this.squantityPerApp;

        pd.dismiss();

    }

    private void showInfo(String title, String details)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
