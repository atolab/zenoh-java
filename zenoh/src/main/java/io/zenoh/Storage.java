package io.zenoh;

import io.zenoh.swig.zenohc;
import io.zenoh.swig.z_sto_t;

/**
 * A Storage (see {@link Zenoh#declareStorage(String, StorageCallback)}).
 */
public class Storage {

    private z_sto_t sto;

    protected Storage(z_sto_t sto) {
        this.sto = sto;
    }

    /**
     * Undeclare the Storage.
     * @throws ZException if undeclaration failed.
     */
    public void undeclare() throws ZException {
        int error = zenohc.z_undeclare_storage(sto);
        if (error != 0) {
            throw new ZException("z_undeclare_storage failed ", error);
        }
    }

}