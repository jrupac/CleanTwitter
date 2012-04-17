package app.jrupac.cleantwitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class ThumbnailDownloader {

	public static final String TAG = Utils.TAG_BASE + "ThumbnailDownloader";

	private static Drawable doIt(String urlString) {
		InputStream is = null;
		Drawable drawable = null;

		try {
			is = new URL(urlString).openStream();
			drawable = Drawable.createFromStream(is, "src");
		} catch (MalformedURLException e) {
			Log.e(TAG, "Failed to parse URL.", e);
		} catch (IOException e) {
			Log.e(TAG, "Failed to fetch drawable", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
					Log.e(TAG, "Failed to close stream.", ioe);
				}
			}
		}

		return drawable;
	}

	public static void fetchDrawable(final Tweet tweet,
			final ImageView imageView) {
		if (tweet.isDownloading.get()) {
			return;
		}

		tweet.isDownloading.set(true);
		imageView.setImageResource(R.drawable.ic_launcher);

		final String urlString = tweet.avatar_url;

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				tweet.avatar = (Drawable) message.obj;
				imageView.setImageDrawable((Drawable) message.obj);
				tweet.isDownloading.set(false);
			}
		};

		new Thread() {
			@Override
			public void run() {
				handler.sendMessage(handler.obtainMessage(1, doIt(urlString)));
			}
		}.start();
	}
}
