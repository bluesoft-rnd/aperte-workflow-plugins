package org.aperteworkflow.contrib.script.drools;

import org.aperteworkflow.scripting.ScriptProcessor;
import org.aperteworkflow.scripting.ScriptValidationException;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 3/6/12
 * Time: 4:14 PM
 */
public class DroolsScriptProcessor implements ScriptProcessor {

    private Logger logger = Logger.getLogger(DroolsScriptProcessor.class.getName());

    private KnowledgeBase kbase;
    private StatelessKnowledgeSession session;
    private StatefulKnowledgeSession s;

    @Override
    public Map<String, Object> process(Map<String, Object> stringObjectMap, InputStream inputStream) throws Exception {

        kbase = readKnowledgeBase(inputStream);
        session = kbase.newStatelessKnowledgeSession();

        if (session != null)
            session.execute(stringObjectMap);

        return stringObjectMap;
    }

    @Override
    public void validate(InputStream inputStream) throws ScriptValidationException {
        try {
            readKnowledgeBase(inputStream);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ScriptValidationException(e.getMessage());
        }
    }

    public static KnowledgeBase readKnowledgeBase(InputStream resource) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(resource), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }
}
