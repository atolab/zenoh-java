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

import io.zenoh.swig.zenohc;

/**
 * Utility class for resource name.
 */
public class Rname {

    /**
     * Return true if the resource name 'rname1' intersect with the resource name
     * 'rname2'.
     * 
     * @param rname1 a resource name
     * @param rname2 a resrouce name
     */
    public static boolean intersect(String rname1, String rname2) {
        return zenohc.zn_rname_intersect(rname1, rname2) != 0;
    }

}