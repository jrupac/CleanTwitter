package app.jrupac.cleantwitter;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class CleanTwitterActivity extends BaseActivity {

	public final String TAG = Utils.TAG_BASE + this.getClass().getName();

	private TwitterFragmentAdapter mAdapter;
	private AsyncTwitter mTwitter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private RequestToken mRequestToken;
	private SharedPreferences mSettings;
	private Menu mMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.i(TAG, "Starting CleanTwitterActivity");

		mSettings = getSharedPreferences(Utils.PREFS_FILE, 0);

		mTwitter = new AsyncTwitterFactory().getInstance();
		mTwitter.setOAuthConsumer(Keys.consumerKey, Keys.consumerSecret);

		mAdapter = new TwitterFragmentAdapter(getSupportFragmentManager(),
				getResources().getStringArray(R.array.fragment_names));

		// TODO: Replace with background drawable
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#3282BD")));

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

		mPager.setCurrentItem(Utils.TWEETS_TAB, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenu = menu;
		
		menu.add(0, Utils.SETTINGS, 0, getString(R.string.settings_activity));

		if (Utils.isLoggedIn(mSettings)) {
			menu.add(0, Utils.LOGIN, 1, "Logout");
		} else {
			menu.add(0, Utils.LOGIN, 1, "Login");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Utils.SETTINGS:
			// TODO: Write a settings activity
			Utils.message(this, "Settings not yet implemented!");
			break;
		case Utils.LOGIN:
			if (Utils.isLoggedIn(mSettings)) {
				Utils.message(this, "Logout not yet implemented!");
			} else {
				OAuthLogin();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	void OAuthLogin() {
		Log.d(TAG, "OAuthLogin");
		
		new AsyncTask<Void, Void, Void>() {
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

			protected void onPostExecute(Void param) {
				String authUrl = mRequestToken.getAuthenticationURL();
				Log.i(TAG,
						"Launching intent to get Twitter authentication with url: "
								+ authUrl);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
			}
		}.execute();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Log.d(TAG, "onNewIntent");
		
		if (intent != null && intent.getData() != null) {
			final Uri uri = intent.getData();

			if (uri.toString().startsWith(Utils.CALLBACK_URL)) {
				mRequestToken = new RequestToken(mSettings.getString(
						"requestToken", null), mSettings.getString(
						"requestSecret", null));

				new AsyncTask<Void, Void, Void>() {
					private AccessToken at = null;

					protected Void doInBackground(Void... params) {
						try {
							at = mTwitter.getOAuthAccessToken(mRequestToken,
									uri.getQueryParameter("oauth_verifier"));
						} catch (TwitterException e) {
							Log.e(TAG, "Could not retrieve access token.", e);
						}

						return null;
					}

					protected void onPostExecute(Void param) {
						if (at == null) {
							return;
						}

						Editor e = mSettings.edit();
						e.putString("accessToken", at.getToken());
						e.putString("accessSecret", at.getTokenSecret());
						e.commit();

						mMenu.findItem(Utils.LOGIN).setTitle("Logout");
						
						displayTimeLine();
					}
				}.execute();
			}
		}
	}

	void displayTimeLine() {

		try {
			// List<Status> statuses = null;

			// TODO: Add listeners
			// statuses = mTwitter.getHomeTimeline();

			// Log.d(TAG, statuses.get(0).getText());

		} catch (Exception ex) {
			Utils.message(this, "Error:" + ex.getMessage());
			Log.e(TAG, "Could not display timeline", ex);
		}
	}
}
