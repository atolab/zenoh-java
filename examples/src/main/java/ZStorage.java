import io.zenoh.*;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.List;

class ZStorage implements StorageCallback {

    private Map<String, ByteBuffer> stored = new HashMap<String, ByteBuffer>();

    public void subscriberCallback(String rname, ByteBuffer data, DataInfo info) {
        System.out.println("Received data: " + rname);
        this.stored.put(rname, data);
    }

    public void queryHandler(String rname, String predicate, RepliesSender repliesSender) {
        System.out.println("Handling Query: " + rname);

        List<Resource> replies = new Vector<Resource>();
        for (Map.Entry<String, ByteBuffer> entry : stored.entrySet()) {
            if (Rname.intersect(rname, entry.getKey())) {
                replies.add(new Resource(entry.getKey(), entry.getValue(), 0, 0));
            }
        }
        repliesSender.sendReplies(replies.toArray(new Resource[replies.size()]));
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
