package com.gpit.android.util;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import junit.framework.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	// File IO
	private final static int MAX_CACHE_FILE_PATH_LEN = 200;
	public final static String SPECIAL_CHARS_IN_FILEPATH = "\\/:*?\"<>|";

	private final static long GIGA_BYTE = 1024 * 1024 * 1024;
	private final static long MEGA_BYTE = 1024 * 1024;
	private final static long KILO_BYTE = 1024;

	/*************************************************************************************
	 * IO FUNCTIONS
	 ************************************************************************************/
	// File IO function
	public static String getFileName(String filePath) {
		return getDirName(filePath);
	}

	public static boolean existSDCard(Context context) {
		String state;

		state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}

	}

	public static long getSDCardFreeSize(Context context) {
		if (!existSDCard(context))
			return 0;

		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		double sdAvailSize = (double)stat.getAvailableBlocks() *(double)stat.getBlockSize();

		return (long)sdAvailSize;
	}

	// File IO function
	public static String getFilePath(Context context, String path) {
		String fullPath;

		if (existSDCard(context)) {
			fullPath = getSDFilePath(context, path);
		} else {
			fullPath = getInternalFilePath(context, path);
		}

		return fullPath;
	}

	public static String getSDFilePath(Context context, String path) {
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String packageName = context.getApplicationContext().getPackageName();
		sdcardPath += "/." + packageName + "/" + path;

		return sdcardPath;
	}

	public static String getInternalFilePath(Context context, String path) {
		String fullPath = context.getFilesDir() + "/" + path;

		return fullPath;
	}

	public static String getAssetFilePath(Context context, String path) {
		String assetPath = "file:///android_asset/" + path;

		return assetPath;
	}

	public static String getRawFilePath(Context context, String path) {
		String rawPath = "android.resource://" + context.getPackageName() + "/" + path;

		return rawPath;
	}

	public static boolean createDir(String path) {
		boolean ret = true;

		File fileIn = new File(path);

		if (!fileIn.exists())
			ret = fileIn.mkdirs();

		return ret;
	}

	public static FileOutputStream getFileOutputStream(Activity activity, String fullPath) {
		FileOutputStream fos = null;
		boolean ret;

		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File file = new File(fullPath);

				if (!file.exists()) {
					ret = file.createNewFile();
					if (ret == false)
						return null;
				}

				fos = new FileOutputStream(file);
			} else {
				fos = activity.openFileOutput(fullPath,
						Context.MODE_WORLD_WRITEABLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fos;
	}

	/**
	 * Delete recursive directory
	 * @param dir
	 */
	public static void deleteRecursive(File dir)
	{
		Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				File temp =  new File(dir, children[i]);
				if(temp.isDirectory())
				{
					Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
					deleteRecursive(temp);
				}
				else
				{
					Log.d("DeleteRecursive", "Delete File" + temp.getPath());
					boolean b = temp.delete();
					if(b == false)
					{
						Log.d("DeleteRecursive", "DELETE FAIL");
					}
				}
			}

			dir.delete();
		}
	}

	public static void deleteFile(String path) {
		File file = new File(path);
		file.delete();
	}

	/**
	 * Ensure directory existing
	 */
	public static String ensureDir(Context context, String path) {
		return ensureDir(context, path, true);
	}

	public static String ensureDir(Context context, String path, boolean applyContextPath) {
		boolean result;

		if (applyContextPath)
			path = getFilePath(context, path);

		File file = new File(path);
		if (!file.exists()) {
			result = file.mkdirs();
			if (result == false) {
				Log.e("Storage Error", "There is no storage to cache app data");
				return null;
			}
		}

		return file.getAbsolutePath();
	}

	/**
	 * Ensure file existing
	 */
	public static String ensureFile(Context context, String path) {
		File file = new File(path);
		if (file.exists())
			return file.getAbsolutePath();

		String dirPath = file.getParent();

		if (ensureDir(context, dirPath, false) == null)
			return null;

		try {
			new File(path).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return file.getAbsolutePath();
	}

	public static void normalizePath(StringBuffer path) {
		Assert.assertTrue(path != null);

		// Replace special characters
		// "\/:*?\"<>|"
		for (int i = 0 ; i < path.length() ; i++) {
			char c = path.charAt(i);
			if (SPECIAL_CHARS_IN_FILEPATH.indexOf(c) != -1) {
				// Replace special to "_"
				path.setCharAt(i, '_');
			}
		}

		// Truncate tail
		if (path.length() > MAX_CACHE_FILE_PATH_LEN) {
			CharSequence footer = path.subSequence(path.length() - MAX_CACHE_FILE_PATH_LEN, path.length());
			path.setLength(0);
			path.append(footer);
		}
	}

	// Copy dir from source to dest
	public static boolean copyDir(String srcPath, String dstPath) {
		File srcDir = new File(srcPath);
		String orgSrcPath, orgDstPath;

		orgSrcPath = srcPath;
		orgDstPath = dstPath;

		// Remove last separate char
		if (orgSrcPath.charAt(orgSrcPath.length() - 1) == File.separatorChar) {
			if (orgSrcPath.length() > 1)
				orgSrcPath = orgSrcPath.substring(0, orgSrcPath.length() - 1);
			else
				orgSrcPath = "";
		}

		if (orgDstPath.charAt(orgDstPath.length() - 1) == File.separatorChar) {
			if (orgDstPath.length() > 1)
				orgDstPath = orgDstPath.substring(0, orgDstPath.length() - 1);
			else
				orgDstPath = "";
		}

		// ensure destination directory
		if (!createDir(dstPath))
			return false;

		// Copy all files from free app to paid app
		File[] srcList = srcDir.listFiles();
		if (srcList == null)
			return true;
		for (File srcFile : srcList) {
			srcPath = srcFile.getAbsolutePath();
			dstPath = orgDstPath
					+ srcPath.substring(srcPath.indexOf(orgSrcPath)
					+ orgSrcPath.length());
			if (srcFile.isDirectory()) {
				copyDir(srcPath, dstPath);
				continue;
			}
			copyFile(srcPath, dstPath);

		}

		return true;
	}

	// Copy file from source to dest
	public static boolean copyFile(String srcPath, String dstPath) {
		FileInputStream srcInput;

		try {
			File srcFile = new File(srcPath);
			if (!srcFile.exists())
				return false;

			srcInput = new FileInputStream(srcFile);
			copyFile(srcInput, dstPath);
			srcInput.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean copyFile(InputStream srcInput, String dstPath) {
		FileOutputStream dstOutput;

		File destFile;

		try {
			destFile = new File(dstPath);
			ensureDir(null, destFile.getParent(), false);
			destFile.createNewFile();

			dstOutput = new FileOutputStream(destFile);
			byte buffer[] = new byte[2048];
			do {
				int bytesRead = srcInput.read(buffer);
				if (bytesRead <= 0) {
					break;
				}
				dstOutput.write(buffer, 0, bytesRead);

			} while (true);
			dstOutput.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}


	public static void deleteDir(File dir) {
		Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
		if (dir.exists() && dir.isDirectory())
		{
			String[] children = dir.list();
			if (children != null) {
				for (int i = 0; i < children.length; i++)
				{
					File temp =  new File(dir, children[i]);
					if(temp.isDirectory())
					{
						Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
						deleteDir(temp);
					}
					else
					{
						Log.d("DeleteRecursive", "Delete File" + temp.getPath());
						boolean b = temp.delete();
						if(b == false)
						{
							Log.d("DeleteRecursive", "DELETE FAIL");
						}
					}
				}
			}

			dir.delete();
		}
	}

	public static void copyAssetToSDRAM(Context context, String strFilename, String dstFileName) {
		try {
			// complete path to target file
			File fileTarget = new File(dstFileName);
			createDir(fileTarget.getParent());

			if (!fileTarget.exists())
				fileTarget.createNewFile();
			// data source stream
			AssetManager assetManager = context.getAssets();
			InputStream istr = assetManager.open(strFilename);

			// data destination stream
			// NOTE: at this point you'll get an exception if you don't have
			// permission to access SDRAM ! (see manifest)
			OutputStream ostr = new FileOutputStream(fileTarget);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = istr.read(buffer)) > 0) {
				ostr.write(buffer, 0, length);
			}
			ostr.flush();
			ostr.close();
			istr.close();

		} catch (Exception e) {
			Toast.makeText(context, "File-Copy Error: " + strFilename,
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	// Get directory path from file path
	public static String getDirPath(String filePath) {
		String dirPath;
		int pos;

		if (filePath == null)
			return null;

		dirPath = filePath;
		pos = filePath.lastIndexOf(File.separatorChar);
		if (pos != -1) {
			dirPath = filePath.substring(0, pos);
		}

		return dirPath;
	}

	// Get directory path from file path
	public static String getDirName(String dirPath) {
		String dirName;
		int pos;

		if (dirPath == null)
			return null;

		dirName = dirPath;
		pos = dirPath.lastIndexOf(File.separatorChar);
		if (pos != -1) {
			dirName = dirPath.substring(pos + 1);
		}

		return dirName;
	}

	public static long getDirSize(String path) {
		return getDirSize(new File(path), 0);
	}

	public static long getDirSize(File path, long size) {
		File[] list = path.listFiles();
		int len;

		if(list != null) {
			len = list.length;

			for (int i = 0; i < len; i++) {
				try {
					if(list[i].isFile() && list[i].canRead()) {
						size += list[i].length();

					} else if(list[i].isDirectory() && list[i].canRead() && !isSymlink(list[i])) {
						size = getDirSize(list[i], size);
					}
				} catch(IOException e) {
					Log.e("IOException", e.getMessage());
				}
			}
		}

		return size;
	}

	public static String getFileSizeString(long size) {
		String sizeStr;
		if (size > GIGA_BYTE) {
			sizeStr = String.format("%.2fGB", ((float)size / GIGA_BYTE));
		} else if (size > MEGA_BYTE) {
			sizeStr = String.format("%.2fMB", ((float)size / MEGA_BYTE));
		} else if (size > KILO_BYTE) {
			sizeStr = String.format("%.2fKB", ((float)size / KILO_BYTE));
		} else {
			sizeStr = String.format("%dByte", size);
		}

		return sizeStr;
	}

	private static boolean isSymlink(File file) throws IOException {
		File fileInCanonicalDir = null;
		if (file.getParent() == null) {
			fileInCanonicalDir = file;
		} else {
			File canonicalDir = file.getParentFile().getCanonicalFile();
			fileInCanonicalDir = new File(canonicalDir, file.getName());
		}
		return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
	}
	public static Uri getResourcePath(Context context, String resType, String name) {
		String path = String.format("android.resource://%s/%s/%s", context.getPackageName(), resType, name);
		Uri resPathUri = Uri.parse(path);

		return resPathUri;
	}

	public static Uri getRawResourcePath(Context context, Class rawCls, String path) {
		int resID = (Integer)PrivateAccessor.getPrivateField(rawCls, rawCls, path);
		Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/"
				+ resID);

		return uri;
	}
}
