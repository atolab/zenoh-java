import io.zenoh.net.*;

class ZNWrite {

    public static void main(String[] args) {
        String uri = "/demo/example/zenoh-java-write";
        if (args.length > 0) {
            uri = args[0];
        }

        String value = "Write from Java!";
        if (args.length > 1) {
            value = args[1];
        }

        String locator = null;
        if (args.length > 3) {
            locator = args[3];
        }

        try {
            System.out.println("Openning session...");
            Session s = Session.open(locator);

            System.out.printf("Writing Data ('%s': '%s')...\n", uri, value);
            java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(value.getBytes("UTF-8"));
            s.writeData(uri, buf);

            s.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}