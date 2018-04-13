/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class TransformationRegistry {

    private static volatile TransformationRegistry globalInstance;
    private final Map<String, Transformation> registry;

    public static synchronized TransformationRegistry getGlobal() {
        if (globalInstance == null) {
            globalInstance = new TransformationRegistry();
            globalInstance.findDefineTransformation();
        }
        return globalInstance;
    }

    public static TransformationRegistry getCopy() {
        TransformationRegistry result = new TransformationRegistry();
        result.registry.putAll(getGlobal().registry);
        return result;
    }

    private TransformationRegistry() {
        registry = new HashMap<>();
    }

    private void findDefineTransformation() {
        new Reflections("eu.h2020.symbiote.semantics.mapping")
                .getSubTypesOf(Transformation.class).stream()
                .filter(x -> x.isAnnotationPresent(Name.class))
                .filter(x -> Stream.of(x.getDeclaredConstructors()).anyMatch(c -> c.getParameterCount() == 0))
                .forEach(x -> {
                    try {
                        registry.put(x.getAnnotation(Name.class).value(), x.newInstance());
                    } catch (InstantiationException ex) {
                        Logger.getLogger(TransformationRegistry.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(TransformationRegistry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
    }

    public void register(List<? extends Transformation> transformations) {
        transformations.forEach(x -> TransformationRegistry.this.register(x));
    }

    public void register(Transformation transformation) {
        if (registry.containsKey(transformation.getName())) {
            throw new IllegalArgumentException("transformation with name '" + transformation.getName() + "' already registered");
        }
        registry.put(transformation.getName(), transformation);
    }

    public void unregister(Transformation transformation) {
        if (registry.containsKey(transformation.getName())) {
            registry.remove(transformation.getName());
        }
    }

    public Object evaluateTransformation(String name, Object[] parameters) {
        if (!registry.containsKey(name)) {
            throw new IllegalArgumentException("transformation with name '" + name + "' not registered");
        }
        return registry.get(name).evaluate(parameters);
    }
}
