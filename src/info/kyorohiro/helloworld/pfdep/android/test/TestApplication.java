package info.kyorohiro.helloworld.pfdep.android.test;

public interface TestApplication {
	void runOnUIThread(Runnable task);
	void showMessage(String message);
}
