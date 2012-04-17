package app.jrupac.cleantwitter;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TimelineFragment extends BaseListFragment<Tweet> {

	public final String TAG = Utils.TAG_BASE + this.getClass().getName();

	private static TimelineFragment mTimelineFragment = null;

	private View mView;
	private ListView mListView;
	private Tweet[] mUpdatedTweets = null;
	private TweetAdapter mAdapter = null;
	private Context mContext;
	private OAuth mOAuth;

	public static TimelineFragment getInstance() {
		if (mTimelineFragment == null) {
			mTimelineFragment = new TimelineFragment();
		}

		return mTimelineFragment;
	}

	@Override
	public void onForceRefresh() {
		Log.i(TAG, "Getting updates for timeline");
		TwitterAPI.getInstance().getTimeline(this);
	}
	
	@Override
	public void onParseCompleted(Tweet[] tweets) {
		mUpdatedTweets = tweets;
		postResults(false);
	}

	@Override
	public void onThumbnailDownload(Bitmap bmp, View v) {
		((ImageView) v).setImageBitmap(bmp);
	}

	private void postResults(boolean getFromDB) {
		mAdapter = new TweetAdapter(mContext, R.layout.timeline_listitem,
				mUpdatedTweets);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.timeline, container, false);
		mListView = (ListView) mView.findViewById(android.R.id.list);
		mListView.setEmptyView(mView.findViewById(android.R.id.empty));
		mContext = getActivity().getApplicationContext();
		mOAuth = OAuth.getInstance((BaseActivity) getActivity());

		if (mOAuth.isLoggedIn()) {
			Log.i(TAG, "Getting updates for timeline");
			TwitterAPI.getInstance().getTimeline(this);
		}

		return mView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private class TweetAdapter extends ArrayAdapter<Tweet> {
		private Tweet[] mTweets;
		private LayoutInflater mInflater;
		private View mView;
		private SimpleDateFormat mSdf;
		private final String DATE_FORMAT = "hh:mm aa";

		public TweetAdapter(Context context, int textViewResourceId,
				Tweet[] objects) {
			super(context, textViewResourceId, objects);
			mTweets = objects;
			mInflater = LayoutInflater.from(context);
			mSdf = new SimpleDateFormat(DATE_FORMAT);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				mView = mInflater.inflate(R.layout.timeline_listitem, null);

				holder = new ViewHolder();
				holder.name = (TextView) mView.findViewById(R.id.tweet_name);
				holder.username = (TextView) mView
						.findViewById(R.id.tweet_username);
				holder.time = (TextView) mView.findViewById(R.id.tweet_time);
				holder.text = (TextView) mView.findViewById(R.id.tweet_text);
				holder.avatar = (ImageView) mView
						.findViewById(R.id.tweet_avatar);
				mView.setTag(holder);
			} else {
				mView = convertView;
				holder = (ViewHolder) mView.getTag();
			}

			Tweet current = mTweets[position];
			holder.name.setText(Html.fromHtml(current.name));
			holder.username.setText(Html.fromHtml(current.username));

			if (current.time == null) {
				holder.time.setText("");
			} else {
				holder.time.setText(mSdf.format(current.time));
			}

			holder.text.setText(Html.fromHtml(current.text));

			if (current.avatar == null) {
				new ThumbnailDownloader(holder.avatar, current,
						TimelineFragment.this).execute();
				holder.avatar.setImageResource(R.drawable.ic_launcher);
			} else {
				holder.avatar.setImageBitmap(current.avatar);
			}

			return mView;
		}
	}
}

class ViewHolder {
	public TextView name;
	public TextView username;
	public TextView time;
	public TextView text;
	public ImageView avatar;
}
