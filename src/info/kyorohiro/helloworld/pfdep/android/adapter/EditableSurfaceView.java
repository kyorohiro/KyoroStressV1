package info.kyorohiro.helloworld.pfdep.android.adapter;

import info.kyorohiro.helloworld.display.simple.CommitText;
import info.kyorohiro.helloworld.display.simple.IMEController;
import info.kyorohiro.helloworld.display.simple.MyInputConnection;
import info.kyorohiro.helloworld.pfdep.android.util.Utility;

import java.util.LinkedList;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

/**
 * todo Android�Ɉˑ����Ă��镔���͕�������
 */
public class EditableSurfaceView extends MultiTouchSurfaceView {

	private InputMethodManager mManager = null;
	private _MyInputConnection mCurrentInputConnection = null;
	private boolean mIMEIsShow = false;

	// private MetaStateForAndroid mMetaState = new MetaStateForAndroid();

	public EditableSurfaceView(Context context) {
		super(context);
		mManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	public void clearFocus() {
		super.clearFocus();
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		outAttrs.inputType = // EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS|
		EditorInfo.TYPE_CLASS_TEXT;
		outAttrs.imeOptions =
		// EditorInfo.IME_ACTION_UNSPECIFIED|
		// EditorInfo.IME_ACTION_NONE|
		// EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION|
		// EditorInfo.IME_FLAG_NO_ENTER_ACTION|
		// EditorInfo.IME_ACTION_DONE|
		EditorInfo.IME_FLAG_NO_EXTRACT_UI;
		/*
		 * EditorInfo.IME_ACTION_UNSPECIFIED|
		 * //EditorInfo.IME_MASK_ACTION|EditorInfo.IME_MASK_ACTION|
		 * //EditorInfo.IME_ACTION_DONE| //EditorInfo.IME_FLAG_NO_ENTER_ACTION|
		 * EditorInfo.IME_FLAG_NO_EXTRACT_UI;
		 */

		if (mCurrentInputConnection == null) {
			mCurrentInputConnection = new _MyInputConnection(this, true);
		}
		// mManager.
		return mCurrentInputConnection;
	}

	public MyInputConnection getCurrentInputConnection() {
		return mCurrentInputConnection;
	}

	public void showInputConnection() {
		mManager.showSoftInput(this, 0);
		mIMEIsShow = true;
	}

	public void hideInputConnection() {
		mManager.hideSoftInputFromWindow(this.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		mIMEIsShow = false;
		mComposingText = "";
		mCommitText = "";
		mCommitTextList.clear();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = super.onTouchEvent(event);
		return ret;
	}

	@Override
	public boolean onCheckIsTextEditor() {
		return true;
	}

	private boolean mPushingCtl = false;
	private boolean mPushingAlt = false;

	public boolean pushingCtlForCommitText() {
		return mPushingCtl;
	}

	public boolean pushingAltForCommitText() {
		return mPushingAlt;
	}

	private IMEController mController = new IMEController();

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(Utility.isDebuggingFromCash()) {
			log("dispatchKeyEvent" + event.getKeyCode() + "," + event.toString());
			log("dispatchKeyEvent" + event.getDisplayLabel());
		}
		setMetaForCommit(event.isAltPressed(), pushingCtl(event));
		// if(!mIMEIsShow) {
		// return super.dispatchKeyEvent(event);
		// } else
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				|| event.getKeyCode() == KeyEvent.KEYCODE_MENU
				|| event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN
				|| event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			return super.dispatchKeyEvent(event);
		}
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			mController.binaryKey(mCurrentInputConnection, event.getKeyCode(),
					event.getScanCode(),event.getDisplayLabel(), event.isShiftPressed(), pushingCtl(event),
					event.isAltPressed());
		}
		return true;// super.dispatchKeyEvent(event);
	}

	public static boolean pushingCtl(KeyEvent event) {
		int ctrl = event.getMetaState();
		if (0x1000 == (ctrl & 0x1000) || 0x2000 == (ctrl & 0x2000)
				|| 0x4000 == (ctrl & 0x4000)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean dispatchKeyShortcutEvent(KeyEvent event) {
		if(Utility.isDebuggingFromCash()) {
			log("dispatchKeyShortcutEvent" + 
					(event == null? "" : event.getKeyCode() + ","
							+ event.toString()));
		}
		setMetaForCommit(event.isAltPressed(), pushingCtl(event));
		return super.dispatchKeyShortcutEvent(event);
	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		if(Utility.isDebuggingFromCash()) {
			log("dispatchKeyEventPreIme" +
					(event == null? "":event.getKeyCode() + ","
					+ event.toString()));
			log("dispatchKeyEventPreIme" + 
					(event == null? "":event.getDisplayLabel()));
		}
		// android.util.Log.v("kiyo","dispatchKeyEventPreIme");
		setMetaForCommit(event.isAltPressed(), pushingCtl(event));

		// if(!mIMEIsShow) {
		// return super.dispatchKeyEventPreIme(event);
		// } else
		if (mController.tryUseBinaryKey(
				event.getKeyCode(),event.isShiftPressed(),
				pushingCtl(event), event.isAltPressed())) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				mController.binaryKey(mCurrentInputConnection,
						event.getKeyCode(),
						event.getScanCode(),event.getDisplayLabel(), event.isShiftPressed(),
						pushingCtl(event), event.isAltPressed());
			}
			return true;
		} else {
			return super.dispatchKeyEventPreIme(event);
		}
	}

	public void setMetaForCommit(boolean alt, boolean ctl) {
		if(Utility.isDebuggingFromCash()) {
			log("#-esf-alt/ctl=" + alt + "/" + ctl);
		}
		// mPushingAlt = alt;
		// mPushingCtl = ctl;
	}

	public synchronized void resetTimer() {

	}
	private static CharSequence mComposingText = null;
	private static int mNewCursorPosition = 0;
	private static CharSequence mCommitText = null;
	private static LinkedList<CommitText> mCommitTextList = new LinkedList<CommitText>();

	public class _MyInputConnection extends BaseInputConnection implements
			MyInputConnection {

		@Override
		public int getCursorCapsMode(int reqModes) {
			if(Utility.isDebuggingFromCash()) {
				log("getCursorCapsMode" + reqModes);
			}
			return super.getCursorCapsMode(reqModes);
		}

		@Override
		public boolean beginBatchEdit() {
			if(Utility.isDebuggingFromCash()) {
				log("beginBatchEditn");
			}
			return super.beginBatchEdit();
		}

		@Override
		public boolean endBatchEdit() {
			if(Utility.isDebuggingFromCash()) {
				log("endBatchEdit");
			}
			return super.endBatchEdit();
		}

		public CharSequence getComposingText() {
			return mComposingText;
		}

		public CharSequence getCommitText() {
			return mCommitText;
		}

		public CommitText popFirst() {
			if (0 < mCommitTextList.size()) {
				return mCommitTextList.removeFirst();
			} else {
				return null;
			}
		}

		public _MyInputConnection(View targetView, boolean fullEditor) {
			super(targetView, fullEditor);
			if(Utility.isDebuggingFromCash()) {
				log("new MyInputConnection");
			}
		}

		@Override
		protected void finalize() throws Throwable {
			if(Utility.isDebuggingFromCash()) {
				log("finalize()");
			}
			super.finalize();
		}

		@Override
		public boolean finishComposingText() {
			if(Utility.isDebuggingFromCash()) {
				log("finishComposingText");
			}
			// for asus fskaren
			{
				if (mCommitText != null && mComposingText.length() != 0) {
					addCommitText(new CommitText(mComposingText, mNewCursorPosition));
					mComposingText = "";
				}
			}
			return super.finishComposingText();
		}

		private SpannableStringBuilder mBuilder = null;

		@Override
		public Editable getEditable() {
			log("getEditable");
			if (mBuilder == null) {
				mBuilder = new SpannableStringBuilder();
			}
			return mBuilder;
		}

		@Override
		public boolean setComposingText(CharSequence text, int newCursorPosition) {
			if(Utility.isDebuggingFromCash()) {
				log("setComposingText=" + text + "," + newCursorPosition);
			}
			resetTimer();
			mComposingText = text;
			mNewCursorPosition = newCursorPosition;
			return super.setComposingText(text, newCursorPosition);
		}

		@Override
		public boolean setSelection(int start, int end) {
			if(Utility.isDebuggingFromCash()) {
				log("setSelection s=" + start + ",e=" + end);
			}
			return super.setSelection(start, end);
		}

		@Override
		public boolean clearMetaKeyStates(int states) {
			if(Utility.isDebuggingFromCash()) {
				log("clearMEtaKetStates s=" + states);
			}
			return super.clearMetaKeyStates(states);
		}

		@Override
		public boolean commitCompletion(CompletionInfo text) {
			if(Utility.isDebuggingFromCash()) {
				log("commitCompletion=" + text);
			}
			try {
				mCommitText = text.getText();
				addCommitText(new CommitText(text.getText(), text.getPosition()));
				return super.commitCompletion(text);
			} finally {
				mComposingText = "";
				getEditable().clear();
			}
		}

		@Override
		public boolean commitText(CharSequence text, int newCursorPosition) {
			if(Utility.isDebuggingFromCash()) {
				log("commitText=" + text + "," + newCursorPosition);
				log("--1--=" + Selection.getSelectionStart(text));
				log("--2--=" + Selection.getSelectionEnd(text));
				log("--3--a/c=" + pushingAltForCommitText() + "/"
						+ pushingCtlForCommitText());
			}

			resetTimer();
			mController.decorateKey(mCurrentInputConnection, text,
					newCursorPosition, false, pushingCtlForCommitText(),
					pushingAltForCommitText());
			mCommitText = text;
			try {
				return true;
			} finally {
				mComposingText = "";
				getEditable().clear();
			}

		}

		@Override
		public CharSequence getTextAfterCursor(int length, int flags) {
			try {
				CharSequence a = super.getTextAfterCursor(length, flags);
				if(Utility.isDebuggingFromCash()) {
					log("getTextAfterCursor=" + a.toString() + "," + length + ","
							+ flags);
				}
				return a;
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}

		@Override
		public CharSequence getTextBeforeCursor(int length, int flags) {
			CharSequence a = super.getTextBeforeCursor(length, flags);
			if(Utility.isDebuggingFromCash()) {
				log("getTextBeforeCursor=" + a.toString() + "," + length + ","
						+ flags);
			}
			return a;
		}

		@Override
		public void addCommitText(CommitText text) {
			if(Utility.isDebuggingFromCash()) {
				log("# addlast---" + text);
			}
			resetTimer();
			mCommitTextList.addLast(text);
		}

		@Override
		public void setIMEController(IMEController controller) {
			mController = controller;
		}

		//
		// ======================================================================
		//
		@Override
		public boolean sendKeyEvent(KeyEvent event) {
			if(Utility.isDebuggingFromCash()) {
				log("sendKeyEvent=" + event.toString());
			}
			resetTimer();
			setMetaForCommit(event.isAltPressed(), pushingCtl(event));
			return super.sendKeyEvent(event);
		}

		@Override
		public ExtractedText getExtractedText(ExtractedTextRequest request,
				int flags) {
			if(Utility.isDebuggingFromCash()) {
				log("getExtractedText=" + 
						(request==null?"":request.hintMaxChars + "," + flags));
			}
			return super.getExtractedText(request, flags);
		}

		@Override
		public boolean performEditorAction(int actionCode) {
			if(Utility.isDebuggingFromCash()) {
				log("performEditorAction=" + actionCode);
			}
			return super.performEditorAction(actionCode);
		}

		@Override
		public boolean performContextMenuAction(int id) {
			log("performContextMenuAction=" + id);
			return super.performContextMenuAction(id);
		}

		@Override
		public boolean performPrivateCommand(String action, Bundle data) {
			if(Utility.isDebuggingFromCash()) {
				log("performPrivateCommand=" + action + "," + data.toString());
			}
			return super.performPrivateCommand(action, data);
		}

		@Override
		public boolean reportFullscreenMode(boolean enabled) {
			if(Utility.isDebuggingFromCash()) {
				log("reportFullscreenMode=" + enabled);
			}
			return super.reportFullscreenMode(enabled);
		}

	}

	public static void log(String log) {
		if(Utility.isDebuggingFromCash()) {
			android.util.Log.v("kiyo", ""+log);
		}
	}
}
