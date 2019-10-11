import java.util.Properties;

import io.zenoh.*;

class ZInfo {

    public static void main(String[] args) {
        String locator = "tcp/127.0.0.1:7447";
        if (args.length > 0) {
            locator = args[0];
        }

        try {
            System.out.println("Connecting to "+locator+"...");
            Properties properties = new Properties();
            properties.setProperty("user", "user");
            properties.setProperty("password", "password");
            Zenoh z = Zenoh.open(locator, properties);

            Properties info = z.info();
            System.out.println("LOCATOR  : " + info.getProperty("peer"));
            System.out.println("PID      : " + info.getProperty("pid"));
            System.out.println("PEER PID : " + info.getProperty("peer_pid"));

            z.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
