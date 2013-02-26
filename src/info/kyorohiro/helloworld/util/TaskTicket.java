package info.kyorohiro.helloworld.util;

public class TaskTicket<T> extends AsyncronousTask {
	private Task<T> mTask = null;
	public TaskTicket(Task<T> task) {
		super(new Task2Run<T>(task));
		mTask = task;
	}

	public Task<T> getTicketTask() {
		return mTask;
	}

	public T getT() throws InterruptedException {
		if(!syncTask()) {
			throw new InterruptedException("--0127--");
		}
		return mTask.get();
	}

	public static class Task2Run<T> implements Runnable {
		private Task<T> mTask = null;
		public Task2Run(Task<T> task) {
			mTask = task;
		}
		@Override
		public void run() {
			mTask.doTask();
		}
	}

	public static interface Task<T> {
		public T get();
		public void doTask();
	}

}
