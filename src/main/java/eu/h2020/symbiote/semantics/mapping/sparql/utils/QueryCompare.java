/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.utils;

import eu.h2020.symbiote.semantics.mapping.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.algebra.walker.Walker;
import org.apache.jena.sparql.core.ComparisonException;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.engine.binding.BindingHashMap;
import org.apache.jena.sparql.graph.NodeTransform;
import org.apache.jena.sparql.graph.NodeTransformExpr;
import org.apache.jena.sparql.syntax.PatternVars;
import org.apache.jena.sparql.syntax.syntaxtransform.NodeTransformSubst;
import org.apache.jena.sparql.syntax.syntaxtransform.QueryTransformOps;
import org.apache.jena.sparql.util.Iso;
import org.apache.jena.sparql.util.NodeIsomorphismMap;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class QueryCompare extends org.apache.jena.sparql.core.QueryCompare {

    private final Query query2;
    private boolean result;
    private Map<Node, Node> map;
    private NodeIsomorphismMap isoMap;

    public static boolean equal(Query query1, Query query2, Map<Node, Node> map, NodeIsomorphismMap isoMap) {
        if (query1 == query2) {
            return true;
        }
        query1.setResultVars();
        query2.setResultVars();
        QueryCompare visitor = new QueryCompare(query1, map, isoMap);
        try {
            query2.visit(visitor);
//            query2Info.getKey().visit(visitor);
        } catch (ComparisonException ex) {
            return false;
        }
        return visitor.isTheSame() && visitor.result;
    }

    public static boolean equal(Query query1, Query query2, Map<Node, Node> map) {
        return equal(query1, query2, map, null);
    }

    public static boolean equal(Query query1, Query query2) {
        if (query1 == query2) {
            return true;
        }
        query1.setResultVars();
        query2.setResultVars();
        Pair<Query, List<Node>> query1Renamed = convertVarsToBlankNodes(query1);
        Pair<Query, List<Node>> query2Renamed = convertVarsToBlankNodes(query2);
        if (query1Renamed.getValue().size() != query2Renamed.getValue().size()) {
            // not same number of vars
            return false;
        }
        List<Map<Node, Node>> permutationMaps = getPermutationMaps(query1Renamed, query2Renamed);
        if (permutationMaps.isEmpty()) {
            return equal(query1, query2, new HashMap<>());
        }
        return permutationMaps.stream().anyMatch(x -> QueryCompare.equal(query1Renamed.getKey(), query2Renamed.getKey(), x));
    }

    private QueryCompare(Query query2) {
        super(query2);
        this.query2 = query2;
        this.map = new HashMap<>();
        this.isoMap = new NodeIsomorphismMap();
    }

    private QueryCompare(Query query2, Map<Node, Node> map) {
        this(query2, map, null);
    }

    @Override
    public void visitGroupBy(Query query1) {
        VarExprList groupByRenamed = new VarExprList();
        query1.getGroupBy().forEachExpr((v, e) -> {
            groupByRenamed.add(v, Walker.transform(e, new NodeTransformExpr((Node node)
                    -> map.containsKey(node)
                    ? map.get(node)
                    : node)));
        });
        check("GROUP BY", groupByRenamed, query2.getGroupBy());
    }

    private QueryCompare(Query query2, Map<Node, Node> map, NodeIsomorphismMap isoMap) {
        this(query2);
        this.map = Utils.ensureBidirectional(map);
        if (isoMap == null) {
            this.isoMap = JenaHelper.toIsoMap(this.map);
        } else {
            this.isoMap = isoMap;
        }
    }

    private static List<Map<Node, Node>> getPermutationMaps(Pair<Query, List<Node>> query1Info, Pair<Query, List<Node>> query2Info) {
        if (query1Info == null
                || query1Info.getKey() == null
                || query1Info.getValue() == null
                || query2Info == null
                || query2Info.getKey() == null
                || query2Info.getValue() == null) {
            return new ArrayList<>();
        }
        return Utils.permutate(query1Info.getValue(), query2Info.getValue()).stream()
                .map(x -> {
                    Map<Node, Node> map = new HashMap<>();
                    x.forEach(y -> {
                        map.put(y.get(0), y.get(1));
                    });
                    return map;
                })
                .collect(Collectors.toList());
    }

    private static Pair<Query, List<Node>> convertVarsToBlankNodes(Query query) {
        final List<Node> createdNodes = new ArrayList<>();
        Query resultQuery = QueryTransformOps.transform(
                query,
                PatternVars.vars(query.getQueryPattern()).stream()
                        .distinct()
                        .collect(Collectors.toMap(
                                x -> x,
                                x -> {
                                    Node newNode = NodeFactory.createBlankNode();
                                    createdNodes.add(newNode);
                                    return newNode;
                                })));
        return new Pair(resultQuery, createdNodes);
    }

    @Override
    public void visitQueryPattern(Query query1) {
        if (query1.getQueryPattern() == null
                && query2.getQueryPattern() == null) {
            return;
        }
        check("Missing pattern", query1.getQueryPattern() != null);
        check("Missing pattern", query2.getQueryPattern() != null);
        result = ElementCompare.equals(query1.getQueryPattern(), query2.getQueryPattern(), map, isoMap);
    }

    @Override
    public void visitSelectResultForm(Query query1) {
        check("Not both SELECT queries", query2.isSelectType());
        check("DISTINCT modifier",
                query1.isDistinct() == query2.isDistinct());
        check("SELECT *", query1.isQueryResultStar() == query2.isQueryResultStar());
        check("Result variables", Utils.equalsIgnoreOrder(
                query1.getProjectVars(),
                query2.getProjectVars(),
                (x, y) -> Iso.nodeIso(x, y, isoMap)));
    }

    private void check(String msg, Object obj1, Object obj2) {
        check(msg, Objects.equals(obj1, obj2));
    }

    private void check(String msg, boolean b) {
        if (!b) {
            result = false;
            throw new IllegalArgumentException(msg);
        }
    }
}
