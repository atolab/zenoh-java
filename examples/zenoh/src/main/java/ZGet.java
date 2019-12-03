import io.zenoh.*;

public class ZGet {

    public static void main(String[] args) {
        String selector = "/demo/example/**";
        if (args.length > 0) {
            selector = args[0];
        }

        String locator = null;
        if (args.length > 1) {
            locator = args[1];
        }

        try {
            Selector s = new Selector(selector);

            System.out.println("Login to Zenoh (locator="+locator+")...");
            Zenoh z = Zenoh.login(locator, null);

            System.out.println("Use Workspace on '/'");
            Workspace w = z.workspace(new Path("/"));

            System.out.println("Get from "+s);
            for (Entry entry : w.get(s)) {
                System.out.println("  "+entry.getPath()+" : "+entry.getValue());
            }

            z.logout();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
