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
 * A resource with a name and a value (data).
 */
public class Resource {

    private String rname;
    private ByteBuffer data;
    private int encoding;
    private int kind;

    public Resource(String rname, ByteBuffer data, int encoding, int kind) {
        this.rname = rname;
        this.data = data;
        this.encoding = encoding;
        this.kind = kind;
    }

    /**
     * @return the resource name.
     */
    public String getRname() {
        return rname;
    }

    /**
     * @return the resource value.
     */
    public ByteBuffer getData() {
        return data;
    }

    /**
     * @return the encoding of the resource value.
     */
    public int getEncoding() {
        return encoding;
    }

    /**
     * @return the kind of the resource.
     */
    public int getKind() {
        return kind;
    }
}
