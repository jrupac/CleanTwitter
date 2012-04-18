package app.jrupac.cleantwitter;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class OAuth {

	public final String TAG = Utils.TAG_BASE + this.getClass().getName();

	private static OAuth mOAuth = null;

	private RequestToken mRequestToken;
	private AsyncTwitter mTwitter;
	private SharedPreferences mSettings;
	private BaseActivity mActivity;
	private boolean mIsLoggedIn;

	public OAuth(BaseActivity ba) {
		mActivity = ba;
		mTwitter = new AsyncTwitterFactory().getInstance();
		mTwitter.setOAuthConsumer(Keys.consumerKey, Keys.consumerSecret);
		mSettings = mActivity.getSharedPreferences(Utils.PREFS_FILE, 0);

		if (mSettings.getString("accessToken", null) != null) {
			mIsLoggedIn = true;
		} else {
			mIsLoggedIn = false;
		}
	}

	public static OAuth getInstance(BaseActivity ba) {
		if (mOAuth == null) {
			mOAuth = new OAuth(ba);
		}

		return mOAuth;
	}

	public AsyncTwitter getAsyncTwitterInstance() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(Keys.consumerKey);
		cb.setOAuthConsumerSecret(Keys.consumerSecret);
		cb.setOAuthAccessToken(mSettings.getString("accessToken", null));
		cb.setOAuthAccessTokenSecret(mSettings.getString("accessSecret", null));
		AsyncTwitterFactory tf = new AsyncTwitterFactory(cb.build());
		return tf.getInstance();
	}
	
	public Twitter getTwitterInstance() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(Keys.consumerKey);
		cb.setOAuthConsumerSecret(Keys.consumerSecret);
		cb.setOAuthAccessToken(mSettings.getString("accessToken", null));
		cb.setOAuthAccessTokenSecret(mSettings.getString("accessSecret", null));
		TwitterFactory tf = new TwitterFactory(cb.build());
		return tf.getInstance();
	}

	public void doLogin() {
		if (!mIsLoggedIn) {
			new GetRequestToken().execute();
		}
	}

	private class GetRequestToken extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				mRequestToken = mTwitter
						.getOAuthRequestToken(Utils.CALLBACK_URL);
				Editor e = mSettings.edit();
				e.putString("requestToken", mRequestToken.getToken());
				e.putString("requestSecret", mRequestToken.getTokenSecret());
				e.commit();
			} catch (TwitterException ex) {
				Log.e(TAG, ex.getMessage(), ex);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			String authUrl = mRequestToken.getAuthenticationURL();
			Log.i(TAG, "Launching intent to get Twitter authentication");
			mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(authUrl)));
		}
	}

	public void getAccessToken(Uri uri) {
		if (!mIsLoggedIn && uri.toString().startsWith(Utils.CALLBACK_URL)) {
			String requestToken = mSettings.getString("requestToken", null);
			String requestSecret = mSettings.getString("requestSecret", null);

			mRequestToken = new RequestToken(requestToken, requestSecret);

			new GetAccessToken(uri).execute();
		}
	}

	private class GetAccessToken extends AsyncTask<Void, Void, Void> {

		private AccessToken mAt;
		private Uri mUri;

		public GetAccessToken(Uri uri) {
			mAt = null;
			mUri = uri;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mAt = mTwitter.getOAuthAccessToken(mRequestToken,
						mUri.getQueryParameter("oauth_verifier"));
			} catch (TwitterException e) {
				Log.e(TAG, "Could not retrieve access token.", e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			if (mAt == null) {
				return;
			}

			Editor e = mSettings.edit();
			e.putString("accessToken", mAt.getToken());
			e.putString("accessSecret", mAt.getTokenSecret());
			e.commit();

			mIsLoggedIn = true;
			mActivity.onLoginCompleted();
		}
	}

	public boolean isLoggedIn() {
		return mIsLoggedIn;
	}
}
