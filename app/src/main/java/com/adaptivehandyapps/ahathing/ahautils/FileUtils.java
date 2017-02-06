/**
 * Copyright © 2014-2015 Adaptive Handy Apps, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
// Project: AHA PM Admin
// Contributor(s): M.A.Tucker
package com.adaptivehandyapps.ahathing.ahautils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {

	private final static String TAG = "FileUtils";
	public static final String NULL_MARKER = "nada";

	////////////////////////////////class data////////////////////////////////
	// standard storage location
	private static String appTargetDir = "/Android/data/com.adaptivehandyapps.aha/";
	///////////////////////////////////////////////////////////////////////////////
	// constructor
    public FileUtils() {
	}
	public static void setAppTargetDir(String appDir) {appTargetDir = appDir;}
	public static String getAppTargetDir() {return appTargetDir;}
	/////////////////////////////private interfaces/////////////////////////////
    // get or create target directory
	private static File getTargetStorageDir(String targetDir) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + getAppTargetDir() + targetDir;
//		Log.v(TAG, "getTargetStorageDir: " + path);
		return new File(path);
	}
	///////////////////////////////////////////////////////////////////////////////
	// get target directory
	public static File getTargetDir(String targetDir) {
		File storageDir = null;
		
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = getTargetStorageDir(targetDir);
//			Log.d(TAG, "getTargetDir: storageDir " + storageDir);
			
			if (storageDir != null) {
                if (!storageDir.exists()) {
                    // if directory does not exist, try making directory w/ parent directories
                    if (!storageDir.mkdirs()) {
                        Log.e(TAG, "getTargetDir unable to find or make data directory " + targetDir + ".");
                        return null;
                    }
				}
			}
		} else {
			Log.v(TAG, "getTargetDir: External storage not mounted R/W.");
		}
		return storageDir;
	}
	
	////////////////////////////public interfaces//////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    // get folders
    public static List<String> getFoldersList(String targetFolder) {
        List<String> folderList = new ArrayList<String>();
        File folder = getTargetStorageDir(targetFolder);
        if (folder.isDirectory()) {

            File[] fileList = folder.listFiles();
            for (int j = 0; j < fileList.length; j++) {
                if (fileList[j].isDirectory()) {
                    folderList.add(fileList[j].getName());
                    Log.v(TAG, "Project folder(" + j + ") " + folderList.get(j));
                }
            }
        }
        else {
            // not a project folder!
			Log.e(TAG, "Invalid target folder: " + targetFolder);
        }
        return folderList;
    }
	///////////////////////////////////////////////////////////////////////////////
	// get list of files within folder - either full path or name
	public static List<String> getFilesList(String targetDir, boolean fullpath) {
		// get list of images in specified dir
		List<String> fileList = new ArrayList<>();
		String filename;
		File folder;
		folder = getTargetDir(targetDir);
		Log.v(TAG, "targetDir: " + targetDir);
		if (folder.isDirectory()) {

			File[] fileHandles = folder.listFiles();
//			Arrays.sort(fileHandles, new Comparator<File>() {
//				public int compare(File f1, File f2) {
//					return Long.compare(f1.lastModified(), f2.lastModified());
//				}
//			});
			for (int j = 0; j < fileHandles.length; j++) {
				if (fileHandles[j].isFile()) {
					if (fullpath) {
						// if full path requested, get path
						filename = fileHandles[j].getPath();
					}
					else {
						// full path not requested, get name
						filename = fileHandles[j].getName();
					}
					fileList.add(filename);
//					Log.v(TAG, "File(" + j + ") " + filename);
				}
			}
			Collections.sort(fileList);

		} else {
			// not an directory!
			Log.e(TAG, "Invalid directory name: " + getAppTargetDir() + targetDir);
		}
		return fileList;
	}

    ///////////////////////////////////////////////////////////////////////////////
    // get input stream
    public static BufferedInputStream getFeed(String targetPath) {
        Log.v(TAG, "getFeed for: " + targetPath);
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(targetPath));
        }
        catch (Exception ex) {
            Log.e(TAG, "Exception: " + ex.getMessage());
        }
        return bis;
    }

    ///////////////////////////////////////////////////////////////////////////////
	// read feed
 	public static String readFeed(String targetPath) {
 		String feed = "";
 		try {
	 		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(targetPath));
	 		byte[] readBuffer = new byte[1024];
	
	 		int bytesRead=0;
	 		while( (bytesRead = bis.read(readBuffer)) != -1){ 
	 			feed = feed.concat(new String(readBuffer, 0, bytesRead));
//		 		System.out.print("readBuffer: " + feed);
	 		}
 		}
 		catch (Exception ex) {
			Log.e(TAG, "Exception: " + ex.getMessage());
			return NULL_MARKER;
 		}
 		Log.v(TAG, "Feed length: " + feed.length());
 		return feed;
 	}

	///////////////////////////////////////////////////////////////////////////////
}
