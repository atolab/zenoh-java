package io.zenoh;

import java.util.Collection;
import java.util.Properties;
import java.util.Map;
import java.util.Hashtable;

import io.zenoh.Path;
import io.zenoh.Value;
import io.zenoh.Workspace;
import io.zenoh.core.ZException;

/**
 * The Administration helper class.
 */
public class Admin {

    private Workspace w;
    private String zid;

    protected Admin(Workspace w, String zid) {
        this.w = w;
        this.zid = zid;
    }

    private Properties propertiesOfValue(Value v) {
        if (v instanceof PropertiesValue) {
            return ((PropertiesValue) v).getProperties();
        } else {
            Properties p = new Properties();
            p.setProperty("value", v.toString());
            return p;
        }
    }

    /************* Backends management *************/

    /**
     * Add a backend in the connected Zenoh router.
     */
    public void addBackend(String beid, Properties properties) throws ZException {
        addBackend(beid, properties, this.zid);
    }

    /**
     * Add a backend in the specified Zenoh router.
     */
    public void addBackend(String beid, Properties properties, String zid) throws ZException {
        String path = String.format("/@/router/%s/plugin/storages/backend/%s", zid, beid);
        w.put(new Path(path), new PropertiesValue(properties));
    }

    /**
     * Get a backend's properties from the connected Zenoh router.
     */
    public Properties getBackend(String beid) throws ZException {
        return getBackend(beid, this.zid);
    }

    /**
     * Get a backend's properties from the specified Zenoh router.
     */
    public Properties getBackend(String beid, String zid) throws ZException {
        String sel = String.format("/@/router/%s/plugin/storages/backend/%s", zid, beid);
        Collection<Data> entries = w.get(new Selector(sel));
        if (! entries.iterator().hasNext()) {
            return null;
        } else {
            return propertiesOfValue(entries.iterator().next().getValue());
        }
    }

    /**
     * Get all the backends from the connected Zenoh router.
     */
    public Map<String, Properties> getBackends() throws ZException {
        return getBackends(this.zid);
    }

    /**
     * Get all the backends from the specified Zenoh router.
     */
    public Map<String, Properties> getBackends(String zid) throws ZException {
        String sel = String.format("/@/router/%s/plugin/storages/backend/*", zid);
        Collection<Data> entries = w.get(new Selector(sel));
        Map<String, Properties> result = new Hashtable<String, Properties>(entries.size());
        for (Data pv : entries) {
            String beid = pv.getPath().toString().substring(sel.length()-1);
            result.put(beid, propertiesOfValue(pv.getValue()));
        }
        return result;
    }

    /**
     * Remove a backend from the connected Zenoh router.
     */
    public void removeBackend(String beid) throws ZException {
        removeBackend(beid, this.zid);
    }

    /**
     * Remove a backend from the specified Zenoh router.
     */
    public void removeBackend(String beid, String zid) throws ZException {
        String path = String.format("/@/router/%s/plugin/storages/backend/%s", zid, beid);
        w.remove(new Path(path));
    }

    /************* Storages management *************/

     /**
     * Add a storage in the connected Zenoh router, using an automatically chosen backend.
     */
    public void addStorage(String stid, Properties properties) throws ZException {
        addStorageOnBackend(stid, properties, "auto", this.zid);
    }

     /**
     * Add a storage in the specified Zenoh router, using an automatically chosen backend.
     */
    public void addStorage(String stid, Properties properties, String zid) throws ZException {
        addStorageOnBackend(stid, properties, "auto", zid);
    }

     /**
     * Add a storage in the connected Zenoh router, using the specified backend.
     */
    public void addStorageOnBackend(String stid, Properties properties, String backend) throws ZException {
        addStorageOnBackend(stid, properties, backend, this.zid);
    }

     /**
     * Add a storage in the specified Zenoh router, using the specified backend.
     */
    public void addStorageOnBackend(String stid, Properties properties, String backend, String zid) throws ZException {
        String path = String.format("/@/router/%s/plugin/storages/backend/%s/storage/%s", zid, backend, stid);
        w.put(new Path(path), new PropertiesValue(properties));
    }

    /**
     * Get a storage's properties from the connected Zenoh router.
     */
    public Properties getStorage(String stid) throws ZException {
        return getStorage(stid, this.zid);
    }

    /**
     * Get a storage's properties from the specified Zenoh router.
     */
    public Properties getStorage(String stid, String zid) throws ZException {
        String sel = String.format("/@/router/%s/plugin/storages/backend/*/storage/%s", zid, stid);
        Collection<Data> entries = w.get(new Selector(sel));
        if (! entries.iterator().hasNext()) {
            return null;
        } else {
            return propertiesOfValue(entries.iterator().next().getValue());
        }
    }

    /**
     * Get all the storages from the connected Zenoh router.
     */
    public Map<String, Properties> getStorages() throws ZException {
        return getStoragesFromBackend("*", this.zid);
    }

    /**
     * Get all the storages from the specified Zenoh router.
     */
    public Map<String, Properties> getStorages(String zid) throws ZException {
        return getStoragesFromBackend("*", zid);
    }

    /**
     * Get all the storages from the specified backend within the connected Zenoh router.
     */
    public Map<String, Properties> getStoragesFromBackend(String backend) throws ZException {
        return getStoragesFromBackend(backend, this.zid);
    }

    /**
     * Get all the storages from the specified backend within the specified Zenoh router.
     */
    public Map<String, Properties> getStoragesFromBackend(String backend, String zid) throws ZException {
        String sel = String.format("/@/router/%s/plugin/storages/backend/%s/storage/*", zid, backend);
        Collection<Data> data = w.get(new Selector(sel));
        Map<String, Properties> result = new Hashtable<String, Properties>(data.size());
        for (Data d : data) {
            String stPath = d.getPath().toString();
            String stid = stPath.substring(stPath.lastIndexOf('/')+1);
            result.put(stid, propertiesOfValue(d.getValue()));
        }
        return result;
    }

    /**
     * Remove a storage from the connected Zenoh router.
     */
    public void removeStorage(String stid) throws ZException {
        removeStorage(stid, this.zid);
    }

    /**
     * Remove a backend from the specified Zenoh router.
     */
    public void removeStorage(String stid, String zid) throws ZException {
        String sel = String.format("/@/router/%s/plugin/storages/backend/*/storage/%s", zid, stid);
        Collection<Data> entries = w.get(new Selector(sel));
        if (entries.iterator().hasNext()) {
            Path p = entries.iterator().next().getPath();
            w.remove(p);
        }
    }

}
