package com.jamtu.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;

import com.jamtu.config.PrefUtil;

public class FileUtil {

	public static boolean isCopySuccess(Context context) {
		File file = getTargetFile(context, "databases", PrefUtil.PRE_DATABASE_NAME);
		boolean flag = false;
		if (null == file)
			flag = false;
		else if (!file.exists())
			flag = false;
		else if (file.length() < 1024 * 1024 * 3)// 原文件大小3.4m
			flag = false;
		else
			flag = true;
		if (!flag)
			file.delete();
		return flag;
	}

	public static void copyFile(Context context) throws Exception {
		copyFile(context, getTargetFile(context, "databases", PrefUtil.PRE_DATABASE_NAME), PrefUtil.PRE_DATABASE_NAME);
	}

	private static void copyFile(Context context, File target, String fileNameInZip) throws Exception {
		if (!target.exists())
			writeToFile(getZipBytes(context, fileNameInZip), target);
	}

	private static File getTargetFile(Context context, String dir, String fileName) {
		return new File(getTargetDir(context, dir), fileName);
	}

	public static File getPropertiesFile(Context context) {
		return getTargetFile(context, "properties", PrefUtil.PRE_PROPERTIES_NAME);
	}

	public static File getTargetDir(Context context, String dir) {
		return context.getDir(dir, Context.MODE_PRIVATE);
	}

	public static void writeToFile(byte[] data, File target) throws IOException {
		FileOutputStream fo = null;
		ReadableByteChannel src = null;
		FileChannel out = null;
		src = Channels.newChannel(new ByteArrayInputStream(data));
		fo = new FileOutputStream(target);
		out = fo.getChannel();
		out.transferFrom(src, 0, data.length);
		fo.close();
		if (fo != null) {
		}
		if (src != null) {
			src.close();
		}
		if (out != null) {
			out.close();
		}
	}

	private static byte[] getZipBytes(Context context, String fileName) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(getInputStream(context)));
		while (true) {
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			if (zipEntry == null) {
				zipInputStream.close();
				break;
			}
			if (zipEntry.getName().contains(fileName)) {
				byte[] arrayOfByte = new byte[1024];
				while (true) {
					int i = zipInputStream.read(arrayOfByte);
					if (i == -1)
						break;
					baos.write(arrayOfByte, 0, i);
				}
			}
			zipInputStream.closeEntry();
		}
		zipInputStream.close();
		return baos.toByteArray();
	}

	private static InputStream getInputStream(Context context) throws Exception {
		InputStream inStream = context.getAssets().open(PrefUtil.PRE_DATABASE_NAME);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int rc = 0;
		while ((rc = inStream.read(buff)) > 0) {
			for (int i = 0; i < rc; i++) {// 解密
				buff[i] -= 2;
			}
			outStream.write(buff, 0, rc);
		}

		return new ByteArrayInputStream(outStream.toByteArray());
	}

}
