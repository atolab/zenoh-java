package io.zenoh;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zenoh.net.ZNetException;
import io.zenoh.net.Session;
import io.zenoh.net.ZNet;

/**
 * The Zenoh client API.
 */
public class Zenoh {

    private static final String PROP_USER = "user";
    private static final String PROP_PASSWORD = "password";

    private static final Logger LOG = LoggerFactory.getLogger("io.zenoh");
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static String hexdump(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(HEX_DIGITS[(bytes[i] & 0xF0) >>> 4]).append(HEX_DIGITS[bytes[i] & 0x0F]);
        }
        return sb.toString();
    }

    private Session session;
    private ExecutorService threadPool;
    private Admin admin;

    private Zenoh(Session session, String zid) {
        this.session = session;
        this.threadPool = Executors.newCachedThreadPool();
        Workspace adminWs = new Workspace(new Path("/@"), session, threadPool);
        this.admin = new Admin(adminWs, zid);
    }

    /**
     * Establish a session with the Zenoh router reachable via provided locator.
     * If the provided locator is ``null``, login will perform some 
     * dynamic discovery and try to establish the session automatically. When not 
     * ``null``, the locator must have the format: {@code tcp/<ip>:<port>}.
     * 
     * @param locator    the locator or ``null``.
     * @param properties the Properties to be used for this session (e.g. "user",
     *                   "password"...). Can be ``null``.
     * @return a {@link Zenoh} object.
     * @throws ZException if login fails.
     */
    public static Zenoh login(String locator, Properties properties) throws ZException {
        try {
            LOG.debug("Establishing session to Zenoh router {}", locator);
            Session s;
            if (properties == null) {
                s = Session.open(locator);
            } else {
                s = Session.open(locator, getSessionProperties(properties));
            }

            Map<Integer, byte[]> props = s.info();
            byte[] buf = props.get(ZNet.INFO_PEER_PID_KEY);
            if (buf == null) {
                throw new ZException("Failed to retrieve Zenoh id from Session info");
            }
            String zid = hexdump(buf);
            LOG.info("Session established with Zenoh router {}", zid);
                return new Zenoh(s, zid);

        } catch (ZNetException e) {
            LOG.warn("Failed to establish session to {}", locator, e);
            throw new ZException("Login failed to " + locator, e);
        }
    }

    private static Map<Integer, byte[]> getSessionProperties(Properties properties) {
        Map<Integer, byte[]> zprops = new HashMap<Integer, byte[]>();

        if (properties.containsKey(PROP_USER))
            zprops.put(ZNet.USER_KEY, properties.getProperty(PROP_USER).getBytes(UTF8));
        if (properties.containsKey(PROP_PASSWORD))
            zprops.put(ZNet.PASSWD_KEY, properties.getProperty(PROP_PASSWORD).getBytes(UTF8));

        return zprops;
    }


    /**
     * Terminates the Zenoh session.
     */
    public void logout() throws ZException {
        threadPool.shutdown();
        try {
            session.close();
            this.session = null;
        } catch (ZNetException e) {
            throw new ZException("Error during logout", e);
        }
    }

    /**
     * Creates a Workspace using the provided path.
     * All relative {@link Selector} or {@link Path} used with this Workspace will be relative to this path.
     * <p>
     * Notice that all subscription listeners and eval callbacks declared in this workspace will be
     * executed by the I/O thread. This implies that no long operations or other call to Zenoh
     * shall be performed in those callbacks.
     *
     * @param path the Workspace's path.
     * @return a {@link Workspace}.
     */
    public Workspace workspace(Path path) {
        return new Workspace(path, session, null);
    }

    /**
     * Creates a Workspace using the provided path.
     * All relative {@link Selector} or {@link Path} used with this Workspace will be relative to this path.
     * <p>
     * Notice that all subscription listeners and eval callbacks declared in this workspace will be
     * executed by a CachedThreadPool. This is useful when listeners and/or callbacks need to perform
     * long operations or need to call other Zenoh operations.
     *
     * @param path the Workspace's path.
     * @return a {@link Workspace}.
     */
    public Workspace workspaceWithExecutor(Path path) {
        return new Workspace(path, session, threadPool);
    }

    /**
     * Returns the {@link Admin} object.
     */
    public Admin admin() {
        return admin;
    }

}
