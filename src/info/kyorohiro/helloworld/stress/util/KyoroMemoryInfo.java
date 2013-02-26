package info.kyorohiro.helloworld.stress.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

public class KyoroMemoryInfo {
	public KyoroMemoryInfo() {
		setMethod();
	}

	public List<RunningAppProcessInfo>  getRunningAppList(Context context) {
		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApp = manager.getRunningAppProcesses();
		return runningApp;
	}

	
	public ActivityManager.MemoryInfo getMemoryInfo(Context context) {
	    ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
	    activityManager.getMemoryInfo(memoryInfo);
		return memoryInfo;
	}

	public String memInfo(Context context, int pid) {
	    ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
	    activityManager.getMemoryInfo(memoryInfo);
	    String extra = "";
	    if(Build.VERSION.SDK_INT >= 5) {
	    	int[] pids = new int[]{pid};
	    	android.os.Debug.MemoryInfo[] infos= getMemInfoData(context, pids);
	    	if(infos != null && infos.length >0){
	    		android.os.Debug.MemoryInfo info = infos[0];
	    		extra = ":"+
	    		//",dpd=" + info.dalvikPrivateDirty +
	    		//",dp=" + info.dalvikPss+ 
	    		//",dsd=" + info.dalvikSharedDirty +
	    		//",npd=" + info.nativePrivateDirty +
	    		//",np=" + info.nativePss +
	    		//",nsd=" + info.nativeSharedDirty +
	    		//",opd=" + info.otherPrivateDirty +
	    		//",op=" + info.otherPss +
	    		",TPD=" + getTotalPrivateDirty(info)+
	    		"kb,TPss=" + getTotalPss(info) + 
	    		"kb,TSD=" + getTotalSharedDirty(info)+"kb";
	    	}
	    }
	     return extra;
	}

	public android.os.Debug.MemoryInfo[] getMemInfoData(Context context, int[] pids) {
		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		android.os.Debug.MemoryInfo[] memoryInfoArray = getProcessMemoryInfo(manager, pids);
		return memoryInfoArray;
	}

	
	public void systemOut(android.os.Debug.MemoryInfo[] memoryInfoArray) {
		for(android.os.Debug.MemoryInfo pidMemoryInfo: memoryInfoArray) {
			Log.v("kyorohiro", "===================================================");
			Log.v("kyorohiro", "getTotalPrivateDirty: " + getTotalPrivateDirty(pidMemoryInfo));
			Log.v("kyorohiro", "getTotalSharedDirty: " + getTotalSharedDirty(pidMemoryInfo));
			Log.v("kyorohiro", "getTotalPss: " + getTotalPss(pidMemoryInfo));
			Log.v("kyorohiro", "---------------------------------------------------");
			Log.v("kyorohiro", "dalvikPrivateDirty: " + pidMemoryInfo.dalvikPrivateDirty);
			Log.v("kyorohiro", "dalvikPss: " + pidMemoryInfo.dalvikPss);
			Log.v("kyorohiro", "dalvikSharedDirty: " + pidMemoryInfo.dalvikSharedDirty);
			Log.v("kyorohiro", "nativePrivateDirty: " + pidMemoryInfo.nativePrivateDirty);
			Log.v("kyorohiro", "nativePss: " + pidMemoryInfo.nativePss);
			Log.v("kyorohiro", "nativeSharedDirty: " + pidMemoryInfo.nativeSharedDirty);
			Log.v("kyorohiro", "otherPrivateDirty: " + pidMemoryInfo.otherPrivateDirty);
			Log.v("kyorohiro", "otherPss: " + pidMemoryInfo.otherPss);
			Log.v("kyorohiro", "otherSharedDirty: " + pidMemoryInfo.otherSharedDirty);
			Log.v("kyorohiro", "===================================================");
		}
	}

	private static final Class<?>[] mGetProcessMemoryInfoSignature = new Class[] {int[].class};
	private Method mGetProcessMemoryInfo;
	private Method mGetTotalPrivateDirty;
	private Method mGetTotalPss;
	private Method mGetTotalSharedDirty;

	private boolean support = false;
	private Object[] mGetProcessMemoryInfoArgs = new Object[1];

	public void setMethod() {
		try {
			mGetProcessMemoryInfo = ActivityManager.class.getMethod("getProcessMemoryInfo", mGetProcessMemoryInfoSignature);
			mGetTotalPrivateDirty = android.os.Debug.MemoryInfo.class.getMethod("getTotalPrivateDirty", null);
			mGetTotalPss = android.os.Debug.MemoryInfo.class.getMethod("getTotalPss", null);
			mGetTotalSharedDirty = android.os.Debug.MemoryInfo.class.getMethod("getTotalSharedDirty", null);
			support = true;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	public synchronized android.os.Debug.MemoryInfo[] getProcessMemoryInfo(ActivityManager manager, int[] pids) {
		if(support){
			mGetProcessMemoryInfoArgs[0] = pids;
			Object ret = invokeMethod(manager, mGetProcessMemoryInfo, mGetProcessMemoryInfoArgs);
			if(ret != null) {
				return (android.os.Debug.MemoryInfo[])ret;
			}
		}
		return null;
	}

	public synchronized int getTotalPrivateDirty(android.os.Debug.MemoryInfo o) {
		if(support){
			Object ret = invokeMethod(o, mGetTotalPrivateDirty, null);
			if(ret != null) {
				return (Integer)ret;
			}
		}
		return 0;
	}

	public synchronized int getTotalPss(android.os.Debug.MemoryInfo o) {
		if(support){
			Object ret = invokeMethod(o, mGetTotalPss, null);
			if(ret != null) {
				return (Integer)ret;
			}
		}
		return 0;
	}

	public synchronized int getTotalSharedDirty(android.os.Debug.MemoryInfo o) {
		if(support){
			Object ret = invokeMethod(o, mGetTotalSharedDirty, null);
			if(ret != null) {
				return (Integer)ret;
			}
		}
		return 0;
	}


	private Object invokeMethod(Object o, Method method, Object[] args) {
		try {
			return  method.invoke(o, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// Should not happen.
			e.printStackTrace();
		}
		return null;
	}
}