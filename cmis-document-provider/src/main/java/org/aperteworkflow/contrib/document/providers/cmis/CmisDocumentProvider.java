package org.aperteworkflow.contrib.document.providers.cmis;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.aperteworkflow.contrib.document.providers.manager.Document;
import org.aperteworkflow.contrib.document.providers.manager.DocumentImpl;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProvider;
import pl.net.bluesoft.rnd.pt.utils.cmis.CmisAtomSessionFacade;
import pl.net.bluesoft.util.io.IOUtils;

import java.io.ByteArrayOutputStream;
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
    private String user;
    private String pass;
    private String atomUrl;
    private String repositoryId;
    private String newFolderPrefix;
    private String rootFolderPath;
    private String folderName;


    @Override
    public void configure(Map<String, String> properties) {
        user = properties.get(USER);
        pass = properties.get(PASS);
        atomUrl = properties.get(ATOM_URL);
        repositoryId = properties.get(REPOSITORY_ID);
        newFolderPrefix = properties.get(NEW_FOLDER_PREFIX);
        rootFolderPath = properties.get(ROOT_FOLDER_PATH);
        folderName = properties.get(FOLDER_NAME);

        sessionFacade = new CmisAtomSessionFacade(user, pass, atomUrl, repositoryId);
        mainFolder = sessionFacade.createFolderIfNecessary(newFolderPrefix + folderName,
                rootFolderPath);

    }

    @Override
    public void uploadDocument(Document doc) {
        byte[] bytes = doc.getContent();
        String mimeType = doc.getMimeType();
        Map<String,String> properties = new HashMap<String, String>();

        sessionFacade.uploadDocument(doc.getFilename(), mainFolder, bytes, mimeType, properties);

    }

    @Override
    public Collection<Document> getDocuments(String absolutePath) {

        Collection<Document> docs = new LinkedList<Document>();
        Folder folder = (Folder) sessionFacade.getObjectByPath(absolutePath);

        ItemIterable<CmisObject> cmisObjectItemIterable = folder.getChildren();
        for (CmisObject co : cmisObjectItemIterable) {
            docs.add(wrapDocument(co, absolutePath));

        }
        return docs;
    }

    private Document wrapDocument(CmisObject co, String path) {
        String filename = co.getName();
        String filePath = path + "/" + filename;
        InputStream is = ((org.apache.chemistry.opencmis.client.api.Document) co).getContentStream().getStream();
        byte[] content = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int c=0;
            while ((c = is.read()) >= 0) {
                baos.write(c);
            }
            content = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
            Document doc = new DocumentImpl(filePath, filename, content);
        return doc;
    }

}
