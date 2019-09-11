import io.zenoh.*;

import java.nio.ByteBuffer;

class ZEval implements EvalCallback {

    private static String uri = "/demo/eval";

    public void queryHandler(String rname, String predicate, RepliesSender repliesSender) {
        System.out.println("Handling Query: " + rname);
    
        ByteBuffer data = ByteBuffer.wrap("\13EVAL_RESULT".getBytes());
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

            System.out.println("Declaring Eval: "+uri);
            z.declareEval(uri, new ZEval());

            while (true) {    
                Thread.sleep(1000);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
