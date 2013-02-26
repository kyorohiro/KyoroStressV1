package info.kyorohiro.helloworld.pfdep.android.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;




import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//
// çÏÇËíºÇ∑
//
public class SimpleFileExplorer extends Dialog {

	public static final int MODE_FILE_SELECT = 1;
	public static final int MODE_DIR_SELECT = 3;
	public static final int MODE_NEW_FILE = 5;	
	private Activity mOwnerActivity =  null; 
	private ListView mCurrentFileList = null;
	private SelectedFileAction mAction = null;
	private EditText mEdit = null;
	private Button mSelectButton = null;
	private ImageButton mNewButton = null;
	private ImageButton mSearchButton = null;
	private int mModeDirectory = 0;
	private LinearLayout mLayout = null;
	private File mDir = null;
	private ViewGroup.LayoutParams mParams = 
		new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
	private ViewGroup.LayoutParams mParams2 = 
		new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
	

	public static SimpleFileExplorer createDialog(Activity owner, File dir) {
		return createDialog(owner, dir, MODE_FILE_SELECT);		
	}

	public static SimpleFileExplorer createDialog(Activity owner, File dir, int mode) {
		return new SimpleFileExplorer(owner, owner, dir, mode);		
	}

	public SimpleFileExplorer(Context context, Activity owner, File dir, int mode) {
		super(context);
		mLayout =new LinearLayout(context);
		mLayout.setOrientation(LinearLayout.VERTICAL);
		mOwnerActivity = owner;
		mCurrentFileList = new ListView(context);
		mEdit = new EditText(context);
		mDir = dir;
		mModeDirectory = mode;

		if((mModeDirectory&MODE_DIR_SELECT) == MODE_DIR_SELECT) {
			mSelectButton = new Button(getContext());		
			mSelectButton.setText("select");
		}

		mNewButton = new ImageButton(getContext());
		mNewButton.setImageResource(android.R.drawable.ic_input_add);
		mSearchButton = new ImageButton(getContext());
		mSearchButton.setImageResource(android.R.drawable.ic_media_play);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		init();
		addContentView(mLayout, mParams);
		startUpdateTask(dir);
	}

	public void setOnSelectedFileAction(SelectedFileAction action){
		mAction = action;
	}

	private void startSearchTask(){
		if((mModeDirectory&MODE_NEW_FILE) == MODE_NEW_FILE) {
			return;
		}
		String find = mEdit.getText().toString();
		if(find == null || find.equals("")){
			startUpdateTask(mDir);
		}
		else {
			try {
				startUpdateTask(new UpdateListFromSearchTask(mDir, Pattern.compile(find)));
			} catch(Exception e){
				
			}
		}
	}
	private void init() {
		mEdit.setSelected(false);
		mEdit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		mEdit.setHint("search file : regex(find)");
		mEdit.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// All IME Application take that actionId become imeoption's value.
				startSearchTask();
				return false;
			}
		});

		ArrayAdapter<ListItemWithFile> adapter = 
		 new ArrayAdapter<ListItemWithFile>(
				getContext(),
				android.R.layout.simple_list_item_1);
		if((mModeDirectory&MODE_DIR_SELECT) != MODE_DIR_SELECT){
			LinearLayout sub = new LinearLayout(getContext()) ;
			sub.setOrientation(LinearLayout.HORIZONTAL);
			if((mModeDirectory&MODE_NEW_FILE) == MODE_NEW_FILE) {
				sub.addView(mNewButton);
				sub.addView(mEdit);
			} 
			else if((mModeDirectory&MODE_FILE_SELECT) == MODE_FILE_SELECT) {			
				sub.addView(mSearchButton);
				sub.addView(mEdit);
			}
			mLayout.addView(sub, mParams);
		}
		//ScrollView s = new ScrollView(getContext());
		//s.addView(mCurrentFileList,mParams2);
		if (mSelectButton != null) {
			mLayout.addView(mSelectButton,mParams);
		}
		mLayout.addView(mCurrentFileList, mParams);
		mCurrentFileList.setAdapter(adapter);
		mCurrentFileList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int pos, long id) {
				ListItemWithFile item = ((ArrayAdapter<ListItemWithFile>)mCurrentFileList.getAdapter()).getItem(pos);
				File f = item.getFile();
				if(f.exists() && f.isDirectory()){
					mDir = f;
				}
				if(mAction == null || mAction != null) {
					if(mAction.onSelectedFile(f,SelectedFileAction.CLICK)) {
						try {
							SimpleFileExplorer.this.dismiss();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					} else {
						startUpdateTask(f);
					}
				}
			}
		});
		mCurrentFileList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
				ListItemWithFile item = ((ArrayAdapter<ListItemWithFile>)mCurrentFileList.getAdapter()).getItem(pos);
				File f = item.getFile();
				if(f.exists() && f.isDirectory()){
					mDir = f;
				}
				if(mAction != null) {
					if(mAction.onSelectedFile(f,SelectedFileAction.LONG_CLICK)) {
						try {
							SimpleFileExplorer.this.stopUpdateTask();
							SimpleFileExplorer.this.dismiss();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
				return false;
			}
		});

		if (mSelectButton != null) {
			mSelectButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mAction != null) {
						if(mAction.onSelectedFile(mDir, SelectedFileAction.PUSH_SELECT)) {
							try {
								SimpleFileExplorer.this.dismiss();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		}
		if(mSearchButton != null) {
			mSearchButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startSearchTask();
				}
			});
		}
		if(mNewButton != null) {
			mNewButton.setOnClickListener(new View.OnClickListener() {
				private EditText mNewFileName = null;
				private AlertDialog mDialog = null;
				public void showDialog() {
					AlertDialog.Builder b = new AlertDialog.Builder(getContext());
					mNewFileName = new EditText(getContext());
					b.setTitle("new filename----");
					b.setView(mNewFileName);
					b.setPositiveButton("OK", new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							if(mNewFileName.getText() == null){
								return;
							}
							String t = mNewFileName.getText().toString();
							if(mAction.onSelectedFile(new File(mDir,""+t), SelectedFileAction.PUSH_SELECT)) {
								try {
									mDialog.dismiss();
									SimpleFileExplorer.this.dismiss();
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
						}
					});
					mDialog = b.show();
//					mDialog = b.create();
//					mDialog.show();
				}
				@Override
				public void onClick(View v) {
//					if(mAction != null&&mEdit != null && mEdit.getText()!=null) {
//						
						//if(mAction.onSelectedFile(new File(mDir,""+mEdit.getText().toString()), SelectedFileAction.PUSH_SELECT)) {
						//	try {
								showDialog();
//								SimpleFileExplorer.this.dismiss();
//							} catch (Throwable e) {
	//							e.printStackTrace();
		//					}
			//			}
				//	}
				}
			});
		}
	}

	private Thread mTh = null;
	private synchronized void startUpdateTask(File f) {
		stopUpdateTask();
		mTh = new Thread(new UpdateListFromDirTask(f));
		mTh.start();
	}

	private synchronized void stopUpdateTask() {
		Thread th = mTh;
		mTh = null;
		if(th != null && th.isAlive() &&th.isInterrupted()) {
			th.interrupt();
		}
		Thread.yield();		
	}

	private synchronized void startUpdateTask(Runnable task) {
		stopUpdateTask();
		mTh = new Thread(task);
		mTh.start();
	}

	private synchronized void add(ArrayAdapter adapter){
		add(adapter,Thread.currentThread());
	}

	private synchronized void add(ArrayAdapter adapter, Thread owner){
		if(mTh == owner){
			mCurrentFileList.setAdapter(adapter);
		}
	}
	

	private synchronized boolean checkEnding() {
		if(mTh == Thread.currentThread()){
			return false;
		}
		else {
			return true;
		}
	}


	public class UpdateListFromSearchTask implements Runnable {
		private File mDir = null;
		private Pattern mPattern = null;

		public UpdateListFromSearchTask(File dir, Pattern pattern) {
			mDir = dir;
			mPattern = pattern;
		}

		@Override
		public void run() {
			ArrayList<ListItemWithFile> mOutput = new ArrayList<ListItemWithFile>();
			ArrayList<File> mTmp = new ArrayList<File>();
			
			try {
				SimpleFileExplorer.this.mOwnerActivity.runOnUiThread(new UpdateTitleTask("search...",Thread.currentThread()));
				mTmp.add(mDir);
				File t = null;
				File[] l = null;
				while(!mTmp.isEmpty()) {
					t = mTmp.get(mTmp.size()-1);
					mTmp.remove(t);
					if(t == null){
						continue;
					}
					l = t.listFiles();
					if(l == null){
						continue;
					}
					for(File f : l){
						if(!f.exists()){
							continue;
						}
						if((mModeDirectory&MODE_DIR_SELECT) == MODE_DIR_SELECT&&!f.isDirectory()){
							continue;
						}
						if(f.isDirectory()){
							mTmp.add(f);
						}
						if(f.isFile()){
							if(mPattern.matcher(f.getPath()).find()) {
								mOutput.add(new ListItemWithFile(f));
							}
						}
					}
				}
				SimpleFileExplorer.this.mOwnerActivity.runOnUiThread(new UpdateTitleTask(""+mPattern.pattern()+":"+mDir.getPath(), Thread.currentThread()));
			} catch(Throwable t){
				t.printStackTrace();
				SimpleFileExplorer.this.mOwnerActivity.runOnUiThread(new UpdateTitleTask("failed search",Thread.currentThread()));
			} finally {
				ArrayAdapter<ListItemWithFile> adapter =
					new ArrayAdapter<ListItemWithFile>(
						SimpleFileExplorer.this.getContext(),
						android.R.layout.simple_list_item_1,
						mOutput);				
				SimpleFileExplorer.this.mOwnerActivity.runOnUiThread(new UpdateListTask( adapter,Thread.currentThread()));
			}
		}
		
	}

	public class UpdateListFromDirTask implements Runnable {
		private File mDir = null;

		public UpdateListFromDirTask(File dir) {
			mDir = dir;
		}

		@Override
		public void run() {
			try {
				ArrayAdapter<ListItemWithFile> adapter = new ArrayAdapter<ListItemWithFile>(
						SimpleFileExplorer.this.getContext(),
						android.R.layout.simple_list_item_1);
				if(!mDir.exists()|| !mDir.isDirectory()){
					return;
				}

				if(mDir.getParent() != null && mDir.exists() && !mDir.isFile()) {
					adapter.add(new ListItemWithFile(mDir.getParentFile(), "../"));
				}

				File[] list = mDir.listFiles();
				if(list == null){
					return;
				}
				Arrays.sort(list);
				for( File f : list) {
					if(checkEnding()){
						return;
					}
					if((mModeDirectory&MODE_DIR_SELECT) == MODE_DIR_SELECT&& !f.isDirectory()) {
						continue;
					}
					adapter.add(new ListItemWithFile(f));
				}
				SimpleFileExplorer.this.mOwnerActivity.runOnUiThread(new UpdateListTask(adapter,Thread.currentThread()));
				SimpleFileExplorer.this.mOwnerActivity.runOnUiThread(new UpdateTitleTask(mDir.getPath(),Thread.currentThread()));

			} catch(Throwable t) {
				t.printStackTrace();
			} finally {
			}
		}
	}

	public class UpdateListTask implements Runnable {
		private ArrayAdapter<ListItemWithFile> mAdapter;
		private Thread mOwner;
		
		public UpdateListTask(ArrayAdapter<ListItemWithFile> adapter, Thread owner) {
			mAdapter = adapter;
			mOwner = owner;
		}
		@Override
		public void run() {
			add(mAdapter, mOwner);
		}
	}

	public class UpdateTitleTask implements Runnable {
		private String mTitle;
		private Thread mOwner;
		
		public UpdateTitleTask(String title, Thread owner) {
			mTitle = title;
			mOwner = owner;
		}
		@Override
		public void run() {
			SimpleFileExplorer.this.setTitle(mTitle);
		}
	}

	public static class ListItemWithFile {
		private File mPath = null;
		private String mOutput = "";

		public ListItemWithFile(File path, String output){
			mPath = path;
			mOutput = ""+output;
		}

		public ListItemWithFile(File path) {
			mPath = path;
			mOutput = path.getName();
			if (path.isDirectory()) {
				mOutput = mOutput+"/";
			}
		}
		
		@Override
		public String toString() {
			return mOutput;
		}
		
		public File getFile() {
			return mPath;
		}	
	}


	public static interface SelectedFileAction {
		public static String LONG_CLICK = "long click";
		public static String CLICK = "click";
		public static String PUSH_SELECT = "select";
		
		/**
		 * @param file is user selected file
		 * @return if end dialog return true, else return false;  
		 */
		public boolean onSelectedFile(File file, String action);
	}
}
