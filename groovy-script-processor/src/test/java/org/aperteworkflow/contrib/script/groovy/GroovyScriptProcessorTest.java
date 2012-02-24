package org.aperteworkflow.contrib.script.groovy;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
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
        String scriptCode = IOUtils.toString(getClass().getResourceAsStream("/test.groovy"));
        long t1 = System.currentTimeMillis();
        gsp.configure(null, scriptCode);
        long t2 = System.currentTimeMillis();
        Map map = new HashMap();
        map.put("name", "World");
        long t3 = System.currentTimeMillis();
        gsp.processFields(map);
        long t4 = System.currentTimeMillis();
        System.out.println("Configuration time: " + (t2-t1));
        System.out.println("Execution time: " + (t4-t3));
        System.out.println(map.get("name"));


    }
}
