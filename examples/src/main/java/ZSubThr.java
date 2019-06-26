import io.zenoh.*;
import io.zenoh.swig.*;

import java.nio.ByteBuffer;

class ZSubThr {
    
    private static final long N = 100000;

    private static long count = 0;
    private static long start;
    private static long stop;

    public static void printStats(long start, long stop) {
        float delta = stop - start;
        float thpt = N / (delta / 1000);
        System.out.format("%f msgs/sec%n", thpt);
    }

    private static class Listener implements SubscriberCallback {
        public void handle(z_resource_id_t rid, ByteBuffer data, z_data_info_t info) {
            if (count == 0) {
                start = System.currentTimeMillis();
                count ++;
            } else if (count < N) {
                count++;
            } else {
                stop = System.currentTimeMillis();
                printStats(start, stop);
                count = 0;
            }
        }
    }

    public static void main(String[] args) {
        String locator = "tcp/127.0.0.1:7447";
        if (args.length > 0) {
            locator = args[0];
        }

        try {
            System.out.println("Connecting to "+locator+"...");
            Zenoh z = Zenoh.open(locator);

            System.out.println("Declaring Subscriber...");
            z_sub_mode_t mode = new z_sub_mode_t();
            mode.setKind((short) 0x01);
            Subscriber sub = z.declareSubscriber("/test/thr", mode, new Listener());

            Thread.sleep(60000);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
