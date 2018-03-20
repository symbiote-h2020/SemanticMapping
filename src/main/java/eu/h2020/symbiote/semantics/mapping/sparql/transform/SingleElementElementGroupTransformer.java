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
public class SingleElementElementGroupTransformer extends ElementTransformerBase {

    @Override
    public Element transform(ElementGroup el) {
        if (el.size() == 1) {
            return el.get(0);
        }
        return el;
    }
}
