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
public interface ElementTransformer {
    public Element transform(ElementTriplesBlock el) ;
    public Element transform(ElementPathBlock el) ;
    public Element transform(ElementFilter el) ;
    public Element transform(ElementAssign el) ;
    public Element transform(ElementBind el) ;
    public Element transform(ElementData el) ;
    public Element transform(ElementUnion el) ;
    public Element transform(ElementOptional el) ;
    public Element transform(ElementGroup el) ;
    public Element transform(ElementDataset el) ;
    public Element transform(ElementNamedGraph el) ;
    public Element transform(ElementExists el) ;
    public Element transform(ElementNotExists el) ;
    public Element transform(ElementMinus el) ;
    public Element transform(ElementService el) ;
    public Element transform(ElementSubQuery el) ;
    
    public default Element transform(Element element) {
        if (element instanceof ElementTriplesBlock) {
            return transform((ElementTriplesBlock)element);
        }
        if (element instanceof ElementPathBlock) {
            return transform((ElementPathBlock)element);
        }
        if (element instanceof ElementFilter) {
            return transform((ElementFilter)element);
        }
        if (element instanceof ElementAssign) {
            return transform((ElementAssign)element);
        }
        if (element instanceof ElementBind) {
            return transform((ElementBind)element);
        }
        if (element instanceof ElementData) {
            return transform((ElementData)element);
        }
        if (element instanceof ElementUnion) {
            return transform((ElementUnion)element);
        }
        if (element instanceof ElementOptional) {
            return transform((ElementOptional)element);
        }
        if (element instanceof ElementGroup) {
            return transform((ElementGroup)element);
        }
        if (element instanceof ElementDataset) {
            return transform((ElementDataset)element);
        }
        if (element instanceof ElementNamedGraph) {
            return transform((ElementNamedGraph)element);
        }
        if (element instanceof ElementExists) {
            return transform((ElementExists)element);
        }
        if (element instanceof ElementNotExists) {
            return transform((ElementNotExists)element);
        }
        if (element instanceof ElementMinus) {
            return transform((ElementMinus)element);
        }
        if (element instanceof ElementService) {
            return transform((ElementService)element);
        }
        if (element instanceof ElementSubQuery) {
            return transform((ElementSubQuery)element);
        }
        return element;
    }
}
