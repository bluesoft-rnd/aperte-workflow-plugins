package org.aperteworkflow.contrib.script.drools;

import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 3/6/12
 * Time: 5:08 PM
 */
public class DroolsScriptProcessorTest {

    @Test
    public void testDroolsEngine() throws Exception {
        DroolsScriptProcessor dsp = new DroolsScriptProcessor();
        InputStream stream = dsp.getClass().getResourceAsStream("/test.drl");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("var", "world");
        dsp.process(map, stream);
        System.out.println("Hello " + map.get("var"));
        stream = dsp.getClass().getResourceAsStream("/test.drl");
        dsp.process(map, stream);
    }
}
