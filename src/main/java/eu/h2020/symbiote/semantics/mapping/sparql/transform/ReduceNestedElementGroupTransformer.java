/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.transform;

import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ReduceNestedElementGroupTransformer extends ElementTransformerBase {

    @Override
    public Element transform(ElementGroup el) {
        if (el.getElements().stream().anyMatch(x -> x instanceof ElementGroup)) {
            ElementGroup result = new ElementGroup();
            el.getElements().forEach(x -> {
                if (x instanceof ElementGroup) {
                    ((ElementGroup) x).getElements().forEach(e -> result.addElement(transform(e)));
                } else {
                    result.addElement(x);
                }
            });
            return result;
        } else {
            return el;
        }
    }
}
