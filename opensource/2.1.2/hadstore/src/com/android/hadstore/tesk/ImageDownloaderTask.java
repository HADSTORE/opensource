package com.android.hadstore.tesk;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.android.hadstore.Global;
import com.android.hadstore.tesk.ImageDownloader.DownloadedDrawable;
import com.android.hadstore.util.BitmapUtil;
import com.android.hadstore.view.AppDetailView.SizeData;
//import com.android.starapp.sihoo.adapter.MusicImageAdapter.SizeData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap>
{
	public String url;
	public String targetUrl;
	public boolean mIsBackground = false;
	public int mIndex = -1;
	public boolean mIsScreen = false;
	private WeakReference<ImageView> imageViewReference;
	
	public ImageDownloaderTask(String url, ImageView imageView,boolean isBackground,boolean screen)
	{
		this.targetUrl = url.replaceAll (" ", "%20");
		this.imageViewReference = new WeakReference<ImageView>(imageView);
		mIsBackground = isBackground;
		mIsScreen = screen;
	}

	public ImageDownloaderTask(String url, ImageView imageView)
	{
		this.targetUrl = url.replaceAll (" ", "%20");
		this.imageViewReference = new WeakReference<ImageView>(imageView);
	}
	
	public ImageDownloaderTask(String url, ImageView imageView,boolean isBackground)
	{
		this.targetUrl = url.replaceAll (" ", "%20");
		this.imageViewReference = new WeakReference<ImageView>(imageView);
		mIsBackground = isBackground;
	}
	
	public ImageDownloaderTask(String url, ImageView imageView,int indx)
	{
		this.targetUrl = url.replaceAll (" ", "%20");
		this.imageViewReference = new WeakReference<ImageView>(imageView);
		mIndex = indx;
	}
	@Override
	protected Bitmap doInBackground(String... params)
	{
		Bitmap dump = null;
		if(mIndex!=-1){
			ImageView imageView = imageViewReference.get();
			SizeData da = (SizeData) imageView.getTag();
			dump = BitmapUtil.getBitmapReflection(downloadBitmap(targetUrl),da.mWith,da.mHeight);
		}else{
			dump = downloadBitmap(targetUrl);
		}
		/*if(mIsLand){
			 if(dump.getWidth()>dump.getHeight()){
				 Matrix matrix = new Matrix(); 
				// rotate the Bitmap 
				 matrix.postRotate(90); 
				 Bitmap resizedBitmap = Bitmap.createBitmap(dump, 0, 0, 
						 dump.getWidth(), dump.getHeight(), matrix, true); 
				 //dump.recycle();
				// dump = null;
				 dump = resizedBitmap;
			 }
		}*/
		return dump;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		if(isCancelled())
		{
			bitmap = null;
		}

		if(imageViewReference != null&&bitmap!=null)
		{
			ImageView imageView = imageViewReference.get();
			ImageDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
			
			if(this == bitmapDownloaderTask)
			{
				Bitmap cachedImage = ImageDownloader.mImageCache.get(targetUrl);
				if(cachedImage == null)
				{
					ImageDownloader.mImageCache.put(targetUrl, bitmap);
					if(mIsScreen){
						double scale = (double)bitmap.getWidth()/(double)bitmap.getHeight();
						imageView.setLayoutParams(new LinearLayout.LayoutParams((int)(Global.DETAIL_IMGSIZE_HEIGHT*scale),(int)Global.DETAIL_IMGSIZE_HEIGHT));
					}
					if(mIsBackground){
						imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
					}else{
						imageView.setImageBitmap(bitmap);
						imageView.setBackgroundDrawable(null);
					}
				}else{
					bitmap.recycle();
					bitmap = null;
					if(mIsScreen){
						double scale = (double)cachedImage.getWidth()/(double)cachedImage.getHeight();
						imageView.setLayoutParams(new LinearLayout.LayoutParams((int)(Global.DETAIL_IMGSIZE_HEIGHT*scale),(int)Global.DETAIL_IMGSIZE_HEIGHT));
					}
					if(mIsBackground){
						imageView.setBackgroundDrawable(new BitmapDrawable(cachedImage));
					}else{
						imageView.setImageBitmap(cachedImage);
						imageView.setBackgroundDrawable(null);
					}
				}
			}
		}
	}
	
	private ImageDownloaderTask getBitmapDownloaderTask(ImageView imageView)
	{
		if(imageView != null)
		{
			Drawable drawable = imageView.getDrawable();
			if(drawable instanceof DownloadedDrawable)
			{
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	static Bitmap downloadBitmap(String url)
	{
		if(url==null) return null;
		final HttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);
		try
		{
			
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != HttpStatus.SC_OK)
			{
				Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if(entity != null)
			{
				InputStream inputStream = null;
				//BitmapFactory.Options options = new BitmapFactory.Options();
				//options.inSampleSize = 2;
				
				try
				{
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
					//final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream), null, options);
					return bitmap;
				}catch(OutOfMemoryError e){
					e.printStackTrace();
					//ImageDownloader.mImageCache.clear();
					//System.gc();
					//inputStream = entity.getContent();
					//final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
					//final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream), null, options);
					//return bitmap;
				}catch(Exception e){
					e.printStackTrace();
				}finally
				{
					if(inputStream != null)
					{
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		}
		catch(Exception e)
		{
			getRequest.abort();
		}
		return null;
	}

	public static class FlushedInputStream extends FilterInputStream
	{
		public FlushedInputStream(InputStream inputStream)
		{
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException
		{
			long totalBytesSkipped = 0L;
			while(totalBytesSkipped < n)
			{
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if(bytesSkipped == 0L)
				{
					int bytes = read();
					if(bytes < 0)
					{
						break; // we reached EOF
					}
					else
					{
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
}
