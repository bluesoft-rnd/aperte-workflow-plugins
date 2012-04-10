package org.aperteworkflow.contrib.script.groovy;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 2/24/12
 * Time: 12:00 PM
 */

public class GroovyScriptProcessorTest {


    @Test
    public void testScriptLoading() throws Exception {
        GroovyScriptProcessor gsp = new GroovyScriptProcessor();
        InputStream scriptCode = getClass().getResourceAsStream("/test.groovy");
        Map map = new HashMap();
        map.put("name", "World");
        long t3 = System.currentTimeMillis();
        gsp.process(map, scriptCode);
        long t4 = System.currentTimeMillis();
        System.out.println("Execution time: " + (t4-t3));

        scriptCode = getClass().getResourceAsStream("/test.groovy");
        long t5 = System.currentTimeMillis();
        gsp.process(map, scriptCode);
        long t6 = System.currentTimeMillis();
        System.out.println("Execution time (2nd): " + (t6-t5));


    }
}
