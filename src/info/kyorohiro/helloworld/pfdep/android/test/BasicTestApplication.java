package info.kyorohiro.helloworld.pfdep.android.test;

import android.app.Activity;
import android.widget.Toast;

public class BasicTestApplication implements TestApplication {

	private Activity mTarget = null;
	public BasicTestApplication(Activity activity) {
		mTarget = activity;
	}

	@Override
	public void runOnUIThread(Runnable task) {
		mTarget.runOnUiThread(task);
	}

	@Override
	public void showMessage(String message) {
		mTarget.runOnUiThread(new ToastTask(message));
		android.util.Log.v("test","showMessage("+message+")");
	}

	public class ToastTask implements Runnable {
		private String mMessage = null;
		public ToastTask(String message) {
			mMessage = ""+message;
		}
		@Override
		public void run() {
			try {
				Toast.makeText(mTarget, mMessage, Toast.LENGTH_SHORT);
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
