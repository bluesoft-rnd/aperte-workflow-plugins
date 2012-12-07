package org.aperteworkflow.contrib.widgets.doclist;

import com.vaadin.Application;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import org.aperteworkflow.contrib.document.providers.manager.Document;
import org.aperteworkflow.contrib.document.providers.manager.DocumentImpl;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProvider;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProviderRegistry;
import org.aperteworkflow.util.vaadin.VaadinUtility;
import pl.net.bluesoft.rnd.processtool.ProcessToolContext;
import pl.net.bluesoft.rnd.processtool.bpm.ProcessToolBpmSession;
import pl.net.bluesoft.rnd.processtool.model.ProcessInstance;
import pl.net.bluesoft.rnd.processtool.model.config.ProcessStateConfiguration;
import pl.net.bluesoft.rnd.processtool.model.config.ProcessStateWidget;
import pl.net.bluesoft.rnd.processtool.ui.widgets.ProcessToolDataWidget;
import pl.net.bluesoft.rnd.processtool.ui.widgets.ProcessToolVaadinRenderable;
import pl.net.bluesoft.rnd.processtool.ui.widgets.ProcessToolVaadinWidget;
import pl.net.bluesoft.rnd.processtool.ui.widgets.ProcessToolWidget;
import pl.net.bluesoft.rnd.processtool.ui.widgets.annotations.*;
import pl.net.bluesoft.rnd.processtool.ui.widgets.impl.BaseProcessToolWidget;
import pl.net.bluesoft.rnd.util.i18n.I18NSource;
import pl.net.bluesoft.util.lang.StringUtil;
import pl.net.bluesoft.rnd.processtool.model.BpmTask;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 1/25/12
 * Time: 1:49 PM
 */
@AliasName(name = "DocumentList")
@AperteDoc(humanNameKey = "widget.doclist.name", descriptionKey = "widget.doclist.description")
@ChildrenAllowed(false)
@WidgetGroup("base-widgets")
public class DocumentListWidget extends BaseProcessToolWidget implements ProcessToolDataWidget,
        ProcessToolVaadinWidget, ProcessToolVaadinRenderable {

    @AutoWiredProperty
    private String rootFolderPath;

    @AutoWiredProperty
    private String subFolder;

    @AutoWiredProperty
    private String newFolderPrefix;

    @AutoWiredProperty
    private String documentProviderName;

    @AutoWiredProperty
    private String repositoryAtomUrl;

    @AutoWiredProperty
    private String repositoryId;

    @AutoWiredProperty
    private String repositoryPassword;

    @AutoWiredProperty
    private String repositoryUser;

    private VerticalLayout vl;

    private Component documentListComponent;
    private ProcessInstance processInstance;
    private DocumentProviderRegistry providerRegistry;
    private Map<String, String> properties = new HashMap<String, String>();

    private String login;

    private static final Logger logger = Logger.getLogger(DocumentListWidget.class.getName());

    @Override
    public void setContext(ProcessStateConfiguration state, ProcessStateWidget configuration, I18NSource i18NSource,
                           ProcessToolBpmSession bpmSession, Application application, Set<String> permissions, boolean isOwner) {
        super.setContext(state, configuration, i18NSource, bpmSession, application, permissions, isOwner);

        login = bpmSession.getUserLogin();
    }

    /*@Override
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

        properties.put(DocumentProvider.ATOM_URL, repositoryAtomUrl);
        properties.put(DocumentProvider.REPOSITORY_ID, repositoryId);
        properties.put(DocumentProvider.PASS, repositoryPassword);
        properties.put(DocumentProvider.USER, repositoryUser);
        properties.put(DocumentProvider.ROOT_FOLDER_PATH, rootFolderPath);
        properties.put(DocumentProvider.NEW_FOLDER_PREFIX, newFolderPrefix);
        properties.put(DocumentProvider.FOLDER_NAME, processInstance.getInternalId());
    }*/

    @Override
	public Collection<String> validateData(BpmTask task, boolean skipRequired) {
		//todo
		return null;
	}
	
    @Override
	public void saveData(BpmTask task) {
		//todo
	}

    @Override
	public void loadData(BpmTask task) {
		//todo
	}
	
    @Override
    public Component render() {
        vl = new VerticalLayout();
        Button refreshDocumentList = new Button(getMessage("widget.doclist.refresh"));
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
            documentListComponent = getDocumentList();
            if(documentListComponent != null)
                vl.addComponent(documentListComponent);
        }
    }

    private Component getDocumentList() {
        VerticalLayout layout = null;
        try {
            final DocumentProvider documentProvider = getProvider();
            layout = new VerticalLayout();
            layout.setWidth("100%");
            Collection<Document> documents = documentProvider.getDocuments(getPath());
            boolean hasAnyDocuments = false;
            for (Document doc : documents) {
                hasAnyDocuments = true;
                layout.addComponent(new DocumentComponent(doc));
            }
            if (!hasAnyDocuments) {
                layout.addComponent(new Label(getI18NSource().getMessage("widget.doclist.no-documents")));
            }
            if (hasPermission("EDIT")) {
                if (!hasAnyDocuments) {
                    layout.addComponent(new Label(getI18NSource().getMessage("widget.doclist.upload")));
                    Upload upload = new Upload();
                    upload.setImmediate(true);
                    upload.setButtonCaption(getI18NSource().getMessage("widget.doclist.upload.button"));
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
                                    getProvider().uploadDocument(ud);
                                    reload();
                                }
                            };
                        }
                    });
                    layout.addComponent(upload);
                }
            }
            return layout;
        } catch (Exception e) {
            handleException(getMessage("widget.doclist.error"), e);
            layout = new VerticalLayout();
            layout.addComponent(new Label(getI18NSource().getMessage("widget.doclist.no-documents")));
        }
        return layout;
    }

    private String getPath() {
        String path = rootFolderPath + "/" + newFolderPrefix + processInstance.getInternalId();
        if(StringUtil.hasText(subFolder))
            path += "/" +subFolder;
        return path;
    }

    protected void handleException(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
        VaadinUtility.validationNotification(getApplication(), i18NSource, message + "<br/>" + getMessage(e
                .getMessage()));
    }


    @Override
    public void addChild(ProcessToolWidget processToolWidget) {
        throw new UnsupportedOperationException();
    }

    private class DocumentComponent extends HorizontalLayout {

        private Document doc;

        private DocumentComponent(Document doc) {
            this.doc = doc;
            setSpacing(true);
            setWidth("100%");

            String name = doc.getFilename();

            Label nameLabel = new Label(name);
            nameLabel.setWidth("100%");
            addComponent(nameLabel);

            StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(DocumentComponent.this.doc.getContent());
                }
            }, doc.getFilename(), DocumentListWidget.this.getApplication());
            resource.setCacheTime(-1);

            Link downloadLink = new Link(getI18NSource().getMessage("widget.doclist.document.download"), resource);
            downloadLink.setTargetName("_blank");

            addComponent(downloadLink);
            if (hasPermission("EDIT")) {
                Upload upload = new Upload();
                upload.setButtonCaption(getI18NSource().getMessage("widget.doclist.update.button"));
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
                    getProvider().uploadDocument(ud);
                    reload();
                }
            };
        }
    }

    private DocumentProvider getProvider() {
        providerRegistry = ProcessToolContext.Util.getThreadProcessToolContext().getRegistry().lookupService(
                DocumentProviderRegistry.class.getName());
        if(providerRegistry == null)
            throw new RuntimeException("widget.error.registry.unavailable");
        DocumentProvider provider = providerRegistry.getProvider(documentProviderName, properties);
        if(providerRegistry == null)
            throw new RuntimeException("widget.error.provider.unavailable");
        return provider;
    }

    public String getRootFolderPath() {
        return rootFolderPath;
    }

    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
    }

    public String getSubFolder() {
        return subFolder;
    }

    public void setSubFolder(String subFolder) {
        this.subFolder = subFolder;
    }

    public String getNewFolderPrefix() {
        return newFolderPrefix;
    }

    public void setNewFolderPrefix(String newFolderPrefix) {
        this.newFolderPrefix = newFolderPrefix;
    }

    public String getRepositoryAtomUrl() {
        return repositoryAtomUrl;
    }

    public void setRepositoryAtomUrl(String repositoryAtomUrl) {
        this.repositoryAtomUrl = repositoryAtomUrl;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getRepositoryPassword() {
        return repositoryPassword;
    }

    public void setRepositoryPassword(String repositoryPassword) {
        this.repositoryPassword = repositoryPassword;
    }

    public String getRepositoryUser() {
        return repositoryUser;
    }

    public void setRepositoryUser(String repositoryUser) {
        this.repositoryUser = repositoryUser;
    }

    public String getDocumentProviderName() {
        return documentProviderName;
    }

    public void setDocumentProviderName(String documentProviderName) {
        this.documentProviderName = documentProviderName;
    }
}
