/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.algebra.walker.Walker;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprFunction2;
import org.apache.jena.sparql.expr.ExprTransformBase;
import org.apache.jena.sparql.syntax.Element;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class SparqlHavingExpressionMatch implements SparqlElementMatch {

    private final Expr expr;

    public SparqlHavingExpressionMatch(Expr expr) {
        this.expr = expr;
    }

    @Override
    public Element getSourceElement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeFromQuery(Query query) {
        List<Expr> old = new ArrayList<>(query.getHavingExprs());
        query.getHavingExprs().clear();
        old.stream()
                .map(x -> Walker.transform(x, new ExprTransformBase() {
            @Override
            public Expr transform(ExprFunction2 func, Expr expr1, Expr expr2) {
                if (func.equalsBySyntax(expr)) {
                    return Expr.NONE;
                }
                return func;
            }
        }))
                .forEach(x -> {
                    if (!x.equals(Expr.NONE)) {
                        query.getHavingExprs().add(x);
                    }
                });
    }

}
