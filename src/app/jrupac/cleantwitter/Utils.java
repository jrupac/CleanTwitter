package app.jrupac.cleantwitter;

import android.content.Context;
import android.widget.Toast;

public final class Utils {

	public static final String TAG_BASE = "CT_";

	public static final String CALLBACK_URL = "cleantwitter://login";

	public static final String PREFS_FILE = TAG_BASE + "prefs";

	public static final int MESSAGES_TAB = 0;
	public static final int TWEETS_TAB = 1;
	public static final int MENTIONS_TAB = 2;

	public static final int SETTINGS = 0;
	public static final int LOGIN = 1;

	public static void message(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
