/*
 * Copyright (c) 2017, 2020 ADLINK Technology Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *
 * Contributors:
 *   ADLINK zenoh team, <zenoh@adlink-labs.tech>
 */
package io.zenoh.net;

import io.zenoh.core.ZException;
import io.zenoh.swig.zenohc;
import io.zenoh.swig.zn_eva_t;

/**
 * An Eval (see {@link Session#declareEval(String, EvalCallback)}).
 */
public class Eval {

    private zn_eva_t eval;

    protected Eval(zn_eva_t eval) {
        this.eval = eval;
    }

    /**
     * Undeclare the Eval.
     * 
     * @throws ZException if undeclaration failed.
     */
    public void undeclare() throws ZException {
        int error = zenohc.zn_undeclare_eval(eval);
        if (error != 0) {
            throw new ZException("zn_undeclare_eval failed ", error);
        }
    }

}