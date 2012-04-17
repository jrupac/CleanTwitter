package app.jrupac.cleantwitter;

import com.viewpagerindicator.TitleProvider;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TwitterFragmentAdapter extends FragmentPagerAdapter implements
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
			return TimelineFragment.getInstance();

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
