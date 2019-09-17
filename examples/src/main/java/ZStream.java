import io.zenoh.*;

class ZStream {

    public static void main(String[] args) {
        String locator = "tcp/127.0.0.1:7447";
        if (args.length > 0) {
            locator = args[0];
        }

        String uri = "/demo/example/zenoh-java-stream";
        if (args.length > 1) {
            uri = args[1];
        }

        String value = "Stream from Java!";
        if (args.length > 2) {
            value = args[2];
        }

        try {
            System.out.println("Connecting to "+locator+"...");
            Zenoh z = Zenoh.open(locator, "user", "password");

            System.out.println("Declaring Publisher on '"+uri+"'...");
            Publisher pub = z.declarePublisher(uri);

            System.out.println("Streaming Data...");
            java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocateDirect(256);
            for (int idx = 0; idx < 100; ++idx) {
                Thread.sleep(1000);
                String s = String.format("[%4d] %s", idx, value);
                buf.put(s.getBytes("UTF-8"));
                System.out.printf("Streaming Data ('%s': '%s')...\n", uri, s);
                pub.streamData(buf);
                buf.rewind();
            }

            pub.undeclare();
            z.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
