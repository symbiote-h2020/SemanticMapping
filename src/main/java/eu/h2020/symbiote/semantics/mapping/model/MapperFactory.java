/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.h2020.symbiote.semantics.mapping.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Jacoby <michael.jacoby@iosb.fraunhofer.de>
 */
public class MapperFactory {
    private MapperFactory() {
    }
    
//    public static Mapper getMapper(String classname, Map<String, String> parameter) throws ClassNotFoundException, SecurityException {
//        Class<?> forName = Class.forName(classname);
//        if (!Mapper.class.isAssignableFrom(forName)) {
//            throw new IllegalArgumentException("class '" + classname + "' must be subclass of " + Mapper.class.getName());
//        }
//        try {            
//            Constructor<?> constructor = forName.getConstructor();
//            Mapper result = (Mapper)constructor.newInstance();
//            result.init(parameter);
//            return result;
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(MapperFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            Logger.getLogger(MapperFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(MapperFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(MapperFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(MapperFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
