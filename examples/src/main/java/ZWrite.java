import io.zenoh.*;

class ZWrite {

    public static void main(String[] args) {
        String locator = null;
        if (args.length > 0) {
            locator = args[0];
        }

        String uri = "/demo/example/zenoh-java-write";
        if (args.length > 1) {
            uri = args[1];
        }

        String value = "Write from Java!";
        if (args.length > 2) {
            value = args[2];
        }

        try {
            System.out.println("Openning session...");
            Zenoh z = Zenoh.open(locator);

            System.out.printf("Writing Data ('%s': '%s')...\n", uri, value);
            java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(value.getBytes("UTF-8"));
            z.writeData(uri, buf);

            z.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
