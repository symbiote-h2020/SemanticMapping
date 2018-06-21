/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.utils;

import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlElementMatch;
import eu.h2020.symbiote.semantics.mapping.sparql.model.SparqlMatch;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.jena.graph.Node;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class Utils {

    private Utils() {
        Map<Node, List<SparqlElementMatch>> t1 = null;
        Map<Node, List<SparqlElementMatch>> t2 = null;
        Stream<Map<Node, List<SparqlElementMatch>>> t3 = Stream.of(t1, t2);
        Utils.combineKeysMatch(t1, t1);
        Utils.combineKeysMatch(t1, t2);
        Utils.combineKeysMatch(t2, t2);
        Utils.combineKeysMatch(t1, t1, t1);
    }

    /* merge */
    public static <K, V, W extends V> void merge(Map<K, List<V>> map1, Map<K, List<W>> map2) {
        if (map1 == null) {
            map1 = new HashMap<>();
        }
        if (map2 == null) {
            return;
        }
        for (Map.Entry<K, List<W>> entry : map2.entrySet()) {
            if (!map1.containsKey(entry.getKey())) {
                map1.put(entry.getKey(), new ArrayList<>());
            }
            map1.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    public static <K, V> void merge(Map<K, List<V>>... maps) {
        merge(Stream.of(maps));
    }

    public static <K, V> void merge(Collection<Map<K, List<V>>> maps) {
        merge(maps.stream());
    }

    public static <K, V> void merge(Stream<Map<K, List<V>>> maps) {
        if (maps == null || maps.count() < 2) {
            return;
        }
        Map<K, List<V>> first = maps.findFirst().get();
        maps.skip(1).forEach(x -> merge(first, x));
    }

    public static void mergeMatches(List<SparqlMatch> list1, List<SparqlMatch> list2) {
        if (list1 == null) {
            list1 = new ArrayList<>();
        }
        if (list2 == null) {
            return;
        }
        for (SparqlMatch match : list2) {
            Optional<SparqlMatch> entry = find(list1, match.getMatchedNode());
            if (entry.isPresent()) {
                entry.get().getMatchedElements().addAll(match.getMatchedElements());
            } else {
                list1.add(match);
            }
        }
    }

    public static void mergeMatches(List<SparqlMatch>... lists) {
        mergeMatches(Stream.of(lists));
    }

    public static void mergeMatches(Collection<List<SparqlMatch>> lists) {
        mergeMatches(lists.stream());
    }

    public static void mergeMatches(Stream<List<SparqlMatch>> lists) {
        if (lists == null || lists.count() < 2) {
            return;
        }
        List<SparqlMatch> first = lists.findFirst().get();
        lists.skip(1).forEach(x -> mergeMatches(first, x));
    }


    /* combineKeysMatch */
    public static <K, V, W extends V> Map<K, List<V>> combineKeysMatch(Map<K, List<V>> map1, Map<K, List<W>> map2) {
        return combineKeysMatch(Stream.of(map1, Utils.convertType(map2)));
    }

    public static <K, V> Map<K, List<V>> combineKeysMatch(Map<K, List<V>>... maps) {
        return combineKeysMatch(Stream.of(maps));
    }

    public static <K, V> Map<K, List<V>> combineKeysMatch(Collection<Map<K, List<V>>> maps) {
        return combineKeysMatch(maps.stream());
    }

    public static <K, V> Map<K, List<V>> combineKeysMatch(Stream<Map<K, List<V>>> maps) {
        Map<K, List<V>> result = new HashMap<>();
        if (maps == null) {
            return result;
        }
        final List<Map<K, List<V>>> mapList = maps.collect(Collectors.toList());
        int mapCount = mapList.size();
        if (mapCount == 1) {
            return mapList.get(0);
        }

        List<K> keysPresentInAllLists = mapList.stream()
                .flatMap(x -> x.entrySet().stream())
                .collect(Collectors.groupingBy(x -> x.getKey(), Collectors.summingInt(x -> x.getValue().size())))
                .entrySet().stream()
                .filter(x -> x.getValue() == mapCount)
                .map(x -> x.getKey()).collect(Collectors.toList());

        keysPresentInAllLists.forEach(k -> {
            mapList.forEach(x -> {
                if (!result.containsKey(k)) {
                    result.put(k, new ArrayList<>());
                }
                result.get(k).addAll(x.get(k));
            });

        });
        return result;
    }

    public static List<SparqlMatch> combineMatchesKeysMatch(List<SparqlMatch> list1, List<SparqlMatch> list2) {
        return combineMatchesKeysMatch(Stream.of(list1, list2));
    }

    public static List<SparqlMatch> combineMatchesKeysMatch(List<SparqlMatch>... lists) {
        return combineMatchesKeysMatch(Stream.of(lists));
    }

    public static List<SparqlMatch> combineMatchesKeysMatch(Collection<List<SparqlMatch>> lists) {
        return combineMatchesKeysMatch(lists.stream());
    }

    public static List<SparqlMatch> combineMatchesKeysMatch(Stream<List<SparqlMatch>> lists) {
        List<SparqlMatch> result = new ArrayList<>();
        if (lists == null) {
            return result;
        }
        List<List<SparqlMatch>> listsList = lists.collect(Collectors.toList());
        int mapCount = listsList.size();
        if (mapCount == 1) {
            return listsList.get(0);
        }
        List<Node> keysPresentInAllLists = listsList.stream()
                .flatMap(x -> x.stream())
                .collect(Collectors.groupingBy(x -> x.getMatchedNode(), Collectors.summingInt(x -> 1)))
                .entrySet().stream()
                .filter(x -> x.getValue() == mapCount)
                .map(x -> x.getKey()).collect(Collectors.toList());

        keysPresentInAllLists.forEach(k -> {
            listsList.forEach(x -> {
                Optional<SparqlMatch> temp = find(result, k);
                List<SparqlElementMatch> toAdd = x.stream().filter(y -> y.getMatchedNode().equals(k)).flatMap(y -> y.getMatchedElements().stream()).collect(Collectors.toList());
                if (temp.isPresent()) {
                    temp.get().getMatchedElements().addAll(toAdd);
                } else {
                    result.add(new SparqlMatch(k, toAdd));
                }
            });

        });
        return result;
    }

    private static Optional<SparqlMatch> find(List<SparqlMatch> map, Node node) {
        return map.stream().filter(x -> x.getMatchedNode().equals(node)).findAny();
    }

    /* combine */
    public static <K, V, W extends V> Map<K, List<V>> combine(Map<K, List<V>> map1, Map<K, List<W>> map2) {
        return combine(Stream.of(map1, convertType(map2)));
    }

    public static <K, V> Map<K, List<V>> combine(Map<K, List<V>>... maps) {
        return combine(Stream.of(maps));
    }

    public static <K, V> Map<K, List<V>> combine(Collection<Map<K, List<V>>> maps) {
        return combine(maps.stream());
    }

    public static <K, V> Map<K, List<V>> combine(Stream<Map<K, List<V>>> maps) {
        if (maps == null) {
            return new HashMap<>();
        }
        return maps.flatMap(x -> x.entrySet().stream()).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
    }

    public static List<SparqlMatch> combineMatches(Stream<List<SparqlMatch>> lists) {
        if (lists == null) {
            return new ArrayList<>();
        }
        return lists
                .flatMap(x -> x.stream())
                .collect(Collectors.groupingBy(x -> x.getMatchedNode()))
                .entrySet().stream()
                .map(x -> new SparqlMatch(x.getKey(),
                x.getValue().stream()
                        .flatMap(y -> y.getMatchedElements().stream()).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public static List<SparqlMatch> combine(List<SparqlMatch>... maps) {
        if (maps == null || maps.length == 0) {
            return new ArrayList<>();
        }
        if (maps.length == 1) {
            return maps[0];
        }
        List<List<SparqlMatch>> lists = Arrays.asList(maps);
        final List<SparqlMatch> result = lists.stream().findFirst().get();
        lists.stream().skip(1).forEach(x -> combineInline(result, x));
        return result;
    }

    public static List<SparqlMatch> combine(List<SparqlMatch> map1, List<SparqlMatch> map2) {
        List<SparqlMatch> result = new ArrayList<>();
        result.addAll(map1);
        combineInline(result, map2);
        return result;
    }

    public static void combineInline(List<SparqlMatch> map1, List<SparqlMatch> map2) {
        for (SparqlMatch match : map2) {
            Optional<SparqlMatch> entry = find(map1, match.getMatchedNode());
            if (entry.isPresent()) {
                entry.get().getMatchedElements().addAll(match.getMatchedElements());
            } else {
                map1.add(match);
            }
        }
    }

    /* helper */
    private static <K, V> Map<K, List<V>> deeperCopy(Map<K, List<V>> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> new ArrayList(e.getValue())));
    }

    private static <K, V, W extends V> Map<K, List<V>> convertType(Map<K, List<W>> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        x -> x.getKey(),
                        x -> x.getValue().stream()
                                .map(y -> (V) y)
                                .collect(Collectors.toList())));
    }

    @FunctionalInterface
    public interface FunctionWithException<T, R, E extends Exception> {

        R apply(T t) throws E;
    }

    public static <T, R, E extends Exception> Function<T, R> executeSafe(FunctionWithException<T, R, E> fe) {
        return arg -> {
            try {
                return fe.apply(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static String replace(String source, Map<String, String> replacements) {
        String result = source;
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            result = result.replace(replacement.getKey(), replacement.getValue());
        }
        return result;
    }

    public static <T> List<List<List<T>>> permutate(List<? extends T> listA, List<? extends T> listB) {
        List<List<List<T>>> result = new ArrayList<>();
        permutateList(listB, null).forEach(x -> {
            List<List<T>> combination = new ArrayList<>(listA.size());
            for (int i = 0; i < listA.size(); i++) {
                combination.add(Arrays.asList(listA.get(i), x.get(i)));
            }
            result.add(combination);
        });
        return result;
    }

    public static <T> List<List<T>> powerset(Collection<T> list) {
        List<List<T>> ps = new ArrayList<>();
        ps.add(new ArrayList<>());   // add the empty set

        // for every item in the original list
        for (T item : list) {
            List<List<T>> newPs = new ArrayList<>();

            for (List<T> subset : ps) {
                // copy all of the current powerset's subsets
                newPs.add(subset);

                // plus the subsets appended with the current item
                List<T> newSubset = new ArrayList<>(subset);
                newSubset.add(item);
                newPs.add(newSubset);
            }

            // powerset is now powerset of list.subList(0, list.indexOf(item)+1)
            ps = newPs;
        }
        return ps;
    }

    private static <T> List<List<T>> permutateList(List<? extends T> data, List<Integer> used) {
        List<List<T>> result = new ArrayList<>();
        if (data == null) {
            return result;
        }
        if (used == null) {
            used = new ArrayList<>();
        }
        for (int i = 0; i < data.size(); i++) {
            if (!used.contains(i)) {
                List<Integer> newUsed = new ArrayList<>(used);
                newUsed.add(i);
                List<List<T>> permutate2 = permutateList(data, newUsed);
                final T current = data.get(i);
                permutate2.forEach(x -> x.add(0, current));
                if (permutate2.isEmpty()) {
                    List<T> newList = new ArrayList<>();
                    newList.add(current);
                    permutate2.add(newList);
                }
                result.addAll(permutate2);
            }
        }
        return result;
    }

    public static <T, S extends Stream<T>> Collector<S, ?, Set<T>> intersecting() {
        class Acc {

            Set<T> result;

            void accept(S s) {
                if (result == null) {
                    result = s.collect(Collectors.toSet());
                } else {
                    result.retainAll(s.collect(Collectors.toList()));
                }
            }

            Acc combine(Acc other) {
                if (result == null) {
                    return other;
                }
                if (other.result != null) {
                    result.retainAll(other.result);
                }
                return this;
            }
        }
        return Collector.of(Acc::new, Acc::accept, Acc::combine,
                acc -> acc.result == null ? Collections.emptySet() : acc.result,
                Collector.Characteristics.UNORDERED);
    }

//    private static <T> List<List<T>> getPermutations(List<T> values, int size) {
//        size = values.size();
//        if (0 == size) {
//            return Collections.singletonList(Collections.<T>emptyList());
//        }
//        if (values.isEmpty()) {
//            return Collections.emptyList();
//        }
//        List<List<T>> combination = new ArrayList<>();
//        T actual = values.iterator().next();
//        List<T> subSet = new ArrayList<>(values);
//        subSet.remove(actual);
//        List<List<T>> subSetCombination = getPermutations(subSet, size - 1);
//        for (List<T> set : subSetCombination) {
//            List<T> newSet = new ArrayList<>(set);
//            newSet.add(0, actual);
//            combination.add(newSet);
//        }
//        combination.addAll(getPermutations(subSet, size));
//        return combination;
//    }
    public static <T> boolean equalsIgnoreOrder(Iterable<T> a, Iterable<T> b, BiPredicate<T, T> equals) {
        if (a == null || b == null) {
            return (a == null && b == null);
        }
        // not working if we have duplicates in a list
        assert (StreamSupport.stream(a.spliterator(), false).noneMatch(x -> contains(a, x, equals)));
        assert (StreamSupport.stream(b.spliterator(), false).noneMatch(x -> contains(b, x, equals)));
        return StreamSupport.stream(a.spliterator(), false).allMatch(x -> contains(b, x, equals))
                && StreamSupport.stream(b.spliterator(), false).allMatch(x -> contains(a, x, equals));
    }

    public static <T> boolean contains(Iterable<T> collection, T element, BiPredicate<T, T> equals) {
        return StreamSupport.stream(collection.spliterator(), false).anyMatch(x -> x != element && equals.test(x, element));
    }

    public static Map<Node, Node> ensureBidirectional(Map<Node, Node> map) {
        Map<Node, Node> result = new HashMap<>(map);
        map.entrySet().forEach(x -> {
            if (map.containsKey(x.getValue())) {
                // already contains other direction
                if (!Objects.equals(map.get(x.getValue()), x.getKey())) {
                    throw new IllegalArgumentException("only 1:1 mapping supported");
                }
            } else {
                result.put(x.getValue(), x.getKey());
            }
        });
        return result;
    }

    public static String readFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    public static void writeFile(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes(), StandardOpenOption.CREATE);
    }

}
