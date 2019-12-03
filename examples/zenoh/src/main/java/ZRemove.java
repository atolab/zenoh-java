import io.zenoh.*;

public class ZRemove {

    public static void main(String[] args) {
        // If not specified as 1st argument, use a relative path (to the workspace below): "zenoh-java-put"
        String path = "zenoh-java-put";
        if (args.length > 0) {
            path = args[0];
        }

        String locator = null;
        if (args.length > 1) {
            locator = args[1];
        }

        try {
            Path p = new Path(path);

            System.out.println("Login to Zenoh (locator="+locator+")...");
            Zenoh z = Zenoh.login(locator, null);

            System.out.println("Use Workspace on '/demo/example'");
            Workspace w = z.workspace(new Path("/demo/example"));

            System.out.println("Remove "+p);
            w.remove(p);

            z.logout();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}