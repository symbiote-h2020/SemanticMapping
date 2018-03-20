/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
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
public class ElementParentVisitor implements ElementVisitor {

    private Map<Element, Element> hierarchy = new HashMap<>();
    private Stack<Element> parents = new Stack<>();

    private void addToHierarchy(Element element) {
        getHierarchy().put(element, parents.peek());
    }

    @Override
    public void visit(ElementTriplesBlock el) {
        addToHierarchy(el);
    }

    @Override
    public void visit(ElementPathBlock el) {
        addToHierarchy(el);
    }

    @Override
    public void visit(ElementFilter el) {
        addToHierarchy(el);
    }

    @Override
    public void visit(ElementAssign el) {
        addToHierarchy(el);
    }

    @Override
    public void visit(ElementBind el) {
        // maybe do some renaming magic
        addToHierarchy(el);
    }

    @Override
    public void visit(ElementData el) {
        addToHierarchy(el);
    }

    @Override
    public void visit(ElementUnion el) {
        addToHierarchy(el);
        el.getElements().forEach(x -> x.visit(this));
    }

    @Override
    public void visit(ElementOptional el) {
        addToHierarchy(el);
        parents.push(el);
        el.getOptionalElement().visit(this);
        parents.pop();
    }

    @Override
    public void visit(ElementGroup el) {
        addToHierarchy(el);
        parents.push(el);
        el.getElements().forEach(x -> x.visit(this));
        parents.pop();
    }

    @Override
    public void visit(ElementDataset el) {
        addToHierarchy(el);
        parents.push(el);
        el.getElement().visit(this);
        parents.pop();
    }

    @Override
    public void visit(ElementNamedGraph el) {
        addToHierarchy(el);
        parents.push(el);
        el.getElement().visit(this);
        parents.pop();
    }

    @Override
    public void visit(ElementExists el) {
        addToHierarchy(el);
        parents.push(el);
        el.getElement().visit(this);
        parents.pop();
    }

    @Override
    public void visit(ElementNotExists el) {
        addToHierarchy(el);
        parents.push(el);
        el.getElement().visit(this);
        parents.pop();
    }

    @Override
    public void visit(ElementMinus el) {
        addToHierarchy(el);
        parents.push(el);
        el.getMinusElement().visit(this);
        parents.pop();
    }

    @Override
    public void visit(ElementService el) {
        addToHierarchy(el);
        parents.push(el);
        el.getElement().visit(this);
        parents.pop();
    }

    @Override
    public void visit(ElementSubQuery el) {
        addToHierarchy(el);
        parents.push(el);
        el.getQuery().getQueryPattern().visit(this);
        parents.pop();
    }

    public Map<Element, Element> getHierarchy() {
        return hierarchy;
    }

}
