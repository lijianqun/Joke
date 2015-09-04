package com.jamtu.joke;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.jamtu.App;
import com.jamtu.AppManager;
import com.jamtu.task.SaveImgTask;

public class ImgDetialActivity extends BaseActivity implements OnClickListener, OnViewTapListener {
	private String path;
	private PhotoView img;
	private ImageView imgDownload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_img_detial);
		AppManager.getAppManager().addActivity(this);
		path = getIntent().getStringExtra("param");
		img = (PhotoView) findViewById(R.id.img_src);
		img.setOnViewTapListener(this);
		App.getPicasso(context).load(path).resize(App.getScw(context), App.getSch(context)).centerInside().into(img);
		imgDownload = (ImageView) findViewById(R.id.img_download);
		imgDownload.setOnClickListener(this);
	}

	@Override
	public void onViewTap(View arg0, float arg1, float arg2) {
		AppManager.getAppManager().finishActivity();
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.img_download:
			v.setEnabled(false);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					v.setEnabled(true);
				}
			}, 3 * 1000);
			// TODO 保存原图 重新下载
			SaveImgTask.start(this, path);
			break;

		default:
			break;
		}
	}
}
