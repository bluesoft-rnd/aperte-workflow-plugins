package org.aperteworkflow.contrib.script.groovy;

import groovy.lang.*;
import org.aperteworkflow.scripting.ScriptProcessor;
import org.aperteworkflow.scripting.ScriptValidationException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 2/24/12
 * Time: 11:27 AM
 */
public class GroovyScriptProcessor implements ScriptProcessor{


    @Override
    public Map<String, Object> process(Map<String, Object> vars, InputStream script) throws Exception {
        Binding binding = new Binding(vars);
        GroovyShell shell = new GroovyShell(binding);
        Reader reader = new InputStreamReader(script, "UTF-8");
        Object evaluate = shell.evaluate(reader);
        if(!(evaluate instanceof Map))
            return null;
        return (Map) evaluate;
    }

    @Override
    public void validate(InputStream script) throws ScriptValidationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
