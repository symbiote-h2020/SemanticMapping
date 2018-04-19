package eu.h2020.symbiote.semantics.mapping.utils;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Helper class for Stream processing
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class StreamHelper {

    /**
     * converts an Iterable<T> to Stream<T>
     *
     * @param <T> type parameter
     * @param it iterable
     * @return <code>it</code> as Stream
     */
    public static <T> Stream<T> stream(Iterable<T> it) {
        return StreamSupport.stream(it.spliterator(), false);
    }

    /**
     * converts an Iterator<T> to Stream<T>
     *
     * @param <T> type parameter
     * @param it iterator
     * @return <code>it</code> as Stream
     */
    public static <T> Stream<T> stream(Iterator<T> it) {
        return stream(() -> it);
    }

    private StreamHelper() {
    }
}
