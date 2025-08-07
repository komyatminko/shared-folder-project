package com.sip.shared.folder.pj.utils;

import java.util.List;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

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
		
	}
	
	public void deleteNestedFolder(DiskShare share, String folderPath) {
		
	}
}
