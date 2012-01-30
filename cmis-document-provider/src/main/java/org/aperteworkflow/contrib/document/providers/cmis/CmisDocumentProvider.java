package org.aperteworkflow.contrib.document.providers.cmis;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.aperteworkflow.contrib.document.providers.manager.Document;
import org.aperteworkflow.contrib.document.providers.manager.DocumentImpl;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProvider;
import pl.net.bluesoft.rnd.pt.utils.cmis.CmisAtomSessionFacade;
import pl.net.bluesoft.util.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 1/25/12
 * Time: 11:36 AM
 */
public class CmisDocumentProvider implements DocumentProvider{


    private CmisAtomSessionFacade sessionFacade;
    private Folder mainFolder;
    private String path;


    @Override
    public void configure(Map<String, String> properties) {
        String user = properties.get(USER);
        String pass = properties.get(PASS);
        String atomUrl = properties.get(ATOM_URL);
        String repositoryId = properties.get(REPOSITORY_ID);
        String newFolderPrefix = properties.get(NEW_FOLDER_PREFIX);
        String rootFolderPath = properties.get(ROOT_FOLDER_PATH);
        String folderName = properties.get(FOLDER_NAME);

        sessionFacade = new CmisAtomSessionFacade(user, pass, atomUrl, repositoryId);
        mainFolder = sessionFacade.createFolderIfNecessary(newFolderPrefix + folderName,
                rootFolderPath);
        path = rootFolderPath + newFolderPrefix + folderName;
    }

    @Override
    public void uploadDocument(Document doc) {
        byte[] bytes = doc.getContent();
        String mimeType = doc.getMimeType();
        Map<String,String> properties = new HashMap<String, String>();
        sessionFacade.uploadDocument(doc.getFilename(), mainFolder, bytes, mimeType, properties);
    }

    @Override
    public Collection<Document> getDocuments(String path) {

        Collection<Document> docs = new LinkedList<Document>();

        ItemIterable<CmisObject> cmisObjectItemIterable = mainFolder.getChildren();
        for (CmisObject co : cmisObjectItemIterable) {
            docs.add(createDocument(co));

        }
        return docs;
    }

    private Document createDocument(CmisObject co) {
        String filename = co.getName();
        String filePath = path + filename;
        InputStream is = ((org.apache.chemistry.opencmis.client.api.Document) co).getContentStream().getStream();
        byte[] content = new byte[0];
        try {
            content = IOUtils.slurp(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = new DocumentImpl(filePath, filename, content);
        return doc;
    }

}
