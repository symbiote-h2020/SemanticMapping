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
import eu.h2020.symbiote.semantics.mapping.model.production.ProductionVisitorSimple;
import eu.h2020.symbiote.semantics.mapping.model.value.ReferenceValue;
import eu.h2020.symbiote.semantics.mapping.model.value.TransformationValue;
import eu.h2020.symbiote.semantics.mapping.model.value.Value;
import java.util.List;
import java.util.stream.Stream;
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
public class DataProductionVisitor extends AbstractProductionVisitor<OntModel, List<IndividualMatch>, Void, OntModel> implements ProductionVisitorSimple<OntModel, List<IndividualMatch>, OntModel> {

    private OntModel model;

    @Override
    public void visit(IndividualProduction production, MappingContext context, List<IndividualMatch> args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(ClassProduction production, MappingContext context, List<IndividualMatch> args) {
        remove(args);
        args.forEach(x -> add(x.getIndividual(), RDF.type, production.getUri()));
    }

    @Override
    public void visit(DataPropertyProduction production, MappingContext context, List<IndividualMatch> args) {
        remove(args);
        args.forEach(x -> {
            List<Node> values = Value.eval(production.getValue(), context, x.getValues());
            values.forEach(v -> add(x.getIndividual(), production.getPath(), v));
        });
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
    public void visit(ObjectPropertyTypeProduction production, MappingContext context, List<IndividualMatch> args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(ObjectPropertyValueProduction production, MappingContext context, List<IndividualMatch> args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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
}
