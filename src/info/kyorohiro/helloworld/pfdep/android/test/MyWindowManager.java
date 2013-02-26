package info.kyorohiro.helloworld.pfdep.android.test;

import java.lang.reflect.Field;

import android.view.View;

//
// study from robotium.
//
public class MyWindowManager {
	private static Class<?> sWindowManager;
	private static String sWindowManagerString;
	public static String CLASSNAME_OVER_17 = "android.view.WindowManagerGlobal";
	public static String CLASSNAME_UNDER_17 = "android.view.WindowManagerImpl";
	public static String FIELDNAME_VIEW = "mViews";
	public static String WINDOWMANAGER_NAME_OVER_17 = "sDefaultWindowManager";
	public static String WINDOWMANAGER_NAME_OVER_13 = "sWindowManager";
	public static String WINDOWMANAGER_NAME_UNDER_13 = "mWindowManager";

	static{
		try {
			if (android.os.Build.VERSION.SDK_INT >= 17) {
				sWindowManager = Class.forName(CLASSNAME_OVER_17);
			} else {
				sWindowManager = Class.forName(CLASSNAME_UNDER_17); 
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		if (android.os.Build.VERSION.SDK_INT >= 17) {
			sWindowManagerString = "sDefaultWindowManager";
			
		} else if(android.os.Build.VERSION.SDK_INT >= 13) {
			sWindowManagerString = "sWindowManager";

		} else {
			sWindowManagerString = "mWindowManager";
		}
	}

	public synchronized View[] getWindowViews() {
		try {
			Field view = sWindowManager.getDeclaredField(FIELDNAME_VIEW);
			Field instance = sWindowManager.getDeclaredField(sWindowManagerString);
			view.setAccessible(true);
			instance.setAccessible(true);
			Object ret = instance.get(null);
			return (View[]) view.get(ret);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
