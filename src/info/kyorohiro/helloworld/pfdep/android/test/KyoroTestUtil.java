package info.kyorohiro.helloworld.pfdep.android.test;

import info.kyorohiro.helloworld.util.AsyncronousTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.view.View;

public class KyoroTestUtil {

	private TestApplication mApplication = null;

	public KyoroTestUtil(TestApplication application) {
		mApplication = application;
	}
	
	// capture screen
	public void captureScreen(View target, File outputPath, boolean async) {
		if(mApplication!=null){mApplication.showMessage("begin captureScreen");}
		if(mApplication!=null){mApplication.showMessage("--"+(outputPath!=null?outputPath.getAbsolutePath():"null"));}
		CaptureScreenTask task = new CaptureScreenTask(mApplication, target, outputPath);
		AsyncronousTask syncTask = new AsyncronousTask(task);
		mApplication.runOnUIThread(syncTask);
		if(async) {
			syncTask.syncTask();
		}
		if(mApplication!=null){mApplication.showMessage("end captureScreen");}
	}

	public static class CaptureScreenTask implements Runnable {
		private View mTarget = null;
		private File mOutputPath = null;
		private TestApplication mApplication = null;

		public CaptureScreenTask(TestApplication application, View target, File outputPath) {
			mTarget = target;
			mOutputPath = outputPath;
			mApplication = application;
		}

		@Override
		public void run() {
			if(mApplication!=null){mApplication.showMessage("begin captureScreen task");}
			BufferedOutputStream fio = null;
			try {
				try {
					mTarget.destroyDrawingCache();
					Thread.sleep(2000);
					mTarget.buildDrawingCache(false);
					Bitmap cash = mTarget.getDrawingCache();
					fio = new BufferedOutputStream(new FileOutputStream(mOutputPath));
					if(!cash.compress(Bitmap.CompressFormat.PNG, 100, fio)){
						if(mApplication!=null){mApplication.showMessage("failed to capture screen --0--");}
					}
				} finally {
					fio.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				if(mApplication!=null){mApplication.showMessage("failed to capture screen --1--");}
			} catch (IOException e) {
				e.printStackTrace();
				if(mApplication!=null){mApplication.showMessage("failed to capture screen --2--");}
			} catch (NullPointerException e) {
				e.printStackTrace();
				if(mApplication!=null){mApplication.showMessage("failed to capture screen --3--");}
			} catch ( InterruptedException e) {
				e.printStackTrace();
				if(mApplication!=null){mApplication.showMessage("failed to capture screen --4--");}				
			} finally {
				if(mApplication!=null){mApplication.showMessage("end captureScreen task");}
			}
		}
	}
}
