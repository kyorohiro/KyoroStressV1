package info.kyorohiro.helloworld.pfdep.android.base;

import java.util.HashMap;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TestResult {
	public static HashMap<String,TestResult>stCurrentTest = new HashMap<String,TestResult>();

	public static String TEST_PASSED = "passed";
	public static String TEST_FAILED = "failed";
	public static String TEST_NODATA = "nodata";
	public static String EMPTY_MESSAGE = "";

	public String mResult = TEST_NODATA;
	public String mMessage = EMPTY_MESSAGE;

	private boolean mSettedResult = false;

	public synchronized void waitBySelectTestResult(long timeout) {
		if(mSettedResult == true){
			return;
		}

		try {
			wait(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.add(TEST_PASSED);
		menu.add(TEST_FAILED);
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(TEST_PASSED.equals(item.getTitle())) {
			setResult(TEST_PASSED, "");
			return true;
		}
		else if(TEST_FAILED.equals(item.getTitle())) {
			setResult(TEST_FAILED, "");
			return true;
		}
		else {
			return false;
		}
	}

	public static void saveInstance(Bundle outState, String key, TestResult result) {
		outState.putString(TestResult.class.getName(), key);
		stCurrentTest.put(key, result);
	}

	public static TestResult restoreInstance(Bundle savedInstanceState, String key) {
		savedInstanceState.putString(TestResult.class.getName(), key);
		return stCurrentTest.get(key);
	}

	public synchronized void setResult(String result, String message){
		notifyAll();
		mResult = result;
		mMessage = message;
		mSettedResult = true;
	}

	public synchronized String getResult(int timeout) {
		waitBySelectTestResult(timeout);
		return mResult;
	}

	public synchronized String getMessage(int timeout) {
		waitBySelectTestResult(timeout);
		return mMessage;
	}
}
