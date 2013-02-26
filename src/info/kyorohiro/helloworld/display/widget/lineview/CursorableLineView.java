package info.kyorohiro.helloworld.display.widget.lineview;

import info.kyorohiro.helloworld.display.simple.MessageDispatcher;
import info.kyorohiro.helloworld.display.simple.SimpleGraphicUtil;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimpleMotionEvent;
import info.kyorohiro.helloworld.text.KyoroString;

public class CursorableLineView extends LineView {

	public final static String MODE_SELECT = "<S>ELECT";
	public final static String MODE_VIEW   = "<V>IEW";
	public final static String MODE_EDIT   = "<E>DIT";
	public final static int __CURSOR__COLOR = SimpleGraphicUtil.parseColor("#88FFAA44");
	public final static int __CURSOR__COLOR2 = SimpleGraphicUtil.parseColor("#FFBB8811");


	private MyCursor mLeft = new MyCursor(this);
	private MyCursor mRight = new MyCursor(this);
	private CharSequence mMode = MODE_VIEW;

	private boolean mIsFocus = false;
	public boolean isFocus(){
//		android.util.Log.v("kiyo","adsdf[1]"+mIsFocus);
		return mIsFocus;
	}
	public void isFocus(boolean isFocus){
//		android.util.Log.v("kiyo","adsdf"+isFocus);
		mIsFocus = isFocus;
	}
	public MyCursor getLeft() {
		return mLeft;
	}

	public MyCursor getRight() {
		return mRight;
	}

	public String copy() {
		try {
			lock();
			if (mLeft.isVisible() && mRight.isVisible()) {
				MyCursor b = mLeft;
				MyCursor e = mRight;
				if (b.getCursorCol() > e.getCursorCol()
						|| (b.getCursorCol() == e.getCursorCol() && b
						.getCursorRow() > e.getCursorRow())) {
					b = mRight;
					e = mLeft;
				}
				if (b.getCursorCol() < 0) {
					b.setCursorCol(0);
				}
				if (e.getCursorCol() < 0) {
					e.setCursorCol(0);
				}
				if(e.getCursorRow()<0){
					e.setCursorRow(0);
				}
				if(b.getCursorRow()<0){
					b.setCursorRow(0);
				}

				StringBuilder bb = new StringBuilder();
				LineViewBufferSpec buffer = getLineViewBuffer();

				try {
					if (b.getCursorCol() == e.getCursorCol()) {
						CharSequence c = buffer.get(b.getCursorCol());
						if (c == null) {
							c = "";
						}
						bb.append(c.subSequence(b.getCursorRow(), e.getCursorRow()));
					} else {
						CharSequence c = buffer.get(b.getCursorCol());
						bb.append("" + c.subSequence(b.getCursorRow(), c.length()));
						for (int i = b.getCursorCol() + 1; i < e.getCursorCol(); i++) {
							bb.append(buffer.get(i));
						}
						CharSequence cc = buffer.get(e.getCursorCol());
						bb.append("" + cc.subSequence(0, e.getCursorRow()));
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				return bb.toString();
			} else {
				return "";
			}
		}finally {
			releaseLock();
		}
	}

	public CharSequence getMode() {
		return mMode;
	}

	public void setMode(String mode) {
		mMode = mode;
		if (mode.startsWith(MODE_SELECT)){
			int col = getShowingTextStartPosition();
			mLeft.setCursorCol(col);
			mLeft.setCursorRow(0);
			mRight.setCursorCol(col);
			mRight.setCursorRow(3);
			// setScale(1.0f);
		}
	}

	private boolean mIsEditClickAction = false;
	private int mIsEditClickActionX = 0;
	private int mIsEditClickActionY = 0;
	@Override
	public boolean onTouchTest(int x, int y, int action) {
		if (super.onTouchTest(x, y, action)) {
			return true;
		} else {
			if (!isFocus()){
				return false;
			}
			if (mMode.toString().startsWith(MODE_EDIT)||mMode.toString().startsWith(MODE_VIEW)) {
				if(action == SimpleMotionEvent.ACTION_DOWN) {
					mIsEditClickActionX = x;
					mIsEditClickActionY = y; 
					mIsEditClickAction = true;
				}
				else if (action == SimpleMotionEvent.ACTION_MOVE) {
					if(mIsEditClickAction){
						int ts = 30;
						if(Math.abs(x-mIsEditClickActionX)>ts|Math.abs(y-mIsEditClickActionY)>ts){
							mIsEditClickAction = false;
						}
					}
				}
				else if (action == SimpleMotionEvent.ACTION_UP) {
					if(mIsEditClickAction==true){
						try{
//							android.util.Log.v("xxx","--A1--");
							lock();
							mLeft.setCursorCol(getYToPosY(y));
							mLeft.setCursorRow(getXToPosX(mLeft.getCursorCol(), x, mLeft.getCursorRow()));
							KyoroString s = this.getKyoroString(mLeft.getCursorCol());
							MessageDispatcher.getInstance().send(s);
//							android.util.Log.v("xxx","--A2--");
						}finally{
							releaseLock();
						}
					}
				}
			}
			if(mMode.toString().startsWith(MODE_SELECT)){
				if(action == SimpleMotionEvent.ACTION_DOWN&&
						0<x&&x<getWidth()&&0<y&&y<getHeight()) {
					prevX = x;
					prevY = y;
				}
				else if(action == SimpleMotionEvent.ACTION_MOVE){
					if(20>Math.abs(prevX-x)+Math.abs(prevY-y)){
					}else {
						time = 0;
						prevX = -1;
						prevY = -1;	
					}
				}
				else if(action == SimpleMotionEvent.ACTION_UP){
					prevX = -1;
					prevY = -1;					
				}
			}
			return false;
		}
	}
	//todo refactaring following field
	private int prevX = -1;
	private int prevY = -1;
	private int time = 0;

	public CursorableLineView(LineViewBufferSpec inputtedText, int textSize, int cashSize) {
		super(inputtedText, textSize, cashSize);
		addChild(mRight);
		addChild(mLeft);
		mRight.setRect(40, 120);
		mLeft.setRect(40, 120);
		mLeft.setPoint(100, 100);
	}

	@Override
	public void paint(SimpleGraphics graphics) {
		if(isFocus()){
			getLeft().isVisible(true);
			if(mMode.toString().startsWith(MODE_SELECT)) {
				getRight().isVisible(true);
			} else {
				getRight().isVisible(false);				
			}
		} else {
			getLeft().isVisible(false);
			getRight().isVisible(false);			
		}

		super.paint(graphics);
		if (null == getBreakText()) {
			return;
		}
		mRight.updateCursor();
		mLeft.updateCursor();
		graphics.setColor(__CURSOR__COLOR);
		graphics.setTextSize((int)(getTextSize()*1.2));
		graphics.drawText(mMode, 20, this.getHeight() - 50);
		drawBGForSelect(graphics);
		//todo refactaring
		if(mMode.toString().startsWith(MODE_SELECT)){
			if(prevX !=-1&&prevY!=-1){
				time++;
				if(time >= 7){
					setMode(MODE_SELECT);
//					android.util.Log.v("xxx","--B1--");
					mLeft.setCursorCol(getYToPosY(prevY));
					mLeft.setCursorRow(getXToPosX(mLeft.getCursorCol(), prevX, mLeft.getCursorRow()));
					mRight.setCursorCol(getYToPosY(prevY));
					mRight.setCursorRow(getXToPosX(mRight.getCursorCol(), prevX+1, mRight.getCursorRow()));
//					android.util.Log.v("xxx","--B2--");
					prevX=-1;
					prevY=-1;
					time=0;
				}
			}
		}
	}

	private void drawBGForSelect(SimpleGraphics graphics) {
		if (mLeft.isVisible() && mRight.isVisible()) {
			MyCursor b = mLeft;
			MyCursor e = mRight;
			int textSize = getShowingTextSize();
			if (b.getY() > e.getY() || (b.getY() == e.getY() && b.getX() > e.getX())) {
				b = mRight;
				e = mLeft;
			}

			graphics.setColor(__CURSOR__COLOR);
			graphics.setStrokeWidth(10);
			if (b.getY() != e.getY()) {
				graphics.drawLine(b.getX(), b.getY(), (int) (getWidth() * 0.95), b.getY());
				graphics.drawLine(this.getLeftForStartDrawLine(), e.getY(), e.getX(), e.getY());
				graphics.startPath();
				graphics.moveTo((int) (getWidth() * 0.05), b.getY() + textSize);// +getTextSize());
				graphics.lineTo((int) (getWidth() * 0.95), b.getY());
				graphics.lineTo((int) (getWidth() * 0.95), e.getY() - textSize);
				graphics.lineTo((int) (getWidth() * 0.05), e.getY());// +getTextSize());
				graphics.moveTo((int) (getWidth() * 0.05), b.getY());// +getTextSize());
				graphics.endPath();
			} else {
				graphics.drawLine(b.getX(), b.getY(), e.getX(), e.getY());
			}
		}
	}


	public void front() {
		setCursorFront(getLeft(),getLeft().getCursorRow()+1, getLeft().getCursorCol());
		if(getLeft().getCursorCol()+1 >getShowingTextEndPosition()) {
			setPositionY(getPositionY()-1);
		}
	}
	public void back() {
		setCursorBack(getLeft(),getLeft().getCursorRow()-1, getLeft().getCursorCol());
		if(getLeft().getCursorCol()-1 <getShowingTextStartPosition()) {
			setPositionY(getPositionY()+1);
		}
	}
	public void next() {
		setCursorAndCRLF(getLeft(),getLeft().getCursorRow(), getLeft().getCursorCol()+1);
		// todo folllowing code refactring target
		if(getLeft().getCursorCol()+1 >getShowingTextEndPosition()) {
			setPositionY(getPositionY()-1);
		}
	}
	public void prev() {
		setCursorAndCRLF(getLeft(),getLeft().getCursorRow(), getLeft().getCursorCol()-1);
		// todo folllowing code refactring target
		if(getLeft().getCursorCol()-1 <getShowingTextStartPosition()) {
			setPositionY(getPositionY()+1);
		}
	}

	
	public void setCursorBack(MyCursor cursor, int row, int col) {
		int _rowTmp = row;
		int _colTmp = col;
		LineViewBufferSpec spec= this.getLineViewBuffer();
		if (_rowTmp < 0) {
			// �ｽﾚ難ｿｽ�ｽ�ｽ�ｽ�ｽB
			if (_colTmp > 0) {
				_colTmp -= 1;
				KyoroString cc = spec.get(_colTmp);
				_rowTmp = cc.lengthWithoutLF(isCrlfMode());
			} else {
				_rowTmp = 0;
			}
		} 
		setCursorAndCRLF(cursor, _rowTmp, _colTmp);
	}
	public void setCursorFront(MyCursor cursor, int row, int col) {
		int _rowTmp = row;
		int _colTmp = col;
		LineViewBufferSpec spec= this.getLineViewBuffer();
		KyoroString c = spec.get(_colTmp);
		if (_rowTmp > c.lengthWithoutLF(isCrlfMode())) {
			// �ｽﾚ難ｿｽ�ｽ�ｽ�ｽ�ｽB
			if (_colTmp < spec.getNumberOfStockedElement() - 1) {
				_rowTmp = 0;
				_colTmp += 1;
			} else {
				_rowTmp = c.lengthWithoutLF(isCrlfMode());
			}
		}
		setCursorAndCRLF(cursor, _rowTmp, _colTmp);
	}
	public void setCursorAndCRLF(MyCursor cursor, int row, int col) {
		// this method Should belong to LIneView
		int _rowTmp = row;
		int _colTmp = col;
		LineViewBufferSpec spec= this.getLineViewBuffer();
		if (_colTmp < 0) {
			_colTmp = 0;
		} else if (_colTmp >= spec.getNumberOfStockedElement()) {
			_colTmp = spec.getNumberOfStockedElement();
		}

		KyoroString c = spec.get(_colTmp);
		if (_rowTmp < 0) {
			_rowTmp = 0;
		} else if (_rowTmp > c.lengthWithoutLF(isCrlfMode())) {
			_rowTmp = c.lengthWithoutLF(isCrlfMode());
		}
		cursor.setCursorCol(_colTmp);
		cursor.setCursorRow(_rowTmp);
	}
}

