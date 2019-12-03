import java.util.HashMap;
import java.util.Map;

import io.zenoh.net.*;

class ZNInfo {
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    public static String hexdump(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(HEX_DIGITS[(b & 0xF0) >> 4])
              .append(HEX_DIGITS[b & 0x0F]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String locator = null;
        if (args.length > 0) {
            locator = args[0];
        }

        try {
            System.out.println("Openning session...");
            Map<Integer, byte[]> properties = new HashMap<Integer, byte[]>(2);
            properties.put(ZNet.USER_KEY, "user".getBytes());
            properties.put(ZNet.PASSWD_KEY, "password".getBytes());
            Session s = Session.open(locator, properties);

            Map<Integer, byte[]> info = s.info();
            System.out.println("LOCATOR  : " + new String(info.get(ZNet.INFO_PEER_KEY)));
            System.out.println("PID      : " + hexdump(info.get(ZNet.INFO_PID_KEY)));
            System.out.println("PEER PID : " + hexdump(info.get(ZNet.INFO_PEER_PID_KEY)));

            s.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
