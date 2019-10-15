import io.zenoh.*;

import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.List;

class ZStorage implements StorageHandler {

    private Map<String, ByteBuffer> stored = new HashMap<String, ByteBuffer>();

    public void handleData(String rname, ByteBuffer data, DataInfo info) {
        System.out.printf(">> [Storage listener] Received ('%20s' : '%s')\n", rname, data.toString());
        this.stored.put(rname, data);
    }

    public void handleQuery(String rname, String predicate, RepliesSender repliesSender) {
        System.out.printf(">> [Query handler   ] Handling '%s?%s'\n", rname, predicate);

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

        String uri = "/demo/example/**";
        if (args.length > 1) {
            uri = args[1];
        }

        try {
            System.out.println("Connecting to "+locator+"...");
            Zenoh z = Zenoh.open(locator);

            System.out.println("Declaring Storage on '"+uri+"'...");
            Storage s = z.declareStorage(uri, new ZStorage());

            InputStreamReader stdin = new InputStreamReader(System.in);
            while ((char) stdin.read() != 'q');

            s.undeclare();
            z.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
