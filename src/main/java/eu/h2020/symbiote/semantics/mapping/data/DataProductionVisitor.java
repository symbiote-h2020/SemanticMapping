/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.data;

import eu.h2020.symbiote.semantics.mapping.model.MappingContext;
import eu.h2020.symbiote.semantics.mapping.model.production.AbstractProductionVisitor;
import eu.h2020.symbiote.semantics.mapping.model.production.ClassProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.DataPropertyProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.IndividualProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyTypeProduction;
import eu.h2020.symbiote.semantics.mapping.model.production.ObjectPropertyValueProduction;
import eu.h2020.symbiote.semantics.mapping.model.value.TransformationValue;
import java.util.List;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ModelGraphInterface;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataProductionVisitor extends AbstractProductionVisitor<OntModel, List<IndividualMatch>, Void, OntModel> {

    private OntModel model;

    private void remove(List<IndividualMatch> matches) {
        matches.stream().flatMap(x -> x.getElementMatches().stream())
                .filter(x -> x instanceof TripleMatch)
                .map(x -> ((TripleMatch) x).getTriple())
                .forEach(x -> model.remove(model.asStatement(x)));
    }

    public void add(Node subject, Node predicate, Node object) {
        model.add(model.asStatement(new Triple(subject, predicate, object)));
    }

    public void add(Individual subject, Node predicate, Node object) {
        add(subject.asNode(), predicate, object);
    }

    public void add(Individual subject, Property predicate, Node object) {
        add(subject.asNode(), predicate.asNode(), object);
    }

    public void add(Individual subject, Path predicate, Node object) {
        if (!(predicate instanceof P_Link)) {
            throw new UnsupportedOperationException("only simple property paths supported!");
        }
        add(subject, ((P_Link) predicate).getNode(), object);
    }

    public void add(Node subject, Path predicate, Node object) {
        if (!(predicate instanceof P_Link)) {
            throw new UnsupportedOperationException("only simple property paths supported!");
        }
        add(subject, ((P_Link) predicate).getNode(), object);
    }

    @Override
    public void init(OntModel input) {
        super.init(input);
        model = ModelFactory.createOntologyModel(input.getSpecification(), input);
    }

    @Override
    public OntModel getResult() {
        return model;
    }

    @Override
    public Void visit(MappingContext context, IndividualProduction production, List<IndividualMatch> temp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visit(MappingContext context, ClassProduction production, List<IndividualMatch> temp) {
        remove(temp);
        temp.forEach(x -> add(x.getIndividual(), RDF.type, production.getUri()));
        return null;
    }

    @Override
    public Void visit(MappingContext context, DataPropertyProduction production, List<IndividualMatch> temp) {
        remove(temp);
//        for (SparqlMatch match : temp) {
//            Node value = null;
//            if (production.getValue() instanceof TransformationValue) {
//                TransformationValue transform = ((TransformationValue) production.getValue());
//                value = transform.eval(context, match.getValues()).asNode();
//            } else {
//                value = production.getValue().asNode();
//            }
//            remove(query, match.getMatchedElements());
//            add(query, match.getMatchedNode(), production.getPath(), value);
//        }
//        return null;
        temp.forEach(x -> {
            Node value = null;
            if (production.getValue() instanceof TransformationValue) {
                TransformationValue transform = ((TransformationValue) production.getValue());
                value = transform.eval(context, x.getValues()).asNode();
            } else {
                value = production.getValue().asNode();
            }
            add(x.getIndividual(), production.getPath(), value);
        });
        return null;
    }

    @Override
    public Void visit(MappingContext context, ObjectPropertyTypeProduction production, List<IndividualMatch> temp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visit(MappingContext context, ObjectPropertyValueProduction production, List<IndividualMatch> temp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
