package io.zenoh;

import java.util.Properties;

/**
 * Interface to be implemented by evaluation functions (see {@link Workspace#registerEval(Path, Eval)})
 */
public interface Eval {

    /**
     * The callback operation called each time the {@link Workspace#get(Selector)} operation
     * is called for a Selector matching the Path this Eval is registered with.
     * Zenoh will wrap the returned {@link Value} into a {@link Data} and add it
     * to the result of the get() operation. 
     * 
     * @param path the Path with which the Eval has been registered with
     *             (in case the same Eval is registered with several Paths).
     * @param props the Properties specified in the Selector used in eval operation.
     * @return a Value resulting of the evaluation.
     */
    public Value callback(Path path, Properties props);
}
