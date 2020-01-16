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

/**
 * Class to be used in a {@link QueryHandler} implementation to send back
 * replies to a query.
 */
public final class RepliesSender {

    private long send_replies_ptr;
    private long query_handle_ptr;

    private RepliesSender(long send_replies_ptr, long query_handle_ptr) {
        this.send_replies_ptr = send_replies_ptr;
        this.query_handle_ptr = query_handle_ptr;
    }

    /**
     * Send back the replies to the query associated with this {@link RepliesSender}
     * object.
     * 
     * @param replies the replies.
     */
    public void sendReplies(Resource[] replies) {
        io.zenoh.swig.zenohc.call_replies_sender(send_replies_ptr, query_handle_ptr, replies);
    }
}
