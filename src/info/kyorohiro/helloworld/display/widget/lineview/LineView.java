package info.kyorohiro.helloworld.display.widget.lineview;

import java.util.WeakHashMap;

import info.kyorohiro.helloworld.display.simple.SimpleDisplayObjectContainer;
import info.kyorohiro.helloworld.display.simple.SimpleGraphicUtil;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimpleImage;
import info.kyorohiro.helloworld.display.simple.sample.BreakText;
import info.kyorohiro.helloworld.text.KyoroString;
import info.kyorohiro.helloworld.util.SimpleLockInter;
import info.kyorohiro.helloworld.util.arraybuilder.FloatArrayBuilder;

public class LineView extends SimpleDisplayObjectContainer {
	public static FloatArrayBuilder widths = new FloatArrayBuilder();

	private KyoroString[] mCashBuffer = new KyoroString[0];
	private final static int sTestTextColor = SimpleGraphicUtil.parseColor("#AAFFFF00");
	private boolean mIsClearBG = false;
	private int mBiasAboutMoveLine = 0;
	private int mMergine = -1;
	private int mPointID = 0;
	private int mScaleX = 0;
	private int mScaleY = 0;
	private int mScaleTime = 0;
	private int mPositionY = 0;
	private int mPositionX = 0;
	private int mTextSize = 16;
	private float mScale = 1.0f;

	private WeakHashMap<Integer, Point> mPoint = new WeakHashMap<Integer, Point>();

	private SimpleImage mBGImage = null;
	private int mBgColor = SimpleGraphicUtil.parseColor("#FF000022");

	private LineViewBufferSpec mInputtedText = null;
	private boolean mIsTail = true;
	private int mDefaultCashSize = 100;
	private boolean mIsLockScreen = false;

	public void isLockScreen(boolean lock) {
		mIsLockScreen = lock;
	}

	public boolean isLockScreen() {
		return mIsLockScreen;
	}

	// setScale�ｽ�ｽsetTextSize()�ｽﾅ拡�ｽ蝸ｦ�ｽ�ｽﾝ定し�ｽﾄゑｿｽ�ｽ�ｽB
	// �ｽ�ｽﾅどゑｿｽ�ｽ轤ｩ�ｽﾉ難ｿｽ�ｽ黷ｷ�ｽ�ｽH
	protected float getSclaeFromTextSize() {
		return (getTextSize() / getBreakText().getSimpleFont().getFontSize());
	}

	public BreakText getBreakText() {
		if (getLineViewBuffer() == null) {
			return null;
		} else {
			return getLineViewBuffer().getBreakText();
		}
	}

	public synchronized Point getPoint(int num) {
		Point point = new Point(num);
		mPoint.put(mPointID++, point);
		return point;
	}


	public boolean isOver() {
		if (this.isTail()
				&& mInputtedText.getMaxOfStackedElement() <= mInputtedText
						.getNumberOfStockedElement()) {
			return true;
		} else {
			return false;
		}
	}

	private synchronized void addPoint(int num) {
		if (isOver()) {
			for (Point p : mPoint.values()) {
				p.setPoint(p.getPoint()-num);
				if(p.getPoint() < 0){
					p.setPoint(0);
				}
			}
		}
	}


	public LineView(LineViewBufferSpec inputtedText, int textSize) {
		mInputtedText = inputtedText;
		mTextSize = textSize;
	}

	public LineView(LineViewBufferSpec inputtedText, int textSize, int cashSize) {
		mInputtedText = inputtedText;
		mTextSize = textSize;
		mDefaultCashSize = cashSize;
	}

	public void setBgColor(int color) {
		mBgColor = color;
	}

	public int getBgColor() {
		return mBgColor;
	}

	public void setScale(float scale) {
		mScale = scale;
	}

	public synchronized void setScale(float scale, float sScale, int sGetX,
			int linePosX, Point linePosY, int baseX, int baseY) {
		_updateStatus(mInputtedText);
		if (mBiasAboutMoveLine < 6) {
			mBiasAboutMoveLine += 2;
		}
		mScale = scale;
		int pos = (int) ((getHeight() - baseY) / (getShowingTextSize() * 1.2));//
		mScaleX = baseX;
		mScaleY = baseY;
		mScaleTime = 20;
		setPositionY(
				mInputtedText.getNumberOfStockedElement() - linePosY.getPoint()
						- pos - 1, true);
		setPositionX((int) (baseX - (baseX - sGetX) * scale / sScale), true);

	}

	public float getScale() {
		return mScale;
	}

	public void setTextSize(int textSize) {
		mTextSize = textSize;
	}

	public int getTextSize() {
		if (mTextSize <= 0) {
			return 1;
		}
		return mTextSize;
	}

	public int getShowingTextSize() {
		int ret = (int) (mTextSize * mScale);
		if (ret < 0) {
			return 1;
		}
		return ret;
	}

	public synchronized void setLineViewBufferSpec(
			LineViewBufferSpec inputtedText) {
		mInputtedText = (LineViewBufferSpec) inputtedText;
	}

	public synchronized LineViewBufferSpec getLineViewBuffer() {
		return mInputtedText;
	}

	// @Deprecated
	public synchronized int getShowingTextStartPosition() {
		return mDrawingPosition.getStart();
	}

	// @Deprecated
	public synchronized int getShowingTextEndPosition() {
		return mDrawingPosition.getEnd();
	}

	public synchronized void setPositionY(int position) {
		setPositionY(position, false);
	}

	public synchronized void setPositionY(int position, boolean ignoreBias) {
		if(mIsLockScreen){
			return;
		}
		if (mBiasAboutMoveLine <= 0 || ignoreBias) {
			mPositionY = position;
		}
	}

	public synchronized int getPositionY() {
		return mPositionY;
	}

	public void setPositionX(int x) {
		if(mIsLockScreen){
			return;
		}
		setPositionX(x, false);
	}

	public void setPositionX(int x, boolean ignoreBias) {
		if (mBiasAboutMoveLine <= 0 || ignoreBias) {
			mPositionX = x;
		}
	}

	public int getPositionX() {
		return mPositionX;
	}

	public int getBlinkY() {
		return mDrawingPosition.getBlank();
	}

	//
	// show line like tail command 
	public boolean isTail() {
		return mIsTail;
	}

	public void isTail(boolean on) {
		mIsTail = on;
	}

	private DrawingPositionForLineView mDrawingPosition = new DrawingPositionForLineView();

	public void setBGImage(SimpleImage image) {
		mBGImage = image;
	}

	public void setMergine(int mergine) {
		mMergine = mergine;
	}

	public int getMergine() {
		if (mMergine == -1) {
			return mMergine = (getWidth()) / 20;
		} else {
			return mMergine;
		}
	}

	public int getLeftForStartDrawLine() {
		return getMergine() + mPositionX;
	}

	public int getYForStartDrawLine(int cursurCol) {
		int yy = (int) ((int) (getShowingTextSize() * 1.2))
				* (getBlinkY() + cursurCol + 1);
		return yy;
	}

	public int getWidth(int cursorCol, float[] w) {
		return getWidth(cursorCol, w, getShowingTextSize());
	}

	private boolean mCrlfMode = false;
	public boolean isCrlfMode () {
		return mCrlfMode;
	}
	public void isCrlfMode(boolean mode) {
		mCrlfMode = mode;
	}
	public int getWidth(int cursorCol, float[] w, int textSize) {
		LineViewBufferSpec mInputtedText = getLineViewBuffer();
		if (mInputtedText == null || null == mInputtedText.getBreakText()) {
			return -1;
		} else if (cursorCol >= mInputtedText.getNumberOfStockedElement()
				|| cursorCol < 0) {
			return -1;
		}

		KyoroString data = mInputtedText.get(cursorCol);
		if (data == null) {
			return -1;
		}
		
		//todo
		int l = mInputtedText.getBreakText().getTextWidths(data, 0,
				data.lengthWithoutLF(mCrlfMode), w, textSize);
		return l;
	}

	// @Deprecated
	public int getXToPosX(int cursorCol, int xx, int cur) {
		float x = xx;// /getScale();
		x -= getLeftForStartDrawLine();
		float[] ws = widths.getBuffer();
		int l = getWidth(cursorCol, ws);
		float ww = 0;
		for (int i = 0; i < l; i++) {
			ww += ws[i];
			if (ww > x) {
				return i;
			}
		}
		return l;
	}

	public int getYToPosY(int y) {
		int n = (int) (y / (int) ((getShowingTextSize() * 1.2)));
		int yy = n - getBlinkY() - 1;
		return yy + (getShowingTextStartPosition());
	}

	public int getLineYForShowLine(int cursurRow, int cursurCol) {
		int yy = getYForStartDrawLine(cursurCol);
		int yyy = yy + (int) (getShowingTextSize() * 0.2);
		return yyy;
	}

	public KyoroString getKyoroString(int cursorCol) {
		if (!withinKyoroString(cursorCol)){return null;}
		try {
			lock();
			KyoroString data = mInputtedText.get(cursorCol);
			return data;
		} catch (Exception e) {
			return null;
		} finally {
			releaseLock();
		}
	}

	private void showLineDate(SimpleGraphics graphics, KyoroString[] list, int len) {
		if (len > list.length) {
			len = list.length;
		}

		if (mPositionX > 0) {
			mPositionX = 0;
		}

		float zoom = getShowingTextSize()/(float)getBreakText().getSimpleFont().getFontSize();
		float textMax = (getBreakText().getWidth())* zoom+getMergine()*2;
		float viewMax = getWidth();
		float positionMax = 0;
		if(textMax>viewMax){
			positionMax = getMergine()+
					textMax-viewMax;
		}else {
			positionMax = //getMergine()+
					textMax-viewMax
					+(viewMax-textMax)/2;			
		}
		positionMax *= -1;
		if (mPositionX < positionMax) {
			mPositionX = (int) positionMax;
		}
		for (int i = 0; i < len; i++) {
			if (list[i] == null) {
				continue;
			}

			graphics.setColor(list[i].getColor());
			int x = getLeftForStartDrawLine();
			int y = getYForStartDrawLine(i);
			int yy = getLineYForShowLine(0, i);

//			System.out.println("list["+i+"]="+list[i]+","+graphics.getColor()+","+list[i].getColor());
//			graphics.drawText(list[i], x, y);
        	SimpleGraphicUtil.drawString(graphics, list[i], x, y, widths);

			if (list[i].includeLF()) {
				int c = list[i].getColor();
				graphics.setColor(SimpleGraphicUtil.argb(62, SimpleGraphicUtil.colorA(c), SimpleGraphicUtil.colorG(c),
						SimpleGraphicUtil.colorB(c)));
				graphics.setStrokeWidth(1);
				graphics.drawLine(10, yy, graphics.getWidth() - 10, yy);				
			}
		}
	}

	private Thread currentThread = null;
	private int num = 0;

	public synchronized void lock() {
		try {
			while (currentThread != null
					&& currentThread != Thread.currentThread()) {
				wait();
			}
			if (mInputtedText instanceof SimpleLockInter) {
				((SimpleLockInter) mInputtedText).beginLock();
			}
		} catch (InterruptedException e) {
		} finally {
			currentThread = Thread.currentThread();
			num++;
		}
	}

	public synchronized void releaseLock() {
		if (mInputtedText instanceof SimpleLockInter) {
			((SimpleLockInter) mInputtedText).endLock();
		}
		num--;
		if (num == 0 && currentThread == Thread.currentThread()) {
			notifyAll();
			currentThread = null;
		}
	}

	public static final String LABEL_LOCK_SCREEN = "now lockscreen";
	@Override
	public void paint(SimpleGraphics graphics) {
		if(getLineViewBuffer().isLoading()) {
			getStage(this).resetTimer();
		}
		graphics.clipRect(0, 0, getWidth(), getHeight());
		if (mBiasAboutMoveLine > 0) {
			mBiasAboutMoveLine--;
		}
		if(mIsLockScreen){
			graphics.drawText(LABEL_LOCK_SCREEN, 100, 100);
			return;
		}
		graphics.setSimpleFont(getBreakText().getSimpleFont());
		LineViewBufferSpec showingText = mInputtedText;
		KyoroString[] list = null;
		int len = 0;

		// update status
		try {
			lock();
			// update statusr
			_updateStatus(showingText);
			// get buffer
			len = _getBuffer(showingText);
			list = mCashBuffer;
		} finally {
			releaseLock();
		}

		// show buffer
		graphics.setTextSize(getShowingTextSize());// todo mScale

		// draw extra
		{// bg
			drawBG(graphics);
		}
		{// line number
			int s = graphics.getTextSize();
			graphics.setTextSize((int)(s*1.2));
			graphics.setColor(sTestTextColor);
			graphics.drawText("" + getShowingTextStartPosition() + ":"
					+ getShowingTextEndPosition(), 30, s * 4);
			graphics.setTextSize(s);
		}
		{// scale in out animation
			if (mScaleTime > 0) {
				graphics.setColor(SimpleGraphicUtil.argb(mScaleTime, 0xff, 0xff, 0x00));
				graphics.drawCircle(mScaleX, mScaleY, 30);
				mScaleTime -= 3;
			}
		}
		// draw content
		if (list != null) {// bug fix
			showLineDate(graphics, list, len);
		}// */
		// fin
		//if(false) {
			super.paint(graphics);
		//	return;
		//}
		graphics.clipRect(-1, -1, -1, -1);
	}

	private int _getBuffer(LineViewBufferSpec showingText) {
		int start = mDrawingPosition.getStart();
		int end = mDrawingPosition.getEnd();

		int len = 0;
		if (start <= end) {
			len = end - start;
		}
		if (mCashBuffer.length < len) {
			int buffeSize = len;
			if (buffeSize < mDefaultCashSize) {
				buffeSize = mDefaultCashSize;
			}
			mCashBuffer = new KyoroString[buffeSize];
		}
		cashing(showingText, start, end);
		return len;
	}

	private void cashing(LineViewBufferSpec showingText, int start, int end) {
		int len = end -start;
		for(int i =0;i<len&&i+start<showingText.getNumberOfStockedElement();i++) {
//			android.util.Log.v("kiyo","#=#"+i+"/"+showingText.getNumberOfStockedElement());
			mCashBuffer[i] = showingText.get(start+i);
		}
	}

	protected void _updateStatus(LineViewBufferSpec showingText) {
		try {
			lock();
			if (!mIsTail || mPositionY > 1) {
				setPositionY(mPositionY + showingText.getNumOfAdd(), true);
				addPoint(showingText.getNumOfAdd());
			}
			showingText.clearNumOfAdd();
			mDrawingPosition.updateInfo(this, mPositionY, getHeight(), mTextSize, mScale, showingText);
			this.setPositionY(mDrawingPosition.getPosition(), true);

		} finally {
			releaseLock();
		}
	}

	public void isClearBG(boolean on) {
		mIsClearBG = on;
	}

	private void drawBG(SimpleGraphics graphics) {
		if (mIsClearBG) {
			SimpleGraphicUtil.fillRect(graphics, 0, 0, getWidth(), getHeight());
			if (mBGImage != null) {
				graphics.drawImageAsTile(mBGImage, 0, 0, getWidth(), getHeight());
			}
		}
	}

	private boolean withinKyoroString(int cursorCol) {
		if (mInputtedText == null){
			return false;
		}else if(cursorCol >= mInputtedText.getNumberOfStockedElement()){
			return false;
		}else if(cursorCol < 0) {
			return false;
		}
		return true;
	}
}
