/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.transform;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.lang.ParserSPARQL11;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
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
import org.apache.jena.sparql.syntax.ElementVisitor;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class QueryTransformer implements ElementVisitor {

    private final Deque<Element> stack = new ArrayDeque<>();
    private final List<ElementTransformer> transformers;

    public QueryTransformer(ElementTransformer... transformers) {
        this.transformers = Arrays.asList(transformers);
    }

    public static void transform(Query query, ElementTransformer... elementTransformers) {     
        QueryTransformer transformer = new QueryTransformer(elementTransformers);
        query.getQueryPattern().visit(transformer);
        query.setQueryPattern(transformer.result());
    }

    protected final Element pop() {
        return stack.pop();
    }

    protected final List<Element> pop(int n) {
        List<Element> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(stack.pop());
        }
        return result;
    }

    protected final void push(Element elt) {
        stack.push(elt);
    }

    public final Element result() {
        if (stack.size() != 1) {
            ElementGroup result = new ElementGroup();
            stack.forEach(x -> result.addElement(x));
            return result;
        }
        return pop();
    }

    @Override
    public void visit(ElementTriplesBlock el) {
        visitSimple(el);
    }

    @Override
    public void visit(ElementPathBlock el) {
        visitSimple(el);
    }

    @Override
    public void visit(ElementFilter el) {
        visitSimple(el);
    }

    @Override
    public void visit(ElementAssign el) {
        visitSimple(el);
    }

    @Override
    public void visit(ElementBind el) {
        visitSimple(el);
    }

    @Override
    public void visit(ElementData el) {
        visitSimple(el);
    }

    private void visitSimple(Element element) {
        push(element);
    }

    private <T extends Element> void visitWithSub(T element, Element subElement, Function<Element, Element> createNewElement) {
        visitWithSub(element, Arrays.asList(subElement), x -> createNewElement.apply(x.get(0)));
    }

    private <T extends Element> void visitWithSub(T element, List<Element> subElements, Function<List<Element>, Element> createNewElement) {
        subElements.forEach(x -> x.visit(this));
        List<Element> newSubElements = new ArrayList<>(subElements.size());
        // collect new sub elements
        boolean changed = false;
        for (int i = 0; i < subElements.size(); i++) {
            changed |= subElements.get(i) != stack.peek();
            newSubElements.add(0,pop());
        }
        if (changed) {
            push(transform(createNewElement.apply(newSubElements)));
        } else {
            push(transform(element));
        }
    }
    
    private Element transform(Element element) {
        if (transformers == null || transformers.isEmpty()) {
            return element;
        } 
        Element result = element;
        for (int i = 0; i < transformers.size(); i++) {
            result = transformers.get(i).transform(result);
        }
        return result;
    }

    @Override
    public void visit(ElementGroup el) {
        visitWithSub(el,
                el.getElements(),
                x -> {
                    ElementGroup result = new ElementGroup();
                    x.forEach(y -> result.addElement(y));
                    return result;
                });
    }

    @Override
    public void visit(ElementOptional el) {
        visitWithSub(el,
                el.getOptionalElement(),
                x -> {
                    return new ElementOptional(x);
                });
    }

    @Override
    public void visit(ElementNamedGraph el) {
        visitWithSub(el,
                el.getElement(),
                x -> {
                    return new ElementNamedGraph(x);
                });
    }

    @Override
    public void visit(ElementService el) {
        visitWithSub(el,
                el.getElement(),
                x -> {
                    return new ElementService(el.getServiceNode(), x, el.getSilent());
                });
    }

    @Override
    public void visit(ElementMinus el) {
        visitWithSub(el,
                el.getMinusElement(),
                x -> {
                    return new ElementMinus(x);
                });
    }

    @Override
    public void visit(ElementUnion el) {
        visitWithSub(el,
                el.getElements(),
                x -> {
                    ElementUnion result = new ElementUnion();
                    x.forEach(y -> result.addElement(y));
                    return result;
                });
    }

    @Override
    public void visit(ElementDataset el) {
        visitWithSub(el,
                el.getElement(),
                x -> {
                    return new ElementDataset(el.getDataset(), x);
                });
    }

    @Override
    public void visit(ElementExists el) {
        visitWithSub(el,
                el.getElement(),
                x -> {
                    return new ElementExists(x);
                });
    }

    @Override
    public void visit(ElementNotExists el) {
        visitWithSub(el,
                el.getElement(),
                x -> {
                    return new ElementNotExists(x);
                });
    }

    @Override
    public void visit(ElementSubQuery el) {
        visitWithSub(el,
                el.getQuery().getQueryPattern(),
                x -> {
                    ElementSubQuery newQuery = new ElementSubQuery(el.getQuery());
                    newQuery.getQuery().setQueryPattern(x);
                    return newQuery;
                });
    }

//    @Override
//    public Element transform(ElementSubQuery el, Query newQuery) {
//        // check if subquery can be resolved
//        boolean resolvable = true;
//        if (!el.getQuery().isSelectType()
//                || el.getQuery().hasGroupBy()
//                || el.getQuery().hasAggregators()
//                || el.getQuery().hasOffset()
//                || el.getQuery().hasLimit()
//                || el.getQuery().hasDatasetDescription()) {
//            resolvable = false;
//        }
//        // not allowed to go any deeper
//        if (ElementUnsupportedTypeVisitor.checkElement(el.getQuery().getQueryPattern(), UNSUPPORTED_ELEMENT_TYPES).isPresent()) {
//            resolvable = false;
//        }
//        if (!resolvable) {
//            return super.transform(el, newQuery);
//        }
//        // rename not projected vars
//        Collection<Var> varsToRename = PatternVars.vars(el.getQuery().getQueryPattern());
//        varsToRename.removeAll(el.getQuery().getProjectVars());
//        Map<Var, Var> varMapping = varsToRename.stream()
//                .collect(Collectors.toMap(
//                        x -> x, 
//                        x -> Rename.chooseVarName(x, usedVarsInQuery, "")));
//        
//        Query renamedQuery = QueryTransformOps.transform(newQuery, varMapping);
//        // merge content of query         
//        return renamedQuery.getQueryPattern();
//    }
}
