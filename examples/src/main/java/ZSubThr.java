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
        public void handle(String rname, ByteBuffer data, DataInfo info) {
            if (count == 0) {
                start = System.currentTimeMillis();
                count ++;
            } else if (count < N) {
                count++;
            } else {
                stop = System.currentTimeMillis();
                printStats(start, stop);
                System.gc();
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
            Zenoh z = Zenoh.open(locator);
            Subscriber sub = z.declareSubscriber("/test/thr", SubMode.push(), new Listener());

            Thread.sleep(60000);

            sub.undeclare();
            z.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
