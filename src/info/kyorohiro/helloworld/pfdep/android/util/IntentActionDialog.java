package info.kyorohiro.helloworld.pfdep.android.util;


import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class IntentActionDialog extends android.app.Dialog {
	private Activity mOwnerActivity = null;
	private ListView mCurrentFileList = null;
	private LinearLayout mLayout = null;
	private File mFile = null;

	private ViewGroup.LayoutParams mParams = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);

	public static SimpleFileExplorer createDialog(Activity owner, File dir) {
		return new SimpleFileExplorer(owner, owner, dir, SimpleFileExplorer.MODE_FILE_SELECT);
	}

	public IntentActionDialog(Context context, Activity owner, File file) {
		super(context);
		mLayout = new LinearLayout(context);
		mLayout.setOrientation(LinearLayout.VERTICAL);
		mOwnerActivity = owner;
		mFile = file;
		mCurrentFileList = new ListView(context);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init();
		addContentView(mLayout, mParams);
	}


	private void itemClickAction(String item){
		sendMail("KyoroLogcat","xxx@example.com", "", new File[]{mFile});
	}

	public void sendMail(String subject, String address, String body, File[] attachfile){
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent .setType("plain/text");
		emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL,  new String[]{address});
		emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, body.toString());
		emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
//		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (File file : attachfile) {
			Uri u = Uri.fromFile(file);
//			uris.add(u);
			emailIntent.putExtra(Intent.EXTRA_STREAM, u);
		}
//		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		
		if(mOwnerActivity != null) {
			mOwnerActivity.getApplicationContext().startActivity(emailIntent);
		}

	}

	private void init() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_list_item_1);

		mLayout.addView(mCurrentFileList, mParams);
		mCurrentFileList.setAdapter(adapter);
		mCurrentFileList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				String item = ((ArrayAdapter<String>) mCurrentFileList
						.getAdapter()).getItem(pos);
				itemClickAction(item);
		}
		});
		adapter.add("send mail(attach)");
	}
}
