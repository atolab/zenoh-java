package io.zenoh.net;

import io.zenoh.swig.zenohc;
import io.zenoh.swig.z_eva_t;

/**
 * An Eval (see {@link Zenoh#declareEval(String, EvalCallback)}).
 */
public class Eval {

    private z_eva_t eval;

    protected Eval(z_eva_t eval) {
        this.eval = eval;
    }

    /**
     * Undeclare the Eval.
     * @throws ZNetException if undeclaration failed.
     */
    public void undeclare() throws ZNetException {
        int error = zenohc.z_undeclare_eval(eval);
        if (error != 0) {
            throw new ZNetException("z_undeclare_eval failed ", error);
        }
    }

}