/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.condition;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.ext.com.google.common.base.Objects;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class DataPropertyTypeCondition extends DataPropertyCondition {

    private RDFDatatype datatype;

    private DataPropertyTypeCondition() {
    }

    public DataPropertyTypeCondition(Path path, RDFDatatype datatype) {
        super(path);
        this.datatype = datatype;
    }

    public DataPropertyTypeCondition(String path, RDFDatatype datatype) {
        super(path);
        this.datatype = datatype;
    }

    public DataPropertyTypeCondition(String path, String datatype) {
        super(path);
        this.datatype = TypeMapper.getInstance().getSafeTypeByName(datatype);
    }

    public DataPropertyTypeCondition(Path path, String datatype) {
        super(path);
        this.datatype = TypeMapper.getInstance().getSafeTypeByName(datatype);
    }

    @Override
    public <R, P> R accept(ConditionVisitor<R, P> visitor, P args) {
        return visitor.visit(this, args);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + java.util.Objects.hashCode(this.datatype);
        return hash;
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (!super.looseEquals(obj)) {
            return false;
        }
        if (!(obj instanceof DataPropertyCondition)) {
            return false;
        }
        DataPropertyTypeCondition other = (DataPropertyTypeCondition) obj;
        return Objects.equal(other.datatype, datatype);
    }

    @Override
    public boolean validate() {
        return super.validate()
                && datatype != null;
    }

    public RDFDatatype getDatatype() {
        return datatype;
    }

    public void setDatatype(RDFDatatype datatype) {
        this.datatype = datatype;
    }
}
