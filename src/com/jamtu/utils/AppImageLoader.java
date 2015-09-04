//package com.jamtu.utils;
//
//import java.io.File;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//
//import com.jamtu.App;
//import com.jamtu.joke.R;
//import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
//import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.assist.ImageScaleType;
//import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
//import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
//import com.nostra13.universalimageloader.core.download.ImageDownloader;
//import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
//import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
//import com.nostra13.universalimageloader.utils.StorageUtils;
//
///**
// * 图片加载配置
// * 
// * 重要用于头像
// * 
// * @author Administrator
// * 
// */
//public class AppImageLoader {
//
//	private static AppImageLoader instance;
//
//	private AppImageLoader() {
//		super();
//	}
//
//	public static AppImageLoader getInstance() {
//		if (null == instance)
//			instance = new AppImageLoader();
//		return instance;
//	}
//
//	// --------------------init----------------------------
//	/**
//	 * 初始化ImageLoader
//	 * 
//	 * @param context
//	 * @param useDefault
//	 */
//	public void initImageLoader(Context context, boolean useDefault) {
//		if (useDefault)
//			getImageLoader().init(ImageLoaderConfiguration.createDefault(context));
//		else
//			getImageLoader().init(getImageConfiguration(context));
//	}
//
//	private ImageLoader getImageLoader() {
//		return ImageLoader.getInstance();
//	}
//
//	/**
//	 * 图片加载器ImageLoader的配置参数
//	 * 
//	 * 自定义配置
//	 * 
//	 * @param context
//	 * @return
//	 */
//	private ImageLoaderConfiguration getImageConfiguration(Context context) {
//		int sw = App.getScw(context);
//		int sh = App.getSch(context);
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//				// 设定线程等级比普通低一点
//				.threadPriority(Thread.NORM_PRIORITY - 2)
//				.denyCacheImageMultipleSizesInMemory()
//				.diskCacheExtraOptions(sw, sh, null)
//				// .memoryCacheExtraOptions(sw * 2 / 3, sh * 2 / 3)
//				// 将保存的时候的URI名称用MD5加密
//				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
//				// 自定义缓存路径
//				.diskCache(new UnlimitedDiscCache(getCacheDir(context))).diskCacheSize(50 * 1024 * 1024)
//				.writeDebugLogs().build();
//		return config;
//	}
//
//	/**
//	 * 设置本地缓存路径
//	 * 
//	 * @param context
//	 * @return
//	 */
//	private File getCacheDir(Context context) {
//		return StorageUtils.getOwnCacheDirectory(context, context.getPackageName() + File.separator + "APP_IMG_CACHE"
//				+ File.separator);
//	}
//
//	// --------------------init----------------------------
//
//	/**
//	 * 快速滑动 停止加载图片
//	 * 
//	 * @return
//	 */
//	public PauseOnScrollListener getPauseOnScrollListener() {
//		// 第一个参数就是我们的图片加载对象ImageLoader,
//		// 第二个是控制是否在滑动过程中暂停加载图片，如果需要暂停传true就行了
//		// 第三个参数控制猛的滑动界面的时候图片是否加载
//		return new PauseOnScrollListener(getImageLoader(), true, true);
//	}
//
//	public void displayImage(String url, ImageView imgView) {
//		displayImage(url, imgView, getDisplayImageOptions(-1));
//	}
//
//	/**
//	 * 展示图片
//	 * 
//	 * 本地图片
//	 * 
//	 * 无加载等待框效果
//	 * 
//	 * @param url
//	 * @param imgView
//	 * @param options
//	 */
//	public void displayImage(String url, ImageView imgView, DisplayImageOptions options) {
//		displayImage(ImageDownloader.Scheme.FILE.wrap(url), imgView, options, null);
//	}
//
//	/**
//	 * 展示图片
//	 * 
//	 * 本地图片
//	 * 
//	 * 有加载等待框效果
//	 * 
//	 * @param url
//	 * @param imgView
//	 * @param options
//	 * @param loading
//	 */
//	public void displayImage(String url, ImageView imgView, DisplayImageOptions options, ProgressBar loading) {
//		if (null == loading)
//			getImageLoader().displayImage(ImageDownloader.Scheme.FILE.wrap(url), imgView, options, null);
//		else
//			getImageLoader().displayImage(ImageDownloader.Scheme.FILE.wrap(url), imgView, options,
//					new ImageLoadingListener(loading));
//	}
//
//	public void displayImageUrl(String url, ImageView imgView) {
//		displayImageUrl(url, imgView, getDisplayImageOptions(-1));
//	}
//
//	/**
//	 * 展示图片
//	 * 
//	 * 网络图片
//	 * 
//	 * 无加载等待框效果
//	 * 
//	 * @param url
//	 * @param imgView
//	 * @param options
//	 */
//	public void displayImageUrl(String url, ImageView imgView, DisplayImageOptions options) {
//		displayImageUrl(url, imgView, options, null);
//	}
//
//	/**
//	 * 展示图片
//	 * 
//	 * 网络图片
//	 * 
//	 * 有加载等待框效果
//	 * 
//	 * @param url
//	 * @param imgView
//	 * @param options
//	 * @param loading
//	 */
//	public void displayImageUrl(String url, ImageView imgView, DisplayImageOptions options, ProgressBar loading) {
//		if (null == loading)
//			getImageLoader().displayImage(url, imgView, options);
//		else
//			getImageLoader().displayImage(url, imgView, options, new ImageLoadingListener(loading));
//	}
//
//	/**
//	 * 图片加载器展示图片的配置
//	 * 
//	 * @param placeHolder
//	 *            -1 为采用默认值
//	 * @return
//	 */
//	public DisplayImageOptions getDisplayImageOptions(int placeHolder) {
//		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
//		builder.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
//				.cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
//				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
//				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
//				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
//				.displayer(new FadeInBitmapDisplayer(300));// 是否图片加载好后渐入的动画时间
//		placeHolder = placeHolder == -1 ? R.drawable.mini_avatar : placeHolder;
//		// 设置图片在下载期间显示的图片
//		builder.showImageOnLoading(placeHolder).showImageForEmptyUri(placeHolder).showImageOnFail(placeHolder);
//		return builder.build();// 构建完成
//	}
//
//	private class ImageLoadingListener extends SimpleImageLoadingListener {
//		private ProgressBar loading;
//
//		public ImageLoadingListener(ProgressBar loading) {
//			this.loading = loading;
//		}
//
//		@Override
//		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//			super.onLoadingComplete(imageUri, view, loadedImage);
//			loading.setVisibility(View.GONE);
//		}
//
//		@Override
//		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//			super.onLoadingFailed(imageUri, view, failReason);
//			loading.setVisibility(View.GONE);
//			// AppUtil.getInstance().showToast("图片加载失败");
//		}
//
//		@Override
//		public void onLoadingStarted(String imageUri, View view) {
//			super.onLoadingStarted(imageUri, view);
//			loading.setVisibility(View.VISIBLE);
//		}
//
//	}
//
//}
