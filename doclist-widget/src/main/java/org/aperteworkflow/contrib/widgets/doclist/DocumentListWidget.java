package org.aperteworkflow.contrib.widgets.doclist;

import com.vaadin.Application;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.aperteworkflow.contrib.document.providers.manager.Document;
import org.aperteworkflow.contrib.document.providers.manager.DocumentImpl;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProvider;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProviderRegistry;
import pl.net.bluesoft.rnd.processtool.ProcessToolContext;
import pl.net.bluesoft.rnd.processtool.bpm.ProcessToolBpmSession;
import pl.net.bluesoft.rnd.processtool.model.ProcessInstance;
import pl.net.bluesoft.rnd.processtool.model.config.ProcessStateConfiguration;
import pl.net.bluesoft.rnd.processtool.model.config.ProcessStateWidget;
import pl.net.bluesoft.rnd.processtool.ui.widgets.ProcessToolDataWidget;
import pl.net.bluesoft.rnd.processtool.ui.widgets.ProcessToolVaadinWidget;
import pl.net.bluesoft.rnd.processtool.ui.widgets.ProcessToolWidget;
import pl.net.bluesoft.rnd.processtool.ui.widgets.annotations.AliasName;
import pl.net.bluesoft.rnd.processtool.ui.widgets.annotations.AutoWiredProperty;
import pl.net.bluesoft.rnd.processtool.ui.widgets.impl.BaseProcessToolWidget;
import pl.net.bluesoft.rnd.util.i18n.I18NSource;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 1/25/12
 * Time: 1:49 PM
 */
@AliasName(name = "DocumentList")
public class DocumentListWidget extends BaseProcessToolWidget implements ProcessToolDataWidget,
        ProcessToolVaadinWidget {

    public static final String KV_SEPARATOR = "=";
    public static final String PROP_SEPARATOR = ";";
    private VerticalLayout vl;
    private Component documentListComponent;
//    private DocumentProvider documentProvider;
    private String login;
    private ProcessInstance processInstance;

    @AutoWiredProperty
    private String documentProviderConfig = null;


    @AutoWiredProperty
    private String documentProviderName = "liferay";
    private DocumentProviderRegistry providerRegistry;
    private Long companyId;

    @Override
    public void setContext(ProcessStateConfiguration state, ProcessStateWidget configuration, I18NSource i18NSource,
                           ProcessToolBpmSession bpmSession, Application application, Set<String> permissions, boolean isOwner) {
        super.setContext(state, configuration, i18NSource, bpmSession, application, permissions, isOwner);

        login = bpmSession.getUserLogin();
        companyId = bpmSession.getUser(ProcessToolContext.Util.getProcessToolContextFromThread()).getCompanyId();

    }

    private Map<String, String> parseProperties() {
        HashMap<String, String> map = new HashMap<String, String>();
        if(documentProviderConfig == null)
            documentProviderConfig = getDefaultConfig();
        String[] properties = documentProviderConfig.split(";");
        for (String p : properties) {
            String[] kv = p.split(KV_SEPARATOR);
            if (kv.length == 2)
                map.put(kv[0], kv[1]);
        }
        return map;
    }

    private String getDefaultConfig() {
        StringBuilder sb = new StringBuilder();
        sb.append(createProperty(DocumentProvider.ATOM_URL, "http://pirx:8080/nuxeo/atom/cmis"));
        sb.append(createProperty(DocumentProvider.FOLDER_NAME, processInstance.getInternalId()));
        sb.append(createProperty(DocumentProvider.NEW_FOLDER_PREFIX, ""));
        sb.append(createProperty(DocumentProvider.PASS, "Administrator"));
        sb.append(createProperty(DocumentProvider.REPOSITORY_ID, "default"));
        sb.append(createProperty(DocumentProvider.ROOT_FOLDER_PATH, "/test/submissions"));
        sb.append(createProperty(DocumentProvider.USER, "Administrator"));
        sb.append(createProperty(DocumentProvider.GROUP_ID, "10180"));
        sb.append(createProperty(DocumentProvider.LOGIN, login));
        sb.append(createProperty(DocumentProvider.COMPANY_ID, "" + companyId));
        return sb.toString();
    }

    private String createProperty(String key, String value) {
        return key + KV_SEPARATOR + value + PROP_SEPARATOR;
    }

    @Override
    public Collection<String> validateData(ProcessInstance processInstance) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveData(ProcessInstance processInstance) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadData(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    @Override
    public Component render() {
        vl = new VerticalLayout();
        Button refreshDocumentList = new Button(getMessage("pt.ext.cmis.list.refresh"));
//        refreshDocumentList.setIcon(new ClassResource(CmisDocumentListWidget.class, "/img/load-repository.png", getApplication()));
        refreshDocumentList.setImmediate(true);
        refreshDocumentList.setStyleName(BaseTheme.BUTTON_LINK);
        refreshDocumentList.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                reload();
            }
        });
        vl.addComponent(refreshDocumentList);
        reload();
        return vl;
    }

    private void reload() {
        if (documentListComponent != null) {
            vl.removeComponent(documentListComponent);
        }
        if (hasPermission("EDIT", "VIEW")) {
            vl.addComponent(documentListComponent = getDocumentList());
        }
    }

    private Component getDocumentList() {
        final DocumentProvider documentProvider = getProvider();
        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("100%");
        String path = parseProperties().get(DocumentProvider.ROOT_FOLDER_PATH);
        Collection<Document> documents = documentProvider.getDocuments(path);
        boolean hasAnyDocuments = false;
        for (Document doc : documents) {
            hasAnyDocuments = true;
            vl.addComponent(new DocumentComponent(doc));
        }
        if (!hasAnyDocuments) {
			vl.addComponent(new Label(getI18NSource().getMessage("pt.ext.cmis.list.no-documents")));
		}
        if (hasPermission("EDIT")) {
			if (!hasAnyDocuments) {
				vl.addComponent(new Label(getI18NSource().getMessage("pt.ext.cmis.list.upload")));
				Upload upload = new Upload();
				upload.setImmediate(true);
				upload.setButtonCaption(getI18NSource().getMessage("pt.ext.cmis.list.upload.button"));
				upload.setReceiver(new Upload.Receiver() {
					@Override
					public OutputStream receiveUpload(final String filename, final String MIMEType) {
						return new ByteArrayOutputStream() {
							@Override
							public void close() throws IOException {
								super.close();
                                byte[] bytes = toByteArray();
                                DocumentImpl ud = new DocumentImpl(filename, filename, bytes);
                                Map<String, String> properties = ud.getAttributes();
                                properties.put(PropertyIds.NAME, filename);
                                properties.put(PropertyIds.LAST_MODIFIED_BY, login);
                                getProvider().uploadDocument(ud);
								reload();
							}
						};
					}
				});
				vl.addComponent(upload);
			}
		}
        return vl;
    }


    @Override
    public void addChild(ProcessToolWidget processToolWidget) {
        throw new UnsupportedOperationException();
    }

    private class DocumentComponent extends HorizontalLayout {

        private Document doc;
        private StreamResource resource;

        @Override
        public void attach() {
            super.attach();
            resource = new StreamResource(new StreamResource.StreamSource() {
                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(DocumentComponent.this.doc.getContent());
                }
            }, doc.getFilename(), getApplication());

//            if (popup) {
//                getApplication().getMainWindow().open(resource, "_blank");
//            }
        }

        private DocumentComponent(Document doc) {
            this.doc = doc;
            setSpacing(true);
            setWidth("100%");

            boolean popup = false;
            String name = doc.getFilename();

//            TODO: What is 'popup'?

//            if (name.contains("__POPUP_ONCE__")) {
//                popup = true;
//                name = name.replaceFirst("__POPUP_ONCE__", "");
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put(PropertyIds.NAME, name);
//                co.updateProperties(map, true);
//            } else if (name.contains("__POPUP_ALWAYS__")) {
//                popup = true;
//                name = name.replaceFirst("__POPUP_ALWAYS__", "");
//            }
//
//            if(hideMatching != null && !"".equals(hideMatching) && name.matches(".*"+hideMatching+".*")){
//                continue;
//            }


            Label nameLabel = new Label(name);
            nameLabel.setWidth("100%");
            addComponent(nameLabel);

            Link downloadLink = new Link(getI18NSource().getMessage("pt.ext.cmis.list.document.download"), resource);
            downloadLink.setTargetName("_blank");

            addComponent(downloadLink);
            if (hasPermission("EDIT")) {
                Upload upload = new Upload();
                upload.setButtonCaption(getI18NSource().getMessage("pt.ext.cmis.list.update.button"));
                upload.setReceiver(new UpdateReceiver(doc));
                upload.setImmediate(true);

                addComponent(upload);

            }
        }
    }

    private final class UpdateReceiver implements Upload.Receiver {
        private final Document doc;

        private UpdateReceiver(Document doc) {
            this.doc = doc;
        }

        @Override
        public OutputStream receiveUpload(final String filename, final String MIMEType) {
            return new ByteArrayOutputStream() {
                @Override
                public void close() throws IOException {
                    super.close();
                    final byte[] bytes = toByteArray();
                    DocumentImpl ud = new DocumentImpl(filename, filename, bytes);
                    Map<String, String> properties = ud.getAttributes();
                    properties.put(PropertyIds.NAME, filename);
                    properties.put(PropertyIds.LAST_MODIFIED_BY, login);
                    getProvider().uploadDocument(ud);
                    reload();
                }
            };
        }
    }

    private DocumentProvider getProvider() {
        providerRegistry = ProcessToolContext.Util.getProcessToolContextFromThread().getRegistry().lookupService(
                DocumentProviderRegistry.class.getName());
        return providerRegistry.getProvider(documentProviderName, parseProperties());
    }
}
