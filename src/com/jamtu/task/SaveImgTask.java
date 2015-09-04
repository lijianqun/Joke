package com.jamtu.task;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import com.jamtu.App;
import com.jamtu.utils.AppUtil;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * 保存图片，不做任何图片压缩
 * 
 * 保存到指定位置，并添加至相册
 * 
 */
public class SaveImgTask extends AsyncTask<Object, Void, Boolean> {
	private String imgUrl;
	private Context mContext;

	public synchronized static void start(final Context context, final String url) {
		new Handler(context.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				AppUtil.executeTask(new SaveImgTask(context, url));
			}
		});
	}

	private SaveImgTask(Context context, String url) {
		mContext = context;
		imgUrl = url;
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		return saveFile(imgUrl, getDownloadDir(mContext));
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result)
			AppUtil.ToastMessage(mContext, "图片已保存至相册Pictures中");
	}

	public static File getDownloadDir(Context context) {
		File dir = context.getFilesDir();
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			// 图片缓存放在SDCard/IJoke/...中
			File file = Environment.getExternalStoragePublicDirectory("IJoke/Pictures/");
			if (file != null) {
				dir = file;
			}
		}
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	// 保存图片文件 原图不作处理
	public boolean saveFile(String url, File fileDir) {
		BufferedInputStream bis = getInputStreamFromNetwork(url);
		BufferedOutputStream bos = null;
		boolean isOK = true;
		String fileName = UUID.randomUUID() + ".jpg";
		File file = new File(fileDir, fileName);
		try {
			if (null == bis)
				isOK = false;
			else {
				// 首先保存图片
				bos = new BufferedOutputStream(new FileOutputStream(file));
				int read = 0;
				byte[] buffer = new byte[1024];
				while ((read = bis.read(buffer)) != -1) {
					bos.write(buffer, 0, read);
				}
				bos.flush();
				scanPhoto(mContext, file.getAbsolutePath());
				// // 其次把文件插入到系统图库
				// MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
				// file.getAbsolutePath(), fileName,
				// null);
				// // 最后通知图库更新
				// mContext.sendBroadcast(new
				// Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				// Uri.parse("file://" + file)));
			}
		} catch (Exception e) {
			isOK = false;
			file.delete();
		} finally {
			try {
				if (null != bos)
					bos.close();
				if (null != bis)
					bis.close();

			} catch (Exception e2) {
			}
		}
		return isOK;
	}

	/**
	 * 让Gallery上能马上看到该图片
	 */
	private static void scanPhoto(Context ctx, String imgFileName) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File file = new File(imgFileName);
		Uri contentUri = Uri.fromFile(file);
		mediaScanIntent.setData(contentUri);
		ctx.sendBroadcast(mediaScanIntent);
	}

	public BufferedInputStream getInputStreamFromNetwork(String url) {
		BufferedInputStream bis = null;
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = App.getOkhttpclient().newCall(request).execute();
			bis = new BufferedInputStream(response.body().byteStream());
		} catch (Exception e) {
		}

		return bis;
	}
}
