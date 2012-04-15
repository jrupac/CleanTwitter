package app.jrupac.cleantwitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

public class TwitterAPI {

	public final String TAG = Utils.TAG_BASE + this.getClass().getName();

	private final String DATE_FORMAT = "ccc MMM dd HH:mm:ss Z yyyy";
	private static TwitterAPI mInstance = null;

	String resp = null;

	public static TwitterAPI getInstance() {
		if (mInstance == null) {
			mInstance = new TwitterAPI();
		}

		return mInstance;
	}

	public void getTimeline(BaseListFragment<Tweet> tf) {
		// TODO: Replace with authenticated request after login 
		new HTTPS_GET(tf,
				"https://api.twitter.com/1/statuses/public_timeline.json?include_entities=true")
				.execute();
	}

	class HTTPS_GET extends AsyncTask<Void, Void, Void> {
		private URI mUrl;
		private String mStrURL;
		private BaseListFragment<Tweet> mTf;

		public HTTPS_GET(BaseListFragment<Tweet> tf, String strURL) {
			mTf = tf;
			mStrURL = strURL;
			mUrl = null;
		}

		@Override
		protected void onPreExecute() {
			try {
				mUrl = new URI(mStrURL);
			} catch (Exception e) {
				Log.e(TAG, "Failed to parse URL.", e);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (mUrl == null) {
				return null;
			}

			BufferedReader in = null;

			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				request.setURI(mUrl);
				in = new BufferedReader(new InputStreamReader(client
						.execute(request).getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";

				while ((line = in.readLine()) != null) {
					sb.append(line);
				}

				in.close();
				resp = sb.toString();
			} catch (Exception e) {
				Log.e(TAG, "Could not parse: " + e.getMessage(), e);
				// Do something...
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			if (mUrl == null) {
				return;
			}

			Tweet[] timeline = null;
			JSONObject jsonObject;
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);

			try {
				JSONArray jsonArr = (JSONArray) new JSONTokener(resp)
						.nextValue();
				timeline = new Tweet[jsonArr.length()];

				for (int i = 0; i < timeline.length; i++) {
					jsonObject = jsonArr.getJSONObject(i);
					Tweet t = new Tweet();
					
					try {
						t.time = df.parse(jsonObject.getString("created_at"));
					} catch (ParseException e) {
						t.time = null;
						Log.e(TAG, "Failed to parse date.", e);
					}

					t.text = jsonObject.getString("text");

					jsonObject = jsonObject.getJSONObject("user");
					t.name = jsonObject.getString("name");
					t.username = "@" + jsonObject.getString("screen_name");
					t.avatar_url = jsonObject.getString("profile_image_url");

					timeline[i] = t;
				}
			} catch (JSONException e) {
				Log.e(TAG, "Could not parse JSON.", e);
			}

			mTf.onParseCompleted(timeline);
		}

	}

}
