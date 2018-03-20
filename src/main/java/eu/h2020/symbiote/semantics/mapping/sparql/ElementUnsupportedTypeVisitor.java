/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql;

import eu.h2020.symbiote.semantics.mapping.model.UnsupportedInputException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
public class ElementUnsupportedTypeVisitor implements ElementVisitor {

    public static final Class<? extends Element>[] RECURSIVE_ELEMENT_TYPES = new Class[]{
        ElementNamedGraph.class,
        ElementService.class,
        ElementSubQuery.class,        
        ElementUnion.class,
    };

    private UnsupportedInputException exception;
    private final List<Class<? extends Element>> unsupportedElementTypes;

    public static Optional<UnsupportedInputException> checkElement(Element element, Class<? extends Element>... unsupportedElementTypes) {
        ElementUnsupportedTypeVisitor visitor = new ElementUnsupportedTypeVisitor(unsupportedElementTypes);
        element.visit(visitor);
        return visitor.getException();
    }

    private ElementUnsupportedTypeVisitor(Class<? extends Element>... unsupportedElementTypes) {
        this.unsupportedElementTypes = Arrays.asList(unsupportedElementTypes);
    }

    public Optional<UnsupportedInputException> getException() {
        return Optional.ofNullable(exception);
    }

    private <T extends Element> boolean checkTypeUnsupported(T element) {
        return unsupportedElementTypes.contains(element.getClass());
    }

    @Override
    public void visit(ElementNamedGraph el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("named graphs not supported!");
        }
        if (el.getElement() != null) {
            el.getElement().visit(this);
        }
    }

    @Override
    public void visit(ElementService el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("SERVICE keyword not supported!");
        }
        if (el.getElement() != null) {
            el.getElement().visit(this);
        }
    }

    @Override
    public void visit(ElementSubQuery el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("subqueries not supported!");
        }
        if (el.getQuery().getQueryPattern() != null) {
            el.getQuery().getQueryPattern().visit(this);
        }
    }

    @Override
    public void visit(ElementBind el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("BIND not supported!");
        }
    }

    @Override
    public void visit(ElementUnion el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("UNION not supported!");
        }
        el.getElements().forEach(e -> e.visit(this));
    }

    @Override
    public void visit(ElementOptional el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("OPTIONAL not supported!");
        }
        if (el.getOptionalElement() != null) {
            el.getOptionalElement().visit(this);
        }
    }

    @Override
    public void visit(ElementGroup el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("group not supported!");
        }
        el.getElements().forEach(e -> e.visit(this));
    }

    @Override
    public void visit(ElementDataset el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("dataset not supported!");
        }
        if (el.getElement() != null) {
            el.getElement().visit(this);
        }
    }

    @Override
    public void visit(ElementExists el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("EXISTS not supported!");
        }
    }

    @Override
    public void visit(ElementNotExists el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("NOT EXISTS not supported!");
        }
    }

    @Override
    public void visit(ElementMinus el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("MINUS not supported!");
        }
        if (el.getMinusElement() != null) {
            el.getMinusElement().visit(this);
        }
    }

    @Override
    public void visit(ElementTriplesBlock el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("ElementTriplesBlock not supported!");
        }
    }

    @Override
    public void visit(ElementPathBlock el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("ElementPathBlock not supported!");
        }
    }

    @Override
    public void visit(ElementFilter el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("FILTER not supported!");
        }
    }

    @Override
    public void visit(ElementAssign el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("assign not supported!");
        }
    }

    @Override
    public void visit(ElementData el) {
        if (checkTypeUnsupported(el)) {
            exception = new UnsupportedInputException("ElementData not supported!");
        }
    }
}
