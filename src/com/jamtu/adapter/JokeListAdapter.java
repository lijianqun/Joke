package com.jamtu.adapter;

import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jamtu.App;
import com.jamtu.bean.Author;
import com.jamtu.bean.Joke;
import com.jamtu.joke.ImgDetialActivity;
import com.jamtu.joke.R;
import com.jamtu.widget.CircleImageView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class JokeListAdapter extends MyBaseAdapter<Joke> {
	static class ViewHolder {
		public CircleImageView face;// 用户头像
		public TextView title;
		public TextView content;// 项目描述
		public ImageView img;
		public GifImageView gifView;
		public TextView statsVote;// n个人觉得好笑
	}

	public JokeListAdapter(Context context, List<Joke> data, int resource) {
		super(context, data, resource);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			holder = new ViewHolder();

			// 获取控件对象
			holder.face = (CircleImageView) convertView.findViewById(R.id.listitem_userface);
			holder.title = (TextView) convertView.findViewById(R.id.listitem_title);
			holder.content = (TextView) convertView.findViewById(R.id.listitem_description);
			holder.img = (ImageView) convertView.findViewById(R.id.listitem_img);
			holder.gifView = (GifImageView) convertView.findViewById(R.id.gif_imgview);
			holder.statsVote = (TextView) convertView.findViewById(R.id.listitem_votes);

			// 设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Joke joke = listData.get(position);

		if (joke.hasAuthor()) {
			Author author = joke.getAuthor();
			// 1.加载头像
			String icon = author.getIcon();
			icon = TextUtils.isEmpty(icon) ? "error url" : icon;
			App.getPicasso(context).load(icon).placeholder(R.drawable.mini_avatar).into(holder.face);

			// 2.显示相关信息
			String name = author.getName();
			name = TextUtils.isEmpty(name) ? "匿名用户" : name;
			holder.title.setText(name);
		} else {
			holder.face.setImageResource(R.drawable.mini_avatar);
			holder.title.setText(TextUtils.isEmpty(joke.getTitle()) ? "匿名用户" : joke.getTitle());
		}

		// 判断是否有项目的介绍
		String content = joke.getContent();
		if (!TextUtils.isEmpty(content)) {
			holder.content.setText(content);
		} else {
			holder.content.setVisibility(View.GONE);
		}

		final String imgUrl = joke.getImg();
		if (TextUtils.isEmpty(imgUrl)) {
			holder.img.setVisibility(View.GONE);
			holder.gifView.setVisibility(View.GONE);
		} else {
			if (imgUrl.endsWith(".gif")) {
				try {
					executeGif(context, imgUrl, holder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				holder.gifView.setVisibility(View.GONE);
				holder.img.setVisibility(View.VISIBLE);
				App.getPicasso(context).load(imgUrl).placeholder(R.drawable.loading)
						.resize(App.getScw(context), App.getSch(context)).centerInside().into(holder.img);
			}

			// picasso.load(imgUrl).placeholder(R.drawable.loading).transform(new
			// Transformation() {
			//
			// @Override
			// public Bitmap transform(Bitmap source) {
			// int bx = source.getWidth();
			// int by = source.getHeight();
			// int mod = by / bx;
			// if (mod < 2)
			// return source;
			//
			// int sw = App.getScw(context);
			// Bitmap result = zoomImage(source, 320, by);
			// if (result != source) {
			// source.recycle();
			// }
			// return result;
			// }
			//
			// @Override
			// public String key() {
			// return "T";
			// }
			// }).resize(App.getScw(context),
			// App.getSch(context)).centerInside().into(listItemView.img);
			// listItemView.img.setScaleType(ScaleType.FIT_XY);
			holder.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					App.launchActivity(context, ImgDetialActivity.class, imgUrl);
				}
			});
		}

		// 显示项目的statsVote信息
		String statsVote = joke.getStatsVote();
		if (TextUtils.isEmpty(statsVote))
			holder.statsVote.setVisibility(View.GONE);
		else {
			holder.statsVote.setText(joke.getStatsVote());
		}

		return convertView;
	}

	private void executeGif(Context context, String url, ViewHolder holder) throws Exception {
		holder.gifView.setBackgroundDrawable(null);
		holder.gifView.setImageDrawable(new GifDrawable(context.getResources(), R.drawable.loading));
		holder.img.setVisibility(View.GONE);
		holder.gifView.setVisibility(View.VISIBLE);
		if (mMemoryCache.get(url) == null) {
			synchronized (mMemoryCache) {
				Request request = new Request.Builder().url(url).build();
				App.getOkhttpclient().newCall(request).enqueue(new GIF_OKHttpCallBack(context, holder));
			}
		} else {
			holder.gifView.setBackgroundDrawable(mMemoryCache.get(url));
			holder.gifView.setImageDrawable(null);
		}

	}

	private class GIF_OKHttpCallBack implements Callback {
		ViewHolder mHolder;
		Context mContext;

		public GIF_OKHttpCallBack(Context context, ViewHolder holder) {
			mHolder = holder;
			mContext = context;
		}

		@Override
		public void onFailure(Request request, IOException exception) {
			exception.printStackTrace();
		}

		@Override
		public void onResponse(Response response) throws IOException {
			final GifDrawable drawable = new GifDrawable(response.body().bytes());
			mMemoryCache.put(response.request().urlString(), drawable);

			new Handler(mContext.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					mHolder.gifView.setBackgroundDrawable(drawable);
					mHolder.gifView.setImageDrawable(null);
				}
			});
		}
	}

	private LruCache<String, GifDrawable> mMemoryCache = new LruCache<String, GifDrawable>(6 * 1024) {
		@Override
		protected int sizeOf(String key, GifDrawable value) {
			return (int) value.getAllocationByteCount();
		}
	};

	// .transform(new Transformation() {
	//
	// @Override
	// public Bitmap transform(Bitmap source) {
	// int bx = source.getWidth();
	// int by = source.getHeight();
	// int mod = by / bx;
	// if (mod < 2)
	// return source;
	//
	// int sw = App.getScw(context);
	// Bitmap result = zoomImage(source, sw / 2, (int) (1.8 * by));
	// if (result != source) {
	// source.recycle();
	// }
	// return result;
	// }
	//
	// @Override
	// public String key() {
	// return "T";
	// }
	// })

	// /***
	// * 图片的缩放方法
	// *
	// * @param bgimage
	// * ：源图片资源
	// * @param newWidth
	// * ：缩放后宽度
	// * @param newHeight
	// * ：缩放后高度
	// * @return
	// */
	// public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
	// // 获取这个图片的宽和高
	// int width = bgimage.getWidth();
	// int height = bgimage.getHeight();
	// // 创建操作图片用的matrix对象
	// Matrix matrix = new Matrix();
	// // 计算缩放率，新尺寸除原始尺寸
	// float scaleWidth = ((float) newWidth) / width;
	// float scaleHeight = ((float) newHeight) / height;
	// // 缩放图片动作
	// matrix.postScale(scaleWidth, scaleHeight);
	// Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix,
	// true);
	// return bitmap;
	// }
}
