package com.sip.shared.folder.pj.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.List;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

public class SharedUtils {

	// Credentials and connection info
	String username = "administrator";
	String host = "192.168.2.100";
	String password = "P@ger123";
	String shareName = "Shares"; // Replace with your actual share name (not full path)

	private SMBClient client;
	private Connection connection;
	private Session session;
	private DiskShare share;

	public DiskShare connect() {
		try {
			client = new SMBClient();
			connection = client.connect(host);

			AuthenticationContext authContext = new AuthenticationContext(username, password.toCharArray(), "");
			session = connection.authenticate(authContext);

			share = (DiskShare) session.connectShare(shareName);
			System.out.println("Connected to: " + shareName);
			return share;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void disconnect() {
		try {
			if (share != null && share.isConnected()) {
				share.close();
				System.out.println("Share disconnected.");
			}
			if (session != null && session.getConnection().isConnected()) {
				session.close();
			}
			if (connection != null && connection.isConnected()) {
				connection.close();
			}
			if (client != null) {
				client.close();
				System.out.println("Client closed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isFileExist(DiskShare share, String filePath) {
		return share.fileExists(filePath);
	}

	public boolean isFolderExist(DiskShare share, String folderPath) {
		return share.folderExists(folderPath);
	}

	public void listFiles(DiskShare share, String folderPath) {
		boolean isFolderExist = this.isFolderExist(share, folderPath);
		if (isFolderExist) {
			List<FileIdBothDirectoryInformation> files = share.list(folderPath);
			for (FileIdBothDirectoryInformation file : files) {
				System.out.println("File name: " + file.getFileName());
			}
		}
	}

	public void createFolder(DiskShare share, String folderPath) {

		boolean isFolderExist = this.isFolderExist(share, folderPath);
		if (!isFolderExist) {
			share.mkdir(folderPath);
			System.out.println("Folder created: " + folderPath);
		} else {
			System.out.println("Folder already exist: " + folderPath);
		}

	}

	public void createNestedFolder(DiskShare share, String folderPath) {

		String[] folders = folderPath.split("/");
		String currentPath = "";

		for (String folder : folders) {

			currentPath += "/" + folder;

			boolean isFolderExist = isFolderExist(share, currentPath);
			if (!isFolderExist) {
				share.mkdir(currentPath);
				System.out.println("Folder created: " + currentPath);
			} else {
				System.out.println("Folder already exist: " + currentPath);
			}
		}
	}

	public void deleteFolder(DiskShare share, String folderPath) {

		boolean isFolderExist = isFolderExist(share, folderPath);
		if (isFolderExist) {
			share.rmdir(folderPath, true); // if set true, folder and files inside it will be deleted
											// if set false, only empty folder will be deleted.
			System.out.println("Folder deleted successfully.");
		} else {
			System.out.println("Folder not found.");
		}

	}

	// copying file and folder on server
	public void copyFile(DiskShare share, String sourcePath, String targetPath) throws IOException {

		try (
				InputStream input = share.openFile(
					sourcePath, 
					EnumSet.of(AccessMask.GENERIC_READ), 
					null,
					SMB2ShareAccess.ALL, 
					SMB2CreateDisposition.FILE_OPEN, 
					null).getInputStream();

				OutputStream out = share.openFile(
						targetPath, 
						EnumSet.of(AccessMask.GENERIC_WRITE), 
						null,
						SMB2ShareAccess.ALL, 
						SMB2CreateDisposition.FILE_OVERWRITE_IF, 
						null).getOutputStream()
			) {

			byte[] buffer = new byte[8192]; // 8KB
			int bytesRead;
			while ((bytesRead = input.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			System.out.println("File copied: " + targetPath);
		} 	
	}

	public void copyFiles(DiskShare share, String sourcePath, String targetPath) throws IOException {

		boolean isFolderExist = isFolderExist(share, sourcePath);
		if (isFolderExist) {
			List<FileIdBothDirectoryInformation> files = share.list(sourcePath);

			for (FileIdBothDirectoryInformation file : files) {

				if (file.getFileName().equals(".") || file.getFileName().equals("..")) {
					continue;
				}

				String sourceFullPath = sourcePath + "/" + file.getFileName();
				String targetFullPath = targetPath + "/" + file.getFileName();

				System.out.println("source path: " + sourceFullPath);
				System.out.println("target path: " + targetFullPath);
				copyFile(share, sourceFullPath, targetFullPath);

			}
		}
	}

	// moving files and folders on server
	public void moveFile(DiskShare share, String sourcePath, String targetPath) throws IOException {
	    try (File file = share.openFile(
	            sourcePath,
	            EnumSet.of(AccessMask.GENERIC_READ, AccessMask.DELETE),
	            null,
	            SMB2ShareAccess.ALL,
	            SMB2CreateDisposition.FILE_OPEN,
	            null)) {

	    	//move the file to the target path
	        file.rename(targetPath); 
	        System.out.println("File moved: " + targetPath);
	    }
	}
	
	public void moveFiles(DiskShare share, String sourcePath, String targetPath) throws IOException {

		boolean isFolderExist = isFolderExist(share, sourcePath);
		if (isFolderExist) {
			List<FileIdBothDirectoryInformation> files = share.list(sourcePath);

			for (FileIdBothDirectoryInformation file : files) {

				if (file.getFileName().equals(".") || file.getFileName().equals("..")) {
					continue;
				}

				String sourceFullPath = sourcePath + "\\" + file.getFileName();
				String targetFullPath = targetPath + "\\" + file.getFileName();

				System.out.println("source path: " + sourceFullPath);
				System.out.println("target path: " + targetFullPath);
				moveFile(share, sourceFullPath, targetFullPath);

			}
		}
	}

	
	public void uploadFile(DiskShare share, String localFilePath, String remoteFilePath) throws Exception {
        try (InputStream localInput = new FileInputStream(localFilePath);
             File remoteFile = share.openFile(
                     remoteFilePath,
                     EnumSet.of(AccessMask.GENERIC_WRITE),
                     null,
                     SMB2ShareAccess.ALL,
                     SMB2CreateDisposition.FILE_OVERWRITE_IF,
                     null);
             OutputStream remoteOutput = remoteFile.getOutputStream()) {

            byte[] buffer = new byte[1024 * 1024]; // 8KB
            int bytesRead;
            while ((bytesRead = localInput.read(buffer)) > 0) {
                remoteOutput.write(buffer, 0, bytesRead);
            }
            
            System.out.println("File uploaded successfully.");
        }
    }
	
	public void downloadFile(DiskShare share, String remoteFilePath, String localFilePath) throws IOException {
        try (File serverFile = share.openFile(
                remoteFilePath,
                EnumSet.of(AccessMask.GENERIC_READ),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null);
             FileOutputStream fos = new FileOutputStream(localFilePath)) {

            byte[] buffer = new byte[1024 * 1024]; // 1MB 
            int offset = 0;
            int bytesRead;

            while ((bytesRead = serverFile.read(buffer, offset, 0, buffer.length)) > 0) {
                fos.write(buffer, 0, bytesRead);
                offset += bytesRead; // move forward in the file
            }

            System.out.println("File downloaded successfully.");
        }
    }
	
	//reading xml file 
	public void readXML(String filename) throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(new java.io.File(filename));
	    document.getDocumentElement().normalize();
	    
	    System.out.println("Root element: " + document.getDocumentElement().getNodeName());
	    NodeList nodeList = document.getElementsByTagName("employee");
//	    document.getDocumentElement().getE
	    for(int i = 0; i< nodeList.getLength(); i++) {
	    	 Node node = nodeList.item(i);
	    	
	    	if(node.getNodeType() == Node.ELEMENT_NODE) {
	    		Element  elem = (Element) node;
	    		String id= elem.getAttribute("id");
	    		String firstName = elem.getElementsByTagName("firstName").item(0).getTextContent();
	    		String lastName = elem.getElementsByTagName("lastName").item(0).getTextContent();
	    		String email = elem.getElementsByTagName("email").item(0).getTextContent();
	    		System.out.println("id: " + id);
	    		System.out.println("firstName: " + firstName);
	    		System.out.println("lastName: " + lastName);
	    		System.out.println("email: " + email);
	    	}
	    }
	}
}