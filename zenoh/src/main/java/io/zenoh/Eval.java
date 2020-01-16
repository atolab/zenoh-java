/*
 * Copyright (c) 2014, 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *
 * Contributors: Julien Enoch, ADLINK Technology Inc.
 * Initial implementation of Eclipse Zenoh.
 */
package io.zenoh;

import java.util.Properties;

/**
 * Interface to be implemented by evaluation functions (see
 * {@link Workspace#registerEval(Path, Eval)})
 */
public interface Eval {

    /**
     * The callback operation called each time the {@link Workspace#get(Selector)}
     * operation is called for a Selector matching the Path this Eval is registered
     * with. Zenoh will wrap the returned {@link Value} into a {@link Data} and add
     * it to the result of the get() operation.
     *
     * @param path  the Path with which the Eval has been registered with (in case
     *              the same Eval is registered with several Paths).
     * @param props the Properties specified in the Selector used in eval operation.
     * @return a Value resulting of the evaluation.
     */
    public Value callback(Path path, Properties props);
}
