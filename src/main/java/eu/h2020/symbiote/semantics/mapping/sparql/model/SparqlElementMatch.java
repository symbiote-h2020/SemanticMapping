/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.sparql.model;

import org.apache.jena.query.Query;
import org.apache.jena.sparql.syntax.Element;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public interface SparqlElementMatch {
    public Element getSourceElement();
    public void removeFromQuery(Query query);
}
