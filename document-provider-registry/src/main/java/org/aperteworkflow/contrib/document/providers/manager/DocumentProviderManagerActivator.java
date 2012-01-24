package org.aperteworkflow.contrib.document.providers.manager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;

/**
 * @author tlipski@bluesoft.net.pl
 */
public class DocumentProviderManagerActivator implements BundleActivator {

    DocumentProviderRegistry registry = new DocumentProviderRegistry();
    private ServiceRegistration serviceRegistration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        serviceRegistration = bundleContext.registerService(DocumentProviderRegistry.class.getName(),
                registry,
                null);
        //TODO bundle listener and manifest property support - as in Mule Plugin
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (serviceRegistration != null)
            serviceRegistration.unregister();
    }
}
