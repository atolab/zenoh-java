import io.zenoh.*;

class ZQuery {

    private static class Handler implements ReplyHandler {

        public void handleReply(ReplyValue reply) {
            switch (reply.getKind()) {
                case Z_STORAGE_DATA:
                case Z_EVAL_DATA:
                    java.nio.ByteBuffer data = reply.getData();
                    try {
                        byte[] buf = new byte[data.remaining()];
                        data.get(buf);
                        String s = new String(buf, "UTF-8");
                        if (reply.getKind() == ReplyValue.Kind.Z_STORAGE_DATA) {
                            System.out.printf(">> [Reply handler] received -Storage Data- ('%s': '%s')\n", reply.getRname(), s);
                        } else {
                            System.out.printf(">> [Reply handler] received -Eval Data-    ('%s': '%s')\n", reply.getRname(), s);
                        }
                    } catch (java.io.UnsupportedEncodingException e) {
                        System.out.println(">> [Reply handler] error decoding data: "+e);
                    }
                    break;
                case Z_STORAGE_FINAL:
                    System.out.println(">> [Reply handler] received -Storage Final-");
                    break;
                case Z_EVAL_FINAL:
                    System.out.println(">> [Reply handler] received -Eval Final-");
                    break;
                case Z_REPLY_FINAL:
                    System.out.println(">> [Reply handler] received -Reply Final-");
                    break;
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

            System.out.println("Send query '"+uri+"'...");
            z.query(uri,  "", new Handler(), QueryDest.all(), QueryDest.all());

            Thread.sleep(1000);

            z.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
