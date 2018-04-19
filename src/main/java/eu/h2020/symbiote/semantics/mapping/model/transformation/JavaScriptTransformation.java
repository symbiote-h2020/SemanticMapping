/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.transformation;

import eu.h2020.symbiote.semantics.mapping.model.Mapping;
import eu.h2020.symbiote.semantics.mapping.model.value.TransformationValue;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class JavaScriptTransformation implements Transformation {

    private final String name;
    private final String code;

    public JavaScriptTransformation(String name, String jsCode) {
        this.name = name;
        this.code = jsCode;
    }

    public JavaScriptTransformation(String jsCode) {
        this(UUID.randomUUID().toString(), jsCode);
    }

    @Override
    public Object evaluate(Object[] parameters) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.put("parameters", parameters);
        try {
            return engine.eval(getCode());
        } catch (Exception ex) {
            Logger.getLogger(TransformationValue.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
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
        final JavaScriptTransformation other = (JavaScriptTransformation) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.code);
        return hash;
    }

}
