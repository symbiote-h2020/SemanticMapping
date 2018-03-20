/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.utils;

import eu.h2020.symbiote.semantics.mapping.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.E_LogicalOr;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.util.NodeIsomorphismMap;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ExpressionCompare {

    private Map<Node, Node> map;
    private NodeIsomorphismMap isoMap;

    private ExpressionCompare() {
        this.map = new HashMap<>();
        this.isoMap = new NodeIsomorphismMap();
    }

    private ExpressionCompare(Map<Node, Node> map) {
        this(map, null);
    }

    private ExpressionCompare(Map<Node, Node> map, NodeIsomorphismMap isoMap) {
        this.map = Utils.ensureBidirectional(map);
        if (isoMap == null) {
            this.isoMap = JenaHelper.toIsoMap(this.map);
        } else {
            this.isoMap = isoMap;
        }
    }

    protected static <T extends Expr> boolean equals(T a, Object b, Map<Node, Node> map, NodeIsomorphismMap isoMap) {
        return new ExpressionCompare(map, isoMap).equalsInternal(a, b);
    }

    public static <T extends Expr> boolean equals(T a, Object b, Map<Node, Node> map) {
        return new ExpressionCompare(map).equalsInternal(a, b);
    }

    public static <T extends Expr> boolean equals(T a, Object b) {
        return new ExpressionCompare().equalsInternal(a, b);
    }

    private <T extends Expr> boolean equalsInternal(T a, Object b) {
        if (a == null || b == null) {
            return (a == null && b == null);
        }
        if (!a.getClass().isAssignableFrom(b.getClass())) {
            return false;
        }
        Expr aMapped = a.applyNodeTransform((Node n) -> {
            if (map.containsKey(n)) {
                return map.get(n);
            }
            return n;
        });
        if (aMapped instanceof E_LogicalAnd) {
            return equals((E_LogicalAnd) aMapped, (E_LogicalAnd) b);
        }
        if (aMapped instanceof E_LogicalOr) {
            return equals((E_LogicalOr) aMapped, (E_LogicalOr) b);
        }
        return defaultEquals(aMapped, (Expr) b);
    }

    private boolean defaultEquals(Expr a, Expr b) {
        return a != null && a.equalsBySyntax(b);
    }

    private boolean equals(E_LogicalAnd a, E_LogicalAnd b) {
        return Utils.equalsIgnoreOrder(a.getArgs(), b.getArgs(), (x, y) -> equalsInternal(x, y));
    }
    
    private boolean equals(E_LogicalOr a, E_LogicalOr b) {
        return Utils.equalsIgnoreOrder(a.getArgs(), b.getArgs(), (x, y) -> equalsInternal(x, y));
    }
}
