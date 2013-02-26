package info.kyorohiro.helloworld.display.widget.lineview;

import info.kyorohiro.helloworld.display.simple.CrossCuttingProperty;
import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleGraphicUtil;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimpleMotionEvent;
import info.kyorohiro.helloworld.text.KyoroString;
import java.lang.ref.WeakReference;

public class MyCursor extends SimpleDisplayObject {
	private int cursorRow = 0;
	private Point cursorCol;
	private int mX = 0;
	private int mY = 0;
	private int px = 0;
	private int py = 0;
	private boolean focus = false;
	private WeakReference<CursorableLineView> mParent;
	private CharSequence mMessage = "";
	public static final String TAG_CURSOR_MESSAGE_COLOR = "TAG_CURSOR_MESSAGE_COLOR";

	public MyCursor(CursorableLineView lineview) {
		mParent = new WeakReference<CursorableLineView>(lineview);
		cursorCol = lineview.getPoint(0);
	}


	public void setMessage(CharSequence message) {
		mMessage = message;
	}

	public void setCursorCol(int col) {
		if(col<0){col = 0;}//todo
		cursorCol.setPoint(col);
	}

	public void setCursorCol(Point col) {
		cursorCol = col;
	}

	public void setCursorRow(int row) {
		cursorRow = row;
	}

	public int getCursorRow() {
		return cursorRow;
	}

	public int getCursorCol() {
		return cursorCol.getPoint();
	}

	private void lock() {
		mParent.get().lock();
	}

	private void releaseLock(){
		mParent.get().releaseLock();	
	}

	@Override
	public void paint(SimpleGraphics graphics) {
		if(!updatable()){
			return;
		}

		if (!focus) {
			graphics.setColor(CursorableLineView.__CURSOR__COLOR);
		} else {
			graphics.setColor(SimpleGraphicUtil.parseColor("#AA00FFFF"));
			drawCursor(graphics, mX - px, mY - py);
			graphics.setColor(SimpleGraphicUtil.parseColor("#AA00FFFF"));
		}
		drawCursor(graphics, 0, 0);

		if(mMessage != null&&mMessage.length()!=0){
			{
				graphics.saveSetting();
				graphics.clipRect(0, -2*mParent.get().getShowingTextSize(), ((SimpleDisplayObject)getParent()).getWidth(), getHeight());
				//				graphics.clipRect(left, top, right, bottom);
				CrossCuttingProperty cp = CrossCuttingProperty.getInstance();
				int c = cp.getProperty(TAG_CURSOR_MESSAGE_COLOR, SimpleGraphicUtil.parseColor("#FF000000"));
				graphics.setColor(c);
				graphics.setTextSize(mParent.get().getTextSize());
				//			graphics.drawText(mMessage, 0, 0);//-1*mParent.get().getShowingTextSize());
				int[] xy = new int[2];
				getGlobalXY(xy);
				if(xy[1]-2*mParent.get().getShowingTextSize() < 0) {
					graphics.drawText(mMessage, 0, 1*mParent.get().getShowingTextSize());					
				} else {
					graphics.drawText(mMessage, 0,-1*mParent.get().getShowingTextSize());
				}
				graphics.restoreSetting();
			}
		}
		graphics.setTextSize(26);
		graphics.drawText("x=" + cursorRow + ",y=" + cursorCol.getPoint(), 10, 100);

		graphics.drawLine(0, 0, 0, -20);
		graphics.setColor(CursorableLineView.__CURSOR__COLOR2);
		graphics.drawLine(0, 0, 0, -1*6);
	}

	private void drawCursor(SimpleGraphics graphics, int x, int y) {
		if(!updatable()){
			return;
		}
		graphics.startPath();
		graphics.moveTo(x, y);
		graphics.lineTo(x + getWidth() / 2, y + getHeight() * 2 / 3);
		graphics.lineTo(x + getWidth() / 2, y + getHeight());
		graphics.lineTo(x + -getWidth() / 2, y + getHeight());
		graphics.lineTo(x + -getWidth() / 2, y + getHeight() * 2 / 3);
		graphics.lineTo(x + 0, y + 0);
		graphics.endPath();
	}

	private boolean updatable() {
		CursorableLineView view = mParent.get();
		int start = view.getShowingTextStartPosition();
		int end = view.getShowingTextEndPosition();
		int cursorY = getCursorCol();
		if(cursorY+10<start||end<cursorY-10) {
		//	android.util.Log.v("kiyo","-- "+cursorY+"<"+start+"||"+end+"<"+cursorY+" --");
			return false;
		} else {
			return true;
		}
		
	}
	@Override
	public boolean onTouchTest(int x, int y, int action) {
		// following code is yatuke sigoto
		if(!mParent.get().isFocus()){
			return false;
		}
		if(!updatable()){
			return false;
		}
		mX = x;
		mY = y;
		if (action == SimpleMotionEvent.ACTION_DOWN) {
			if (-getWidth() < x && x < getWidth() && 0 < y
					&& y < getHeight()) {
				focus = true;
				px = x;
				py = y;
			} else {
				focus = false;
			}
		}

		if (action == SimpleMotionEvent.ACTION_UP) {
			if (focus == true) {
				focus = false;
//				return false;
				return true;
			}
		} else if (action == SimpleMotionEvent.ACTION_MOVE) {
			if (focus == true) {
				try {
					lock();
					cursorCol.setPoint(getYToPosY(y - py + getY()));
					cursorRow = getXToPosX(cursorCol.getPoint(), x - px + getX(), cursorRow);
				} 
				catch(Exception e){
					e.printStackTrace();
				}finally {
					releaseLock();
				}
			}
		}
		return focus;
	}

	private int getYToPosY(int y) {
		LineView l = mParent.get();
		if(l != null){
			return l.getYToPosY(y);
		} else {
			return 0;
		}
	}

	private int getXToPosX(int y, int xx, int cur) {
		LineView l = mParent.get();
		if(l != null){
			return l.getXToPosX(y, xx, cur);
		} else {
			return 0;
		}
	}

	@Override
	public boolean includeParentRect() {
		return false;
	}
	public void updateCursor() {
		if(!mParent.get().isFocus()){
			return;
		}
		if(!updatable()){
			return;
		}

//		android.util.Log.v("kiyo","cursor:c="+getCursorCol()+",r="+getCursorRow());
		int y = 0;
		float x = 0.0f;
		int l = 0;
		if (this.getCursorCol() < mParent.get().getShowingTextStartPosition()
				|| this.getCursorCol() > mParent.get().getShowingTextEndPosition()) {
			// TextViewer�ｽﾆのキ�ｽ�ｽ�ｽb�ｽV�ｽ�ｽ�ｽﾌ趣ｿｽ闕�ｿｽ�ｽ�ｽﾅ、�ｽ�ｽﾊゑｿｽ�ｽ_�ｽﾅゑｿｽ�ｽﾄゑｿｽ�ｽﾜゑｿｽ�ｽB
			// todo �ｽ�ｽﾅ対搾ｿｽ�ｽ�ｽl�ｽ�ｽ�ｽ�ｽB
			// LineView�ｽ�ｽ�ｽ�ｽ�ｽo�ｽb�ｽt�ｽ@�ｽﾌ抵ｿｽ�ｽg�ｽﾉつゑｿｽ�ｽﾄ知�ｽ�ｽﾈゑｿｽ�ｽﾄゑｿｽ�ｽﾇゑｿｽ�ｽ謔､�ｽﾉゑｿｽ�ｽ�ｽB�ｽA
		} else {
			KyoroString d = null;
			try {
				lock();
				int yy = this.getCursorCol();
				if(mParent.get().isOver()){
					yy-=mParent.get().getLineViewBuffer().getNumOfAdd();
				}
				d = mParent.get().getKyoroString(yy);
			} finally {
				releaseLock();
			}
	//		android.util.Log.v("kiyo","cursor:d="+d+","+d.length());
	//		android.util.Log.v("kiyo","cursor:d_l="+d.length());

			try {
				if (d != null) {
					l=getCursorRow();
					if(!d.use()){
					//	android.util.Log.v("kiyo","cursor:l="+l+","+mParent.get().getShowingTextSize()+","+d);
						d.setCashWidths(mParent.get().getBreakText().getSimpleFont(),
								(int)mParent.get().getBreakText().getSimpleFont().getFontSize());	
//								mParent.get().getShowingTextSize());
					}
					//android.util.Log.v("kiyo","cursor:--2--"+l);
					float[] ws = d.getCashWidths();

					//android.util.Log.v("kiyo","cursor:--3--"+x);
					for (int i = 0; i < l; i++) {
						x += ws[i];
					}
					x*=d.getCashZoomSize(mParent.get().getShowingTextSize());
					//android.util.Log.v("kiyo","cursor:--4--"+x);
					//android.util.Log.v("kiyo","cursor:x="+x);
				}
			} catch(Throwable t){
				// todo refactaring BreakText�ｽﾍ托ｿｽ�ｽﾒゑｿｽ�ｽ�ｽ`�ｽ�ｽ�ｽ�ｽﾌで、�ｽO�ｽﾌゑｿｽ�ｽ�ｽ
			}
		}

		try {
			lock();
			y = mParent.get().getYForStartDrawLine(this.getCursorCol()-mParent.get().getShowingTextStartPosition());
			this.setPoint((int) x + mParent.get().getLeftForStartDrawLine(), y);
		} finally {
			releaseLock();
		}
	}
}