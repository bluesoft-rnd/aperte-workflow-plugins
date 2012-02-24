package org.aperteworkflow.contrib.script.groovy;

import groovy.lang.*;
import groovy.util.GroovyScriptEngine;
import org.aperteworkflow.scripting.ScriptProcessor;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 2/24/12
 * Time: 11:27 AM
 */
public class GroovyScriptProcessor implements ScriptProcessor{

    private Script script;

    @Override
    public void processFields(Map<String, Object> fields) throws Exception {
        Binding binding = new Binding(fields);
        script.setBinding(binding);
        script.run();
    }

    @Override
    public void configure(String url, String code)  {
        try {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class scriptClass = gcl.parseClass(code);
            script = (Script) scriptClass.newInstance();
        } catch (Exception e) {
            throw new GroovyRuntimeException(e);
        }

    }
}
