package org.aperteworkflow.contrib.document.providers.manager;

import org.osgi.framework.*;
import pl.net.bluesoft.rnd.util.func.Func;

import java.util.Dictionary;

import static pl.net.bluesoft.util.lang.StringUtil.hasText;

/**
 * @author tlipski@bluesoft.net.pl
 */
public class DocumentProviderManagerActivator implements BundleActivator {

    DocumentProviderRegistry registry;
    private ServiceRegistration serviceRegistration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        registry = new DocumentProviderRegistry();
        serviceRegistration = bundleContext.registerService(DocumentProviderRegistry.class.getName(),
                registry,
                null);
        bundleContext.addBundleListener(new BundleListener() {

            @Override
            public void bundleChanged(BundleEvent event) {
                processBundleExtensions(event.getBundle(),
                        event.getType(),
                        registry);
            }

            private void processBundleExtensions(Bundle bundle, int state, DocumentProviderRegistry registry) {
                String muleCfgs = (String) bundle.getHeaders().get("Doc-Provider-Config-Files");
                if (hasText(muleCfgs)) {
                    String[] names = muleCfgs.split(",");
                    for (String name : names) {
                        name = name.trim();
                        if (state == BundleEvent.STARTED) {
                            try {
//TODO: what here?

                            } catch (Exception e) {
//                               TODO
                            }


                        }
                    }
                }
            }

        });
            //TODO bundle listener and manifest property support - as in Mule Plugin
        }

        @Override
        public void stop (BundleContext bundleContext)throws Exception {
            if (serviceRegistration != null)
                serviceRegistration.unregister();
        }
    }
