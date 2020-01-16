
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
import io.zenoh.*;

import java.io.InputStreamReader;
import java.util.List;

public class ZSub {

    public static void main(String[] args) {
        String s = "/demo/example/**";
        if (args.length > 0) {
            s = args[0];
        }

        String locator = null;
        if (args.length > 1) {
            locator = args[1];
        }

        try {
            Selector selector = new Selector(s);

            System.out.println("Login to Zenoh (locator=" + locator + ")...");
            Zenoh z = Zenoh.login(locator, null);

            System.out.println("Use Workspace on '/'");
            Workspace w = z.workspace(new Path("/"));

            System.out.println("Subscribe on " + selector);
            SubscriptionId subid = w.subscribe(selector, new Listener() {
                public void onChanges(List<Change> changes) {
                    for (Change c : changes) {
                        switch (c.getKind()) {
                        case PUT:
                            System.out.printf(">> [Subscription listener] Received PUT on '%s': '%s')\n", c.getPath(),
                                    c.getValue());
                            break;
                        case UPDATE:
                            System.out.printf(">> [Subscription listener] Received UPDATE on '%s': '%s')\n",
                                    c.getPath(), c.getValue());
                            break;
                        case REMOVE:
                            System.out.printf(">> [Subscription listener] Received REMOVE on '%s')\n", c.getPath());
                            break;
                        default:
                            System.err.printf(
                                    ">> [Subscription listener] Received unkown operation with kind '%s' on '%s')\n",
                                    c.getKind(), c.getPath());
                            break;
                        }
                    }
                }
            });

            System.out.println("Enter 'q' to quit...\n");
            InputStreamReader stdin = new InputStreamReader(System.in);
            while ((char) stdin.read() != 'q')
                ;

            w.unsubscribe(subid);
            z.logout();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}