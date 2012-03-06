package org.aperteworkflow.contrib.script.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.aperteworkflow.scripting.ScriptProcessor;
import org.aperteworkflow.scripting.ScriptValidationException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 2/24/12
 * Time: 11:27 AM
 */
public class GroovyScriptProcessor implements ScriptProcessor {

    private Logger logger = Logger.getLogger(GroovyScriptProcessor.class.getName());

    @Override
    public Map<String, Object> process(Map<String, Object> vars, InputStream script) throws Exception {
        Binding binding = new Binding(vars);
        GroovyShell shell = new GroovyShell(binding);
        Reader reader = new InputStreamReader(script, "UTF-8");
        Object evaluate = shell.evaluate(reader);
        if (!(evaluate instanceof Map))
            return null;
        return (Map) evaluate;
    }

    @Override
    public void validate(InputStream script) throws ScriptValidationException {
        try {
            GroovyShell shell = new GroovyShell();
            Reader reader = new InputStreamReader(script, "UTF-8");
            shell.parse(reader);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ScriptValidationException(e.getMessage());
        }

    }
}
