package app.jrupac.cleantwitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class ThumbnailDownloader extends AsyncTask<Void, Void, Void> {

	public final String TAG = Utils.TAG_BASE + this.getClass().getName();

	private final int HORIZ_SIZE = 96;
	private final int VERT_SIZE = 96;

	private Tweet mTweet;
	private InputStream mIs;
	private BaseListFragment<Tweet> mTf;
	private URL mUrl;
	private Bitmap mBmp;
	private View mView;

	public ThumbnailDownloader(View view, Tweet tweet,
			BaseListFragment<Tweet> tf) {
		mTweet = tweet;
		mTf = tf;
		mView = view;
		mUrl = null;
		mBmp = null;
	}

	@Override
	protected void onPreExecute() {
		try {
			mUrl = new URL(mTweet.avatar_url);
		} catch (Exception e) {
			Log.e(TAG, "Could not parse URL.", e);
		}

		mIs = null;
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (mUrl == null) {
			return null;
		}

		try {
			mIs = mUrl.openStream();
			mBmp = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(mIs),
					HORIZ_SIZE, VERT_SIZE, true);
			mTweet.avatar = mBmp;
		} catch (Exception e) {
			Log.e(TAG, "Could not download thumbnail.", e);
		} finally {
			if (mIs != null) {
				try {
					mIs.close();
				} catch (IOException ioe) {
					Log.e(TAG, "Could not close input stream.", ioe);
				}
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (mUrl == null) {
			return;
		}

		mTf.onThumbnailDownload(mBmp, mView);
		//Log.d(TAG, "Done downloading thumbnail.");
	}
}
