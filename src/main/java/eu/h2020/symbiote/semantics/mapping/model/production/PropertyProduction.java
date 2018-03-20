/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.production;


import eu.h2020.symbiote.semantics.mapping.sparql.utils.JenaHelper;
import java.util.Objects;
import org.apache.jena.sparql.path.Path;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public abstract class PropertyProduction implements Production {

    private Path path;

    protected PropertyProduction() {
    }

    protected PropertyProduction(Path path) {
        this.path = path;
    }

    protected PropertyProduction(String path) {
        this.path = JenaHelper.parsePath(path);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() && looseEquals(obj);
    }

    @Override
    public boolean looseEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PropertyProduction)) {
            return false;
        }
        final PropertyProduction other = (PropertyProduction) obj;
        return Objects.equals(this.path, other.path);
    }

    @Override
    public boolean validate() {
        return path != null;
    }
}
