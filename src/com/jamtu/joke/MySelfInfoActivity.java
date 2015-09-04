package com.jamtu.joke;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.jamtu.AppManager;
import com.jamtu.common.FileUtils;
import com.jamtu.utils.AppUtil;
import com.jamtu.utils.ImageUtils;

/**
 * 用户信息详情页面
 * 
 * @created 2014-07-01
 * @author 火蚁(http://my.oschina.net/LittleDY)
 * 
 */
public class MySelfInfoActivity extends BaseActivity implements View.OnClickListener {

	private final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/IJoke/Portrait/";
	private final static int CROP = 200;
	private Uri origUri;
	private Uri cropUri;
	private File protraitFile;
	private Bitmap protraitBitmap;
	private String protraitPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		Button btn = new Button(this);
		btn.setText("选头像");
		btn.setId(0x01);
		setContentView(btn);
		btn.setOnClickListener(this);
		initView();
		initData();
	}

	private void initView() {
	}

	private void initData() {
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		// 编辑用户头像
		case 0x01:
			CharSequence[] items = { getString(R.string.img_from_album), getString(R.string.img_from_camera) };
			imageChooseItem(items);
			break;
		// 其他
		default:
			break;
		}
	}

	private void imageChooseItem(CharSequence[] items) {
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("上传头像").setIcon(android.R.drawable.btn_star)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// 相册选图
						if (item == 0) {
							startImagePick();
						}
						// 手机拍照
						else if (item == 1) {
							startActionCamera();
						}
					}
				}).create();

		imageDialog.show();
	}

	/**
	 * 选择图片裁剪
	 * 
	 * @param output
	 */
	private void startImagePick() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}

	/**
	 * 相机拍照
	 * 
	 * @param output
	 */
	private void startActionCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getCameraTempFile());
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}

	// 拍照保存的绝对路径
	private Uri getCameraTempFile() {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			AppUtil.ToastMessage(context, "无法保存上传的头像，请检查SD卡是否挂载");
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		// 照片命名
		String cropFileName = "osc_camera_" + timeStamp + ".jpg";
		// 裁剪头像的绝对路径
		protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);
		cropUri = Uri.fromFile(protraitFile);
		this.origUri = this.cropUri;
		return this.cropUri;
	}

	// 裁剪头像的绝对路径
	private Uri getUploadTempFile(Uri uri) {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			AppUtil.ToastMessage(context, "无法保存上传的头像，请检查SD卡是否挂载");
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

		// 如果是标准Uri
		if (TextUtils.isEmpty(thePath)) {
			thePath = ImageUtils.getAbsoluteImagePath(MySelfInfoActivity.this, uri);
		}
		String ext = FileUtils.getFileFormat(thePath);
		ext = TextUtils.isEmpty(ext) ? "jpg" : ext;
		// 照片命名
		String cropFileName = "osc_crop_" + timeStamp + "." + ext;
		// 裁剪头像的绝对路径
		protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);

		cropUri = Uri.fromFile(protraitFile);
		return this.cropUri;
	}

	/**
	 * 拍照后裁剪
	 * 
	 * @param data
	 *            原始图片
	 * @param output
	 *            裁剪后图片
	 */
	private void startActionCrop(Uri data) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", this.getUploadTempFile(data));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
	}

	/**
	 * 上传新照片
	 */
	// private void uploadNewPhoto() {
	//
	// final ProgressDialog dialog = new ProgressDialog(context);
	// dialog.setCanceledOnTouchOutside(false);
	// dialog.setMessage("正在上传头像...");
	//
	// new AsyncTask<Void, Void, Message>() {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// dialog.show();
	// }
	//
	// @Override
	// protected void onPostExecute(Message msg) {
	// super.onPostExecute(msg);
	// dialog.dismiss();
	// if (msg.what == 1) {
	// UpLoadFile file = (UpLoadFile) msg.obj;
	// if (file == null) {
	// Log.i("Test", "返回的结果为空");
	// }
	// // UIHelper.ToastMessage(mAppContext, file.getMsg());
	// mUserFace.setImageBitmap(protraitBitmap);
	// } else {
	// AppUtil.ToastMessage(context, "上次头像失败");
	// }
	// }
	//
	// @Override
	// protected Message doInBackground(Void... params) {
	// Message msg = new Message();
	// // 获取头像缩略图
	// if (!TextUtils.isEmpty(protraitPath) && protraitFile.exists()) {
	// protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath, 200, 200);
	// } else {
	// AppUtil.ToastMessage(context, "图像不存在");
	// }
	// if (protraitBitmap != null && protraitFile != null) {
	// try {
	// UpLoadFile file = mAppContext.upLoad(protraitFile);
	// msg.what = 1;
	// msg.obj = file;
	// } catch (AppException e) {
	// dialog.dismiss();
	// msg.what = -1;
	// msg.obj = e;
	// }
	// }
	// return msg;
	// }
	//
	// }.execute();
	// }

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
			startActionCrop(origUri);// 拍照后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
			startActionCrop(data.getData());// 选图后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
			// uploadNewPhoto();// 上传新照片
			break;
		}
	}
}
