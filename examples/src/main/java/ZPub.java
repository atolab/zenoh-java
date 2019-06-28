import io.zenoh.*;

class ZPub {

    public static void main(String[] args) {
        String locator = "tcp/127.0.0.1:7447";
        if (args.length > 0) {
            locator = args[0];
        }

        String uri = "/demo/hello/alpha";
        if (args.length > 1) {
            uri = args[1];
        }

        String value = "Hello World!";
        if (args.length > 2) {
            value = args[2];
        }

        try {
            System.out.println("Connecting to "+locator+"...");
            Zenoh z = Zenoh.open(locator);

            // System.out.println("Declaring Publisher...");
            // Publisher pub = z.declarePublisher(uri);

            java.nio.ByteBuffer data = java.nio.ByteBuffer.allocate(512);
            data.put(value.getBytes("UTF-8"));
            data.flip();

            System.out.println("Streaming Data...");
            while (true) {
                System.out.println("pub.streamData: "+data);
                // pub.streamData(data);
                z.writeData(uri, data);
                Thread.sleep(1000);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
