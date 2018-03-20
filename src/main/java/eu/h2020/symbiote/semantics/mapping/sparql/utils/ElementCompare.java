/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.utils;

import eu.h2020.symbiote.semantics.mapping.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementDataset;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.util.Iso;
import org.apache.jena.sparql.util.NodeIsomorphismMap;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ElementCompare {

    private final Map<Node, Node> map;
    private final NodeIsomorphismMap isoMap;

    private ElementCompare() {
        this.map = new HashMap<>();
        this.isoMap = new NodeIsomorphismMap();
    }

    private ElementCompare(Map<Node, Node> map) {
        this.map = Utils.ensureBidirectional(map);
        isoMap = JenaHelper.toIsoMap(this.map);
    }
    
    private ElementCompare(Map<Node, Node> map, NodeIsomorphismMap isoMap) {
        this.map = map;
        this.isoMap = isoMap;
    }

    protected static <T extends Element> boolean equals(T a, Object b, Map<Node, Node> map, NodeIsomorphismMap isoMap) {
        return new ElementCompare(map, isoMap).equalsInternal(a, b);
    }
    
    public static <T extends Element> boolean equals(T a, Object b, Map<Node, Node> map) {
        return new ElementCompare(map).equalsInternal(a, b);
    }

    public static <T extends Element> boolean equals(T a, Object b) {
        return new ElementCompare().equalsInternal(a, b);
    }



    private <T extends Element> boolean equalsInternal(T a, Object b) {
        if (a == null || b == null) {
            return (a == null && b == null);
        }
        if (!a.getClass().isAssignableFrom(b.getClass())) {
            return false;
        }
        if (a instanceof ElementPathBlock) {
            return equals((ElementPathBlock) a, (ElementPathBlock) b);
        }
        if (a instanceof ElementTriplesBlock) {
            return equals((ElementTriplesBlock) a, (ElementTriplesBlock) b);
        }
        if (a instanceof ElementUnion) {
            return equals((ElementUnion) a, (ElementUnion) b);
        }
        if (a instanceof ElementOptional) {
            return equals((ElementOptional) a, (ElementOptional) b);
        }
        if (a instanceof ElementGroup) {
            return equals((ElementGroup) a, (ElementGroup) b);
        }
        if (a instanceof ElementDataset) {
            return equals((ElementDataset) a, (ElementDataset) b);
        }
        if (a instanceof ElementNamedGraph) {
            return equals((ElementNamedGraph) a, (ElementNamedGraph) b);
        }
        if (a instanceof ElementExists) {
            return equals((ElementExists) a, (ElementExists) b);
        }
        if (a instanceof ElementNotExists) {
            return equals((ElementNotExists) a, (ElementNotExists) b);
        }
        if (a instanceof ElementMinus) {
            return equals((ElementMinus) a, (ElementMinus) b);
        }
        if (a instanceof ElementService) {
            return equals((ElementService) a, (ElementService) b);
        }
        if (a instanceof ElementSubQuery) {
            return equals((ElementSubQuery) a, (ElementSubQuery) b);
        }
        if (a instanceof ElementFilter) {
            return equals((ElementFilter) a, (ElementFilter) b);
        }
        return defaultEquals(a, (Element) b);
    }

    private boolean defaultEquals(Element a, Element b) {
        return a != null && a.equalTo(b, isoMap);
    }

    private boolean equals(ElementPathBlock a, ElementPathBlock b) {
        return Utils.equalsIgnoreOrder(a.getPattern(), b.getPattern(), (x, y) -> Iso.triplePathIso(x, y, isoMap));
    }

    private boolean equals(ElementTriplesBlock a, ElementTriplesBlock b) {
        return Utils.equalsIgnoreOrder(a.getPattern(), b.getPattern(), (x, y) -> Iso.tripleIso(x, y, isoMap));
    }

    private boolean equals(ElementUnion a, ElementUnion b) {
        return Utils.equalsIgnoreOrder(a.getElements(), b.getElements(), (x, y) -> equalsInternal(x, y));
    }

    private boolean equals(ElementOptional a, ElementOptional b) {
        return equalsInternal(a.getOptionalElement(), b.getOptionalElement());
    }

    private boolean equals(ElementGroup a, ElementGroup b) {
        return Utils.equalsIgnoreOrder(a.getElements(), b.getElements(), (x, y) -> equalsInternal(x, y));
    }

    private boolean equals(ElementDataset a, ElementDataset b) {
        return Objects.equals(a.getDataset(), b.getDataset())
                && equalsInternal(a.getElement(), b.getElement());
    }

    private boolean equals(ElementNamedGraph a, ElementNamedGraph b) {
        return Objects.equals(a.getGraphNameNode(), b.getGraphNameNode())
                && equalsInternal(a.getElement(), b.getElement());
    }

    private boolean equals(ElementExists a, ElementExists b) {
        return equalsInternal(a.getElement(), b.getElement());
    }

    private boolean equals(ElementNotExists a, ElementNotExists b) {
        return equalsInternal(a.getElement(), b.getElement());
    }

    private boolean equals(ElementMinus a, ElementMinus b) {
        return equalsInternal(a.getMinusElement(), b.getMinusElement());
    }

    private boolean equals(ElementService a, ElementService b) {
        return Objects.equals(a.getServiceNode(), b.getServiceNode())
                && a.getSilent() == b.getSilent()
                && equalsInternal(a.getElement(), b.getElement());
    }

    private boolean equals(ElementSubQuery a, ElementSubQuery b) {
        return QueryCompare.equal(a.getQuery(), b.getQuery(), map);
    }

    private boolean equals(ElementFilter a, ElementFilter b) {
        if (a == null || b == null) {
            return false;
        }
        return ExpressionCompare.equals(a.getExpr(), b.getExpr(), map, isoMap);
        //also ignore ordering of filters
//        Expr exA = a.getExpr();
//        Expr exB = b.getExpr();
//        Expr exMapped = exA.applyNodeTransform((Node n) -> {
//            if (map.containsKey(n)) {
//                return map.get(n);
//            }
//            return n;
//        });
        // TODO sophisticated expression compare, needs to be recursive
        // e.g. ignore order for AND/OR
        // check values for </<=/>/>=, <= 5 equals < 6 (only for integer)
//        boolean result = exMapped.equalsBySyntax(exB);
//        return result;
    }

}
