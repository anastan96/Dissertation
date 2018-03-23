package ana.sniffer;

/**
 * Created by anast on 25/02/2018.
 */

public class Throughput {
    String appName;
    int nrPacketsInterval;
    long totalNrOfPackets;

    public boolean equals(Object obj){
        Throughput tp = (Throughput)obj;
        if(!this.appName.equals(tp.appName))
            return false;

        if(this.nrPacketsInterval != tp.nrPacketsInterval)
            return false;

        if(this.totalNrOfPackets != tp.totalNrOfPackets)
            return false;

        return true;
    }
}
