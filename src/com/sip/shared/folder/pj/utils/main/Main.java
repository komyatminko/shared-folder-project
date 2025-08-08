package com.sip.shared.folder.pj.utils.main;

import java.io.IOException;

import com.hierynomus.smbj.share.DiskShare;
import com.sip.shared.folder.pj.utils.SharedUtils;

public class Main {

	public static void main(String[] args) {

		SharedUtils sharedUtils = new SharedUtils();
		Main main = new Main();
		DiskShare share = sharedUtils.connect();

		// check file existance
//		main.checkFileExist(sharedUtils, share);

		// check folder existance
//		main.checkFolderExist(sharedUtils, share);

		// show list file name
//		main.listFiles(sharedUtils, share);

		// create single folder
//		main.createFolder(sharedUtils, share);

		// create nested folder
//		main.createNestedFolder(sharedUtils, share);
		
		//delete folder
//		main.deleteFolder(sharedUtils, share);
		
		//delete nested folder
//		main.deleteNestedFolder(sharedUtils, share);
		
		//copy files from one folder to another on server
//		main.copyFiles(sharedUtils, share);
		
		//move files from one folder to another on server
		main.moveFiles(sharedUtils, share);

	}

	public void checkFileExist(SharedUtils sharedUtils, DiskShare share) {
		String filePath = "UOBKHFileUploadTesting/DoneClone/3166678_202108241521004120.pdf"; // Relative path
		boolean isFileExist = sharedUtils.isFileExist(share, filePath);
		if (isFileExist)
			System.out.println("File exist.");
		else
			System.out.println("File not exist.");
		sharedUtils.disconnect();
	}

	public void checkFolderExist(SharedUtils sharedUtils, DiskShare share) {
		String filePath = "UOBKHFileUploadTesting/DoneClone";
		boolean isFileExist = sharedUtils.isFolderExist(share, filePath);
		if (isFileExist)
			System.out.println("Folder exist.");
		else
			System.out.println("Folder not exist.");
		sharedUtils.disconnect();
	}

	public void listFiles(SharedUtils sharedUtils, DiskShare share) {
		String folderPath = "UOBKHFileUploadTesting/DoneClone";
		sharedUtils.listFiles(share, folderPath);
		sharedUtils.disconnect();
	}

	public void createFolder(SharedUtils sharedUtils, DiskShare share) {
		String folderPath = "UOBKHFileUploadTesting/DoneClone/MMK_Test";
		sharedUtils.createFolder(share, folderPath);
		sharedUtils.disconnect();
	}

	public void createNestedFolder(SharedUtils sharedUtils, DiskShare share) {
		String folderPath = "UOBKHFileUploadTesting/DoneClone/MMK_Test/Java/SpringBoot/Test";
		sharedUtils.createNestedFolder(share, folderPath);
		sharedUtils.disconnect();
	}
	
	public void deleteFolder(SharedUtils sharedUtils, DiskShare share) {
		String folderPath = "UOBKHFileUploadTesting/DoneClone/MMK_Test";
		sharedUtils.deleteFolder(share, folderPath);
		sharedUtils.disconnect();
	}
	
	public void copyFiles(SharedUtils shareUtils, DiskShare share) {
		String sourcePath = "UOBKHFileUploadTesting/DoneClone/source folder";
		String targetPath = "UOBKHFileUploadTesting/DoneClone/target folder";
		try {
			shareUtils.copyFiles(share, sourcePath, targetPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		shareUtils.disconnect();
	}
	
	public void moveFiles(SharedUtils shareUtils, DiskShare share) {
		String sourcePath = "UOBKHFileUploadTesting\\DoneClone\\source folder";
		String targetPath = "UOBKHFileUploadTesting\\DoneClone\\target folder";
		try {
			shareUtils.moveFiles(share, sourcePath, targetPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		shareUtils.disconnect();
	}
	
}
