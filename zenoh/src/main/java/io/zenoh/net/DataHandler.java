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
package io.zenoh.net;

import java.nio.ByteBuffer;

/**
 * A callback interface to be implemented for the reception of subscribed/stored
 * resources. See
 * {@link Session#declareSubscriber(String, SubMode, DataHandler)} and
 * {@link Session#declareStorage(String, StorageHandler)}.
 */
public interface DataHandler {

    /**
     * The method that will be called on reception of data matching the subscribed
     * or stored resource.
     *
     * @param rname the resource name of the received data.
     * @param data  the received data.
     * @param info  the {@link DataInfo} associated with the received data.
     */
    public void handleData(String rname, ByteBuffer data, DataInfo info);

}
