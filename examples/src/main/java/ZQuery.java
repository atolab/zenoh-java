import io.zenoh.*;
import io.zenoh.swig.z_reply_value_t;

class ZQuery {

    private static class ReplyHandler implements ReplyCallback {
        public void handle(z_reply_value_t reply) {
            switch (reply.getKind()) {
                case Zenoh.Z_STORAGE_DATA:
                    reply.getData();
                    break;
                case Zenoh.Z_STORAGE_FINAL:
                    System.out.println("Received Storage Final.");
                    break;
                case Zenoh.Z_REPLY_FINAL:
                    System.out.println("Received Reply Final.");
                    break;
            }
        }
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

        String value = "Hello World!";
        if (args.length > 2) {
            value = args[2];
        }

        try {
            System.out.println("Connecting to "+locator+"...");
            Zenoh z = Zenoh.open(locator);

            System.out.println("Send query for "+uri);
            z.query(uri,  "", new ReplyHandler());

            java.nio.ByteBuffer data = java.nio.ByteBuffer.allocate(512);
            data.put(value.getBytes("UTF-8"));
            data.flip();

            Thread.sleep(60000);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
