package app.jrupac.cleantwitter;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class TwitterAPI {

	public final String TAG = Utils.TAG_BASE + this.getClass().getName();

	private static TwitterAPI mInstance = null;
	private Twitter mTwitter = null;
	
	public static TwitterAPI getInstance(Context cxt) {
		if (mInstance == null) {
			mInstance = new TwitterAPI(cxt);
		}

		return mInstance;
	}

	private TwitterAPI(Context cxt) {
		SharedPreferences settings = cxt.getSharedPreferences(Utils.PREFS_FILE, 0);
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(Keys.consumerKey);
		cb.setOAuthConsumerSecret(Keys.consumerSecret);
		cb.setOAuthAccessToken(settings.getString("accessToken", null));
		cb.setOAuthAccessTokenSecret(settings.getString("accessSecret", null));
		TwitterFactory tf = new TwitterFactory(cb.build());
		mTwitter = tf.getInstance();
	}
	
	public void getHomeTimeline(BaseListFragment tf) {
		new HTTPS_GET(tf).execute();
	}

	class HTTPS_GET extends AsyncTask<Void, Void, Void> {
		private BaseListFragment mTf;
		private ResponseList<twitter4j.Status> statuses;

		public HTTPS_GET(BaseListFragment tf) {
			mTf = tf;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Paging p = new Paging(1, 200);
				statuses = mTwitter.getHomeTimeline(p);
			} catch (TwitterException e) {
				Log.d(TAG, "Failed to get home timeline.", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			mTf.onParseCompleted(statuses);
		}
	}
}
