/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;

import eu.h2020.symbiote.semantics.mapping.model.MappingContext;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public interface ProductionVisitorVoid extends ProductionVisitor<Void, Void, Void, Void> {
  
        
    public default void visit(Production production) {
        production.accept(null, this, null);
    }
    public void visit(IndividualProduction production);
    public void visit(ClassProduction production);    
    public void visit(DataPropertyProduction production);
    public void visit(ObjectPropertyTypeProduction production);
    public void visit(ObjectPropertyValueProduction production);

    @Override
    public default Void getResult() {
        return null;
    }

    @Override
    public default void init(Void input) {
    }
    
    @Override
    public default Void visit(MappingContext context, Production production, Void temp) {
        production.accept(context, this, temp);
        return null;
    }

    @Override
    public default Void visit(MappingContext context, IndividualProduction production, Void temp) {
        visit(production);
        return null;
    }

    @Override
    public default Void visit(MappingContext context, ClassProduction production, Void temp) {
        visit(production);
        return null;
    }

    @Override
    public default Void visit(MappingContext context, DataPropertyProduction production, Void temp) {
        visit(production);
        return null;
    }

    @Override
    public default Void visit(MappingContext context, ObjectPropertyTypeProduction production, Void temp) {
        visit(production);
        return null;
    }

    @Override
    public default Void visit(MappingContext context, ObjectPropertyValueProduction production, Void temp) {
        visit(production);
        return null;
    }
}
