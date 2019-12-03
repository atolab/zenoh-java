import io.zenoh.net.*;

import java.io.InputStreamReader;
import java.nio.ByteBuffer;

class ZNEval implements QueryHandler {

    private static String uri = "/demo/example/zenoh-java-eval";

    public void handleQuery(String rname, String predicate, RepliesSender repliesSender) {
        System.out.printf(">> [Query handler] Handling '%s?%s'\n", rname, predicate);
    
        ByteBuffer data = ByteBuffer.wrap("Eval from Java!".getBytes());
        Resource[] replies = {
            new Resource(uri, data, 0, 0)
        };

        repliesSender.sendReplies(replies);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            uri = args[0];
        }
        
        String locator = null;
        if (args.length > 1) {
            locator = args[1];
        }

        try {
            System.out.println("Openning session...");
            Session s = Session.open(locator);

            System.out.println("Declaring Eval on '"+uri+"'...");
            Eval e = s.declareEval(uri, new ZNEval());

            InputStreamReader stdin = new InputStreamReader(System.in);
            while ((char) stdin.read() != 'q');

            e.undeclare();
            s.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
