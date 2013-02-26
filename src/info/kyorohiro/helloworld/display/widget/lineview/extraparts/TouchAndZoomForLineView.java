package info.kyorohiro.helloworld.display.widget.lineview.extraparts;


import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleGraphicUtil;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimplePoint;
import info.kyorohiro.helloworld.display.simple.SimpleStage;
import info.kyorohiro.helloworld.display.widget.lineview.LineView;
import info.kyorohiro.helloworld.display.widget.lineview.Point;
import info.kyorohiro.helloworld.text.KyoroString;
import info.kyorohiro.helloworld.util.SimpleLockInter;

public class TouchAndZoomForLineView extends SimpleDisplayObject {
	private LineView mLineViewer = null;
	private boolean mStartZoom = false;
	private float mStartScale = 0;
	private float mNextScale = 0;
	private int mStartCenterY = 0;
	private int mStartCenterX = 0;
	private Point mStartPosY = null;
	private int mStartPosX = 0;
	private int mStartGetX = 0;
	private KyoroString mStartLine = null;
	private int mStartLineBaseColor = 0;
	private float mStartLength = 0;
	private float mCurrentScale = 0;

	public TouchAndZoomForLineView(LineView viewer) {
		mLineViewer = viewer;
	}

	private int getCenterX() {
		SimpleStage stage = SimpleDisplayObject.getStage(this);
		SimplePoint[] p = stage.getMultiTouchEvent();
		//
		int[] xy = new int[2];
		this.getGlobalXY(xy);
		//
		int ret = (p[0].getX()-xy[0] + p[1].getX()-xy[0]) / 2;
		return ret;
	}

	private int getCenterY() {
		SimpleStage stage = SimpleDisplayObject.getStage(this);
		SimplePoint[] p = stage.getMultiTouchEvent();
		//
		int[] xy = new int[2];
		this.getGlobalXY(xy);
		//
		int ret = (p[0].getY()-xy[1] + p[1].getY()-xy[1]) / 2;
		return ret;
	}

	private int getLength() {
		SimpleStage stage = SimpleDisplayObject.getStage(this);
		SimplePoint[] p = stage.getMultiTouchEvent();
		int xx = p[0].getX() - p[1].getX();
		int yy = p[0].getY() - p[1].getY();
		return xx * xx + yy * yy;
	}

	private boolean doubleTouched() {
		SimpleStage stage = SimpleDisplayObject.getStage(this);
		SimplePoint[] p = stage.getMultiTouchEvent();
		if (p[0].isVisible() && p[1].isVisible()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public synchronized boolean onTouchTest(int x, int y, int action) {
		boolean focusIn = false;
		if(0<x&&x<mLineViewer.getWidth()&&0<y&&y<mLineViewer.getHeight()){
			focusIn = true;
		} else {
			focusIn = false;
		}
		SimpleStage stage = SimpleDisplayObject.getStage(this);

		if (stage != null && stage.isSupportMultiTouch()) {
			if (!doubleTouched()) {
				mStartLength = 0;
				if (mStartZoom) {
					mStartZoom = false;
					if (mStartLine != null) {
						mStartLine.setColor(mStartLineBaseColor);
					}
					// kiyo checking
					return false;
				} else {
					return false;
				}
			}
			//
			// todo focusIn
			//
			if (focusIn &&mStartZoom == false) {
				try {
					if (mLineViewer instanceof SimpleLockInter) {
						((SimpleLockInter) mLineViewer).beginLock();
					}
					mStartZoom = true;
					mStartScale = mNextScale = mLineViewer.getScale();
					mStartLength = getLength();
					mStartCenterX = getCenterX();
					mStartCenterY = getCenterY();
					mStartPosY = mLineViewer.getPoint(mLineViewer.getYToPosY(mStartCenterY));
					mStartLine = mLineViewer.getKyoroString(mStartPosY.getPoint());
					mStartPosX = mLineViewer.getXToPosX(mStartPosY.getPoint(), mStartCenterX, 0);
					mStartGetX = mLineViewer.getPositionX();
					if (mStartLine != null) {
						mStartLineBaseColor = mStartLine.getColor();
						mStartLine.setColor(SimpleGraphicUtil.YELLOW);
					}
				}finally {
					if (mLineViewer instanceof SimpleLockInter) {
						((SimpleLockInter) mLineViewer).endLock();
					}					
				}
			}
			
			//
			// mStartZoom
			//
			if(!mStartZoom){
				return false;
			}

			int currentLength = getLength();// xx*xx + yy*yy;
			if (mStartLength != 0) {
				float nextScale = mNextScale += (currentLength-mStartLength)/(400 * 400);
				if (nextScale < 1.0) {
					nextScale = 1.0f;
				} else if (nextScale > 6) {
					nextScale = 6.0f;
				}
				mCurrentScale = nextScale;
			}
			mStartLength = currentLength;
			//
//			android.util.Log.v("kiyo","zoom true");
			//return true;
			return false;
		}
		return false;
	}

	@Override
	public synchronized void paint(SimpleGraphics graphics) {
		if (mStartZoom) {
			mLineViewer.setScale(mCurrentScale, mStartScale, mStartGetX,mStartPosX, mStartPosY, mStartCenterX, mStartCenterY);
		}
	}

}