package ana.sniffer;

import java.net.InetAddress;

/**
 * Created by anast on 21/02/2018.
 */

public class FlowFrequency {
    String appName;
    String protocol;
    InetAddress src_address;
    InetAddress dst_address;
    int src_port;
    int dst_port;

    @Override
    public boolean equals(Object obj)
    {
        FlowFrequency ff = (FlowFrequency)obj;
        if(!this.appName.equals(ff.appName))
            return false;

        if(!this.protocol.equals(ff.protocol))
            return false;

        if(!this.src_address.equals(ff.src_address))
            return false;

        if(!this.dst_address.equals(ff.dst_address))
            return false;

        if(this.src_port != ff.src_port)
            return false;

        if(this.dst_port != ff.dst_port)
            return false;

        return true;
    }

    @Override
    public String toString() {
        return appName + " " + src_port;
    }
}
