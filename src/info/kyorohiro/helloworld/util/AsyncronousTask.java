package info.kyorohiro.helloworld.util;

//
// add syncTask() in Runnable. syncTask is wait for finishing task。
//
public class AsyncronousTask implements Runnable {
	private boolean mIsAlive = false;
	private Runnable mTask = null;

	public AsyncronousTask(Runnable task) {
		mIsAlive = true;
		mTask = task;
	}

	public Runnable getTask() {
		return mTask;
	}

	public void run() {
		try {
			log("start task");
			mTask.run();
		} finally {
			mIsAlive = false;
			awake();
			log("end task");
		}
	}

	public synchronized void awake() {
		log("awake");
		notifyAll();
	}

	//
	// if interrupted, return false else return true
	public synchronized boolean syncTask() {
		if (mIsAlive) {
			try {
				log("before wait");
				wait();
				log("after wait");
				return true;
			} catch (InterruptedException e) {
				log("interrupted");
				e.printStackTrace();
				return false;
			}
		} else {
			log("none");
			return true;
		}
	}

	private void log(String log) {
//		System.out.println("#AsyncronousTask#"+log);
//		android.util.Log.v("kiyo", "#AsyncronousTask#"+log);
	}

}
