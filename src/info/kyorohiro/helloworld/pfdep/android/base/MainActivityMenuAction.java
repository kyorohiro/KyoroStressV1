package info.kyorohiro.helloworld.pfdep.android.base;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public interface MainActivityMenuAction {
	boolean onPrepareOptionsMenu(Activity activity, Menu menu);
	boolean onMenuItemSelected(Activity activity, int featureId, MenuItem item);
}
