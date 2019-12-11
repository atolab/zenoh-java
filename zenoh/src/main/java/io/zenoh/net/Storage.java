package io.zenoh.net;

import io.zenoh.swig.zenohc;
import io.zenoh.swig.zn_sto_t;

/**
 * A Storage (see {@link Zenoh#declareStorage(String, StorageCallback)}).
 */
public class Storage {

    private zn_sto_t sto;

    protected Storage(zn_sto_t sto) {
        this.sto = sto;
    }

    /**
     * Undeclare the Storage.
     * @throws ZNetException if undeclaration failed.
     */
    public void undeclare() throws ZNetException {
        int error = zenohc.zn_undeclare_storage(sto);
        if (error != 0) {
            throw new ZNetException("z_undeclare_storage failed ", error);
        }
    }

}