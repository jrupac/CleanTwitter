package app.jrupac.cleantwitter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class CleanTwitterActivity extends BaseActivity {

	TwitterFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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
		menu.add(0, Utils.SETTINGS, 0, getString(R.string.settings_activity));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Utils.SETTINGS:
			// TODO: Write a settings activity
			Utils.message(this, "Not yet implemented!");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}

class TwitterFragmentAdapter extends FragmentPagerAdapter implements
		TitleProvider {
	private String[] mFragmentNames;
	private int mCount;

	public TwitterFragmentAdapter(FragmentManager fm, String[] fragmentNames) {
		super(fm);
		this.mFragmentNames = fragmentNames;
		mCount = fragmentNames.length;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position % mCount) {
		case Utils.TWEETS_TAB:
			return TimelineFragment.instantiate();

		case Utils.MENTIONS_TAB:
		case Utils.MESSAGES_TAB:
			// TODO: Replace with actual fragments
			return TestFragment.newInstance(mFragmentNames[position % mCount]);
		}

		return null;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	public String getTitle(int position) {
		return mFragmentNames[position % mFragmentNames.length];
	}

	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}
}