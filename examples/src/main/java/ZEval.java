import io.zenoh.*;

import java.io.InputStreamReader;
import java.nio.ByteBuffer;

class ZEval implements EvalCallback {

    private static String uri = "/demo/example/zenoh-java-eval";

    public void queryHandler(String rname, String predicate, RepliesSender repliesSender) {
        System.out.printf(">> [Query handler] Handling '%s?%s'\n", rname, predicate);
    
        ByteBuffer data = ByteBuffer.wrap("Eval from Java!".getBytes());
        Resource[] replies = {
            new Resource(uri, data, 0, 0)
        };

        repliesSender.sendReplies(replies);
    }

    public static void main(String[] args) {
        String locator = "tcp/127.0.0.1:7447";
        if (args.length > 0) {
            locator = args[0];
        }

        if (args.length > 1) {
            uri = args[1];
        }

        try {
            System.out.println("Connecting to "+locator+"...");
            Zenoh z = Zenoh.open(locator);

            System.out.println("Declaring Eval on '"+uri+"'...");
            Eval e = z.declareEval(uri, new ZEval());

            InputStreamReader stdin = new InputStreamReader(System.in);
            while ((char) stdin.read() != 'q');

            e.undeclare();
            z.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
