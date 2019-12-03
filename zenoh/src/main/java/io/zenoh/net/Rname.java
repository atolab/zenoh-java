package io.zenoh.net;

import io.zenoh.swig.zenohc;

/**
 * Utility class for resource selectors.
 */
public class Rname {

    /**
     * Return true if the resource selector 'rname1' intersect with the resrouce selector 'rname2'.
     * @param rname1 a resource selector
     * @param rname2 a resrouce selector
     */
    public static boolean intersect(String rname1, String rname2) {
        return zenohc.intersect(rname1, rname2) != 0;
    }

}