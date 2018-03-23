package ana.sniffer;

import java.util.HashMap;
import java.util.List;

/**
 * Created by anast on 25/02/2018.
 */

public class ThroughputChartData {
    public HashMap<String, Float> throughputData;

    public ThroughputChartData(){
        this.throughputData = new HashMap<String, Float>();
    }

    public void addThroughputToApp(Throughput tp){
        float value = tp.nrPacketsInterval/tp.totalNrOfPackets;
        this.throughputData.put(tp.appName, value);
    }


}
