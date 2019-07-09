package io.zenoh;

import io.zenoh.swig.zenohc;

/**
 * Utility class for resource names.
 */
public class Rname {

    /**
     * Return true if the resource name 'rname1' intersect with the resrouce name 'rname2'.
     * @param rname1 a resource name
     * @param rname2 a resrouce name
     */
    public static boolean intersect(String rname1, String rname2) {
        return zenohc.intersect(rname1, rname2) != 0;
    }

}