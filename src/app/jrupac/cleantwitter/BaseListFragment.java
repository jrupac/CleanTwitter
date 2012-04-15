package app.jrupac.cleantwitter;

import android.graphics.Bitmap;
import android.support.v4.app.ListFragment;
import android.view.View;

public class BaseListFragment<T> extends ListFragment {
	public void onThumbnailDownload(Bitmap bmp, View v) {
		// Sub-classes should override this function to implement
		// functionality upon retrieval of images
		return;
	}

	public void onParseCompleted(T[] t) {
		// Sub-classes should override this function to implement
		// functionality upon retrieval of data
		return;
	}
}
