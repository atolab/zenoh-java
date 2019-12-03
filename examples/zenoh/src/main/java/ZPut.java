import io.zenoh.*;

public class ZPut {

    public static void main(String[] args) {
        // If not specified as 1st argument, use a relative path (to the workspace below): "zenoh-java-put"
        String path = "zenoh-java-put";
        if (args.length > 0) {
            path = args[0];
        }

        String value = "Put from Zenoh Java!";
        if (args.length > 1) {
            value = args[1];
        }

        String locator = null;
        if (args.length > 2) {
            locator = args[2];
        }

        try {
            Path p = new Path(path);
            Value v = new StringValue(value);

            System.out.println("Login to Zenoh (locator="+locator+")...");
            Zenoh z = Zenoh.login(locator, null);

            System.out.println("Use Workspace on '/demo/example'");
            Workspace w = z.workspace(new Path("/demo/example"));

            System.out.println("Put on "+p+" : "+v);
            w.put(p, v);

            z.logout();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}