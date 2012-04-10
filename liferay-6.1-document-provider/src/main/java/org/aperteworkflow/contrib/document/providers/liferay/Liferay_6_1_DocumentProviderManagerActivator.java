package org.aperteworkflow.contrib.document.providers.liferay;

import org.aperteworkflow.contrib.document.providers.manager.DocumentProvider;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProviderRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import pl.net.bluesoft.rnd.util.func.Func;

/**
 * @author tlipski@bluesoft.net.pl
 */
public class Liferay_6_1_DocumentProviderManagerActivator implements BundleActivator {


    private DocumentProviderRegistry registry;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        registry = (DocumentProviderRegistry) bundleContext.getService(bundleContext.getServiceReference(
                DocumentProviderRegistry.class.getName()));
        registry.registerProvider("liferay-6.1", new Func<DocumentProvider>() {
            @Override
            public DocumentProvider invoke() {
                return new Liferay_6_1_DocumentProvider();
            }
        });
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        DocumentProviderRegistry registry = (DocumentProviderRegistry) bundleContext.getService(bundleContext.getServiceReference(
                DocumentProviderRegistry.class.getName()));
       registry.unregisterProvider("liferay-6.1");
    }
}
