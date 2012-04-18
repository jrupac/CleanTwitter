package app.jrupac.cleantwitter;

import twitter4j.ResponseList;
import twitter4j.Status;
import android.graphics.Bitmap;
import android.support.v4.app.ListFragment;
import android.view.View;

public class BaseListFragment extends ListFragment {
	
	public void onForceRefresh() {
		return;
	}
	
	public void onThumbnailDownload(Bitmap bmp, View v) {
		// Sub-classes should override this function to implement
		// functionality upon retrieval of images
		return;
	}

	public void onParseCompleted(ResponseList<Status> statuses) {
		// Sub-classes should override this function to implement
		// functionality upon retrieval of data
		return;
	}
}
