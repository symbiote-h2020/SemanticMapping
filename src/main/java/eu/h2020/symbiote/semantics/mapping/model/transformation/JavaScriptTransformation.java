/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.transformation;

import eu.h2020.symbiote.semantics.mapping.model.value.TransformationValue;
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
    private final String jsCode;

    public JavaScriptTransformation(String name, String jsCode) {
        this.name = name;
        this.jsCode = jsCode;
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
            return engine.eval(getJsCode());
        } catch (ScriptException ex) {
            Logger.getLogger(TransformationValue.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getJsCode() {
        return jsCode;
    }

}
