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

	private static Drawable doIt(URL url) {
		InputStream is = null;
		Drawable drawable = null;

		try {
			is = url.openStream();
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

	public static void fetchDrawable(final TweetData current, final ImageView iv) {
		if (current.isDownloading.get()) {
			return;
		}

		current.isDownloading.set(true);
		iv.setImageResource(R.drawable.ic_launcher);

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				current.avatar = (Drawable) message.obj;
				iv.setImageDrawable((Drawable) message.obj);
				current.isDownloading.set(false);
			}
		};

		new Thread() {
			@Override
			public void run() {
				handler.sendMessage(handler.obtainMessage(1,
						doIt(current.avatar_url)));
			}
		}.start();
	}
}
