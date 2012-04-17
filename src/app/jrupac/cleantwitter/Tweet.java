package app.jrupac.cleantwitter;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.drawable.Drawable;

public class Tweet {
	public String text;
	public String username;
	public String name;
	public Date time;
	public String avatar_url;
	public Drawable avatar = null;
	public AtomicBoolean isDownloading = new AtomicBoolean(false);
}
