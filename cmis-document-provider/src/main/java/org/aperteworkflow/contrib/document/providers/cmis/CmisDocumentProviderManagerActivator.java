package org.aperteworkflow.contrib.document.providers.cmis;

import org.aperteworkflow.contrib.document.providers.manager.Document;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProvider;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProviderRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import pl.net.bluesoft.rnd.util.func.Func;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

/**
 * @author tlipski@bluesoft.net.pl
 */
public class CmisDocumentProviderManagerActivator implements BundleActivator {

    

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        DocumentProviderRegistry registry = (DocumentProviderRegistry) bundleContext.getService(bundleContext.getServiceReference(
                DocumentProviderRegistry.class.getName()));
        registry.registerProvider("cmis", new Func<DocumentProvider>() {
            @Override
            public DocumentProvider invoke() {
                return new DocumentProvider() {//TODO add real implementation
                    @Override
                    public void configure(Map<String, String> properties) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void uploadDocument(Document doc) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public Collection<Document> getDocuments(String path) {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                };
            }
        });
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        DocumentProviderRegistry registry = (DocumentProviderRegistry) bundleContext.getService(bundleContext.getServiceReference(
                DocumentProviderRegistry.class.getName()));
        //TODO registry.unregisterProvider("cmis");
    }
}
