/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.transform;

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

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ElementTransformerBase implements ElementTransformer {

    @Override
    public Element transform(ElementTriplesBlock el) {
        return el;
    }

    @Override
    public Element transform(ElementPathBlock el) {
        return el;
    }

    @Override
    public Element transform(ElementFilter el) {
        return el;
    }

    @Override
    public Element transform(ElementAssign el) {
        return el;
    }

    @Override
    public Element transform(ElementBind el) {
        return el;
    }

    @Override
    public Element transform(ElementData el) {
        return el;
    }

    @Override
    public Element transform(ElementUnion el) {
        return el;
    }

    @Override
    public Element transform(ElementOptional el) {
        return el;
    }

    @Override
    public Element transform(ElementGroup el) {
        return el;
    }

    @Override
    public Element transform(ElementDataset el) {
        return el;
    }

    @Override
    public Element transform(ElementNamedGraph el) {
        return el;
    }

    @Override
    public Element transform(ElementExists el) {
        return el;
    }

    @Override
    public Element transform(ElementNotExists el) {
        return el;
    }

    @Override
    public Element transform(ElementMinus el) {
        return el;
    }

    @Override
    public Element transform(ElementService el) {
        return el;
    }

    @Override
    public Element transform(ElementSubQuery el) {
        return el;
    }
    
}
