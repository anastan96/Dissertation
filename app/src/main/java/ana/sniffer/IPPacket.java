package ana.sniffer;

/**
 * Created by anast on 15/01/2018.
 */

import java.net.*;

public class IPPacket extends Packet{

    public long timestamp;
    public long packetSize;

    public InetAddress src_ip;
    public InetAddress dst_ip;

    public byte[] data;

    public IPPacket(long timestamp, long packetSize){
        this.timestamp = timestamp;
        this.packetSize = packetSize;
    }
}
