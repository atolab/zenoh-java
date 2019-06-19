import io.zenoh.*;

class ZPubThr {

    public static void main(String[] args) {
        String locator = "tcp/127.0.0.1:7447";
        if (args.length < 1) {
            System.out.println("USAGE:\n\tZPubThr <payload-size> [<zenoh-locator>]\n\n");
            System.exit(-1);
        }

        int len = Integer.parseInt(args[0]);
        if (args.length > 1) {
            locator = args[1];
        }

        java.nio.ByteBuffer data = java.nio.ByteBuffer.allocateDirect(len+8);
        Vle.encode(data, len);
        for (int i = 0; i < len; ++i) {
            data.put((byte) (i%10));
        }

        try {
            Zenoh z = Zenoh.open(locator);
            Publisher pub = z.declarePublisher("/test/thr");

            while (true) {
                pub.streamData(data);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
