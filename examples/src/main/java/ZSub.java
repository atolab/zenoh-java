import io.zenoh.*;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

class ZSub {

    private static class Listener implements DataHandler {
        public void handleData(String rname, ByteBuffer data, DataInfo info) {
            try {
                byte[] buf = new byte[data.remaining()];
                data.get(buf);
                String str = new String(buf, "UTF-8");
                System.out.printf(">> [Subscription listener] Received ('%s': '%s')\n", rname, str);
            } catch (UnsupportedEncodingException e) {
                System.out.printf(">> [Subscription listener] Received ('%s': '%s')\n", rname, data.toString());
            }
        }
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

            System.out.println("Declaring Subscriber on '"+uri+"'...");
            Subscriber sub = z.declareSubscriber(uri, SubMode.push(), new Listener());

            InputStreamReader stdin = new InputStreamReader(System.in);
            while ((char) stdin.read() != 'q');

            sub.undeclare();
            z.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
