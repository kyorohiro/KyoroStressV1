package info.kyorohiro.helloworld.pfdep.android.base;
import java.lang.reflect.Constructor;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;



public class TestActivity extends Activity {

	public void changeTitle(Context context,String title, int font, int bg) {
		try {
			TextView titleView = new TextView(context);
			titleView.setText(title);
			titleView.setTextColor(font);
			this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
			int titleContainerId = (Integer) Class.forName("com.android.internal.R$id").getField("title_container").get(null); 
			ViewGroup titleGroup = ((ViewGroup) getWindow().findViewById(titleContainerId));
			titleGroup.setBackgroundColor(bg);
			titleGroup.addView(titleView);
		} catch(Throwable e){ 
			setTitle(title);
		} 
	}

	private int mMenuBgColor =0;
	public void changeMenuBgColor(Context context,int bg) {
		try{
			mMenuBgColor = bg;
			LayoutInflater service = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);  
			final Factory orgFactory = service.getFactory();
			if(orgFactory== null){
				final Factory f = new Factory() {
					public View onCreateView(String name, Context context,
							AttributeSet attrs) {
						try {
							if((""+name).equals("com.android.internal.view.menu.IconMenuView")) { 
								ClassLoader loader = getClassLoader();
								Class<?> clazz = loader.loadClass("com.android.internal.view.menu.IconMenuView");
								Constructor<?> constructor = clazz.getConstructor(Context.class, AttributeSet.class);
								Object view = constructor.newInstance(context, attrs);  
								ViewGroup titleGroup = (ViewGroup) view;
								titleGroup.setBackgroundColor(mMenuBgColor);
								return titleGroup;
							} 
						} catch(Throwable e){ 
						}
						return null;
					}
				};
				service.setFactory(f); 
			}
		}catch(Throwable t){

		}
	}

	/**
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		if(mtCurrentTest != null) {
			mtCurrentTest.onPrepareOptionsMenu(menu);		
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mtCurrentTest != null) {
			TestResult.saveInstance(outState, this.getClass().getName(), mtCurrentTest);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(mtCurrentTest == null) {
			mtCurrentTest = TestResult.restoreInstance(savedInstanceState, this.getClass().getName());
		}
	}

	protected void onResetMenu(Menu menu) {
		;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean parentResult = super.onMenuItemSelected(featureId, item);
		if(parentResult == true || item == null) {
			return parentResult;
		}

		boolean testResult = false;
		if(mtCurrentTest != null) {
			testResult = mtCurrentTest.onMenuItemSelected(featureId, item);
		}

		if(testResult == true) {
			return testResult;
		}
		else {
			return parentResult;
		}
	}

	private TestResult mtCurrentTest = null;
	public TestResult startTest() {
		mtCurrentTest = new TestResult();
		return mtCurrentTest;
	}	
}
/*


	private Controller mController;
	private ControllerAndroidUi mControllerUi;

	public TestActivity() {
		mController = new Controller(1);
	}

	public Controller getController(){
		return mController;
	}

	public ControllerAndroidUi getControllerAndroidUi(){
		return mControllerUi;
	}
private boolean mControllerUiShown = false;
public void showControllerMenu() {
//mControllerUi.startConnectionProcess();
mControllerUi.showControllerMenu();
mControllerUiShown = true;
}

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
mControllerUi = new ControllerAndroidUi(this, mController);
mControllerUiShown = false;

//mControllerUi.startConnectionProcess();
//controller.addButtonListener(this.mStage.getZeemoteControllerExtention());
//controller.addJoystickListener(this.mStage.getZeemoteControllerExtention());
}

@Override
protected void onResume() {
super.onResume();
//if (mControllerUiShown) {
//	mControllerUi.startConnectionProcess();
//}
//} else {
//	mControllerUiShown = false;
//}
}

@Override
protected void onPause() {
super.onPause();
if (!mControllerUiShown) {
	// If the controller activity is not shown,
	// disconnect the Zeemote controller.
	try {
		mController.disconnect();
	} catch (Exception e) {
	}
}
}
*/