package org.aperteworkflow.contrib.document.providers.liferay;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.liferay.portlet.documentlibrary.util.comparator.RepositoryModelCreateDateComparator;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import org.aperteworkflow.contrib.document.providers.manager.Document;
import org.aperteworkflow.contrib.document.providers.manager.DocumentImpl;
import org.aperteworkflow.contrib.document.providers.manager.DocumentProvider;
import pl.net.bluesoft.util.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zmalinowski
 * Date: 1/26/12
 * Time: 2:40 PM
 */

public class Liferay_6_1_DocumentProvider implements DocumentProvider {


    public static final String DEFAULT_GROUP_NAME = "Guest";
    private long userId;
    private long groupId;
    private long folderId;
    private long companyId;

    @Override
    public void configure(Map<String, String> properties) {

        String companyIdStr = properties.get(DocumentProvider.COMPANY_ID);
        String folderName = properties.get(DocumentProvider.FOLDER_NAME);
        String login= properties.get(DocumentProvider.LOGIN);

        try {
            companyId = Long.parseLong(companyIdStr);
            userId = UserServiceUtil.getUserIdByScreenName(companyId, login);
//            repository group for documents
            String groupName = DEFAULT_GROUP_NAME;
            if(properties.get(DocumentProvider.GROUP_NAME) != null)
                groupName = properties.get(DocumentProvider.GROUP_NAME);
            groupId = GroupLocalServiceUtil.getGroup(companyId, groupName).getGroupId();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (PortalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            DLFolder folder = null;
            try {
                folder = DLFolderLocalServiceUtil.getFolder(groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, folderName);
            } catch (NoSuchFolderException e) {
                folder = createDLFolder(folderName);
            }
            folderId = folder.getFolderId();
        } catch (PortalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    @Override
    public void uploadDocument(Document doc) {

        String filename = doc.getFilename();
        String fileTitle = doc.getFilename();
        byte[] content = doc.getContent();

        try {
            String mimeType = URLConnection.guessContentTypeFromName(filename);
            long fileEntryTypeId = 0;
            DLFileEntryLocalServiceUtil.addFileEntry(userId, groupId, groupId, folderId, filename, mimeType, fileTitle, "description", "changelog", fileEntryTypeId, new HashMap<String, Fields>(), null, new ByteArrayInputStream(content), content.length, new ServiceContext());
//        TODO: exception handling
        } catch (PortalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    @Override
    public Collection<Document> getDocuments(String path) {
        Collection<Document> docs = new LinkedList<Document>();
        try {
            Collection<DLFileEntry> fileEntries = DLFileEntryLocalServiceUtil.getFileEntries(groupId, folderId, 0, 10, new RepositoryModelCreateDateComparator());
            for (DLFileEntry dlfe : fileEntries) {
                long repositoryId = dlfe.getRepositoryId();
                byte[] content = null;
                try {
                    content = IOUtils.slurp(DLFileEntryLocalServiceUtil.getFileAsStream(userId, dlfe.getFileEntryId(), dlfe.getVersion(), false));
                } catch (PortalException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                docs.add(new DocumentImpl(path + dlfe.getTitle(), dlfe.getTitle(), content));

            }
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return docs;
    }

    private DLFolder createDLFolder(String folderName) throws Exception {

        ServiceContext serviceContext = new ServiceContext();
        return DLFolderLocalServiceUtil.addFolder(userId, groupId, groupId, false, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
                folderName, StringPool.BLANK, serviceContext);
    }

}
