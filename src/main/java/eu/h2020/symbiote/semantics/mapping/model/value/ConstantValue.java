/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.value;

import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class ConstantValue implements Value {

    private Object value;
    private RDFDatatype datatype;

    private ConstantValue() {
    }

    public ConstantValue(String value) {
        this.value = value;
        this.datatype = JenaHelper.datatypeFromString(value);
    }

    public ConstantValue(Object value) {
        this.value = value;
        this.datatype = JenaHelper.datatypeFromValue(value);
    }

    public ConstantValue(String value, RDFDatatype datatype) {
        Validate.notNull(datatype, "datatype most be non-null");
        try {
            this.value = datatype.parse(value);
        } catch (DatatypeFormatException e) {
            throw new IllegalArgumentException("value does not match datatype (value: " + value + ", datatype: " + datatype, e);
        }
        this.datatype = datatype;

    }

    public Object getValue() {
        return value;
    }

    public RDFDatatype getDatatype() {
        return datatype;
    }

    @Override
    public Node asNode() {
        return ResourceFactory.createTypedLiteral(datatype.unparse(value), datatype).asNode();
    }

    @Override
    public boolean validate() {
        return value != null
                && datatype != null
                && datatype.isValidValue(value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.value);
        hash = 37 * hash + Objects.hashCode(this.datatype);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConstantValue other = (ConstantValue) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.datatype, other.datatype)) {
            return false;
        }
        return true;
    }

}
