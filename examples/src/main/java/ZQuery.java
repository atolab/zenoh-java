import io.zenoh.*;

class ZQuery {

    private static class ReplyHandler implements ReplyCallback {

        public void handle(ReplyValue reply) {
            switch (reply.getKind()) {
                case Z_STORAGE_DATA:
                case Z_EVAL_DATA:
                    java.nio.ByteBuffer data = reply.getData();
                    try {
                        int len = Vle.decode(data);
                        byte[] buf = new byte[len];
                        data.get(buf);
                        String s = new String(buf, "UTF-8");
                        if (reply.getKind() == ReplyValue.Kind.Z_STORAGE_DATA) {
                            System.out.println("Received Storage Data. " + reply.getRname() + ":"+ s);
                        } else {
                            System.out.println("Received Eval Data. " + reply.getRname() + ":"+ s);
                        }
                    } catch (java.io.UnsupportedEncodingException e) {
                        System.out.println("Error decoding data: "+e);
                    }
                    break;
                case Z_STORAGE_FINAL:
                    System.out.println("Received Storage Final.");
                    break;
                case Z_EVAL_FINAL:
                    System.out.println("Received Eval Final.");
                    break;
                case Z_REPLY_FINAL:
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

        try {
            System.out.println("Connecting to "+locator+"...");
            Zenoh z = Zenoh.open(locator);

            System.out.println("Send query for "+uri);
            z.query(uri,  "", new ReplyHandler());

            Thread.sleep(1000);
            z.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
