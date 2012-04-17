package app.jrupac.cleantwitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private Menu mMenu;
	private OAuth mOAuth;
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.i(TAG, "Starting CleanTwitterActivity");

		mOAuth = OAuth.getInstance(this);

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

		if (mOAuth.isLoggedIn()) {
			menu.add(0, Utils.LOGIN, 1, getString(R.string.logout));
		} else {
			menu.add(0, Utils.LOGIN, 1, getString(R.string.login));
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
			if (mOAuth.isLoggedIn()) {
				Utils.message(this, "Logout not yet implemented!");
			} else {
				getProgressDialog();
				mProgressDialog.show();
				mOAuth.doLogin();
				mProgressDialog.dismiss();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Log.d(TAG, "onNewIntent");

		if (intent != null && intent.getData() != null) {
			mProgressDialog.show();
			mOAuth.getAccessToken(intent.getData());
		}
	}

	@Override
	public void onLoginCompleted() {
		Log.d(TAG, "Login successfully completed!");
		
		mProgressDialog.dismiss();

		mMenu.findItem(Utils.LOGIN).setTitle(getString(R.string.logout));
		((TimelineFragment) mAdapter.getItem(Utils.TWEETS_TAB)).onForceRefresh();
	}
	
	private void getProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIndeterminate(true);
	}
}
