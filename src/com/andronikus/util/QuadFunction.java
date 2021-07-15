package com.andronikus.util;

/**
 * Functional interface similar to java functional interfaces but it takes four parameters as opposed to where java
 * maxes out which is at a BiFunction.
 *
 * @param <A> Type of first parameter
 * @param <B> Type of second parameter
 * @param <C> Type of third parameter
 * @param <D> Type of fourth parameter
 * @param <Y> Type of return
 * @author Andronikus
 */
public interface QuadFunction<A, B, C, D, Y> {

    /**
     * Use function and return result.
     *
     * @param a First parameter
     * @param b Second parameter
     * @param c Third parameter
     * @param d Fourth parameter
     * @return Result of the function
     */
    Y apply(A a, B b, C c, D d);
}
