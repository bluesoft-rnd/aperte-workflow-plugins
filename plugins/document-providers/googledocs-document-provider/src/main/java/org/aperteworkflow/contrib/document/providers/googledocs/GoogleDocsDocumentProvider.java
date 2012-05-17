package org.aperteworkflow.contrib.document.providers.googledocs;

import org.aperteworkflow.contrib.document.providers.manager.Document;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProvider;

import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 2/1/12
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleDocsDocumentProvider implements DocumentProvider{
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
}
