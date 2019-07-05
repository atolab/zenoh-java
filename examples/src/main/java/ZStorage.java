import io.zenoh.*;
import io.zenoh.swig.*;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;

class ZStorage extends Storage {

    private Map<String, ByteBuffer> stored = new HashMap<String, ByteBuffer>();

    public void subscriberCallback(String rname, ByteBuffer data, DataInfo info) {
        System.out.println("Received data: " + rname);
        this.stored.put(rname, data);
    }

    public z_array_z_resource_t queryHandler(String rname, String predicate) {
        System.out.println("Handling Query: " + rname);

        return null;

    }

    public void repliesCleaner(z_array_z_resource_t replies) {
        System.out.println("Cleaning Replies.");
    }



    public static void main(String[] args) {
        String locator = "tcp/127.0.0.1:7447";
        if (args.length > 0) {
            locator = args[0];
        }

        String uri = "/demo/**";
        if (args.length > 1) {
            uri = args[1];
        }

        try {
            System.out.println("Connecting to "+locator+"...");
            Zenoh z = Zenoh.open(locator);

            System.out.println("Declaring Storage: "+uri);
            z.declareStorage(uri, new ZStorage());

            while (true) {    
                Thread.sleep(1000);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
