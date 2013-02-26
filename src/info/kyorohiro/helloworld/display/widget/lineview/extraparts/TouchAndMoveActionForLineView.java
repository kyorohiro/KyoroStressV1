package info.kyorohiro.helloworld.display.widget.lineview.extraparts;

import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimpleMotionEvent;
import info.kyorohiro.helloworld.display.simple.SimplePoint;
import info.kyorohiro.helloworld.display.simple.SimpleStage;
import info.kyorohiro.helloworld.display.widget.lineview.LineView;

// this class is refactraing target.
public class TouchAndMoveActionForLineView extends SimpleDisplayObject {

	private int mPrevX = 0;
	private int mPrevY = 0;
	private int mMovingX = 0;
	private double mMovingY = 0;
	private int mHeavyX = 0;
	private int mHeavyY = 0;
	private int mPowerX = 0;
	private int mPowerY = 0;
	private int mPower_prevX = 0;
	private int mPower_prevY = 0;
	private long mPower_time = 0;
	private LineView mViewer = null;
	private TappCheck mTapCheck = new TappCheck();

	public TouchAndMoveActionForLineView(LineView viewer) {
		mViewer = viewer;
	}

	@Override
	public void paint(SimpleGraphics graphics) {
		if (mHeavyX < -5 || mHeavyX > 5) {
			mHeavyX /= 1.2;
			// todo refactaring
			mViewer.setPositionX(mViewer.getPositionX() + mHeavyX/* /textSize */);
		}
		if (mHeavyY < -5 || mHeavyY > 5) {
			mHeavyY /= 1.2;
			int textSize = (int) (mViewer.getTextSize() * mViewer.getScale());// todo																				// 2.5f

			// todo refactaring
			mViewer.setPositionY(mViewer.getPositionY()+mHeavyY/textSize);
		}
	}

	private double mMoveYY =0;
	private boolean mIsTouched = false;
	@Override
	public boolean onTouchTest(int x, int y, int action) {
		boolean focusIn = false;
		boolean doubleTouched = doubleTouched();
		mTapCheck.onTouchTest(x, y, action);
		boolean tapped = mTapCheck.tapped();
		if(0<x&&x<mViewer.getWidth()&&0<y&&y<mViewer.getHeight()){
			focusIn = true;
		} else {
			focusIn = false;
		}
		if(!focusIn){
			if(mIsTouched!=true){
				action = SimpleMotionEvent.ACTION_UP;
			}
		}
		if(doubleTouched){
//			android.util.Log.v("kiyo","touched");
			mIsTouched = false;
			action = SimpleMotionEvent.ACTION_UP;
		}
		if(tapped){
			clearPower();
		}
		if (action == SimpleMotionEvent.ACTION_MOVE&& mIsTouched ==true) {
//			android.util.Log.v("kiyo","move");
			mHeavyX = 0;
			mHeavyY = 0;
			mMovingY += y - mPrevY;
			mMoveYY  += y - mPrevY;
			mMovingX += x - mPrevX;
			int todoY = y - mPrevY;
			mPrevY = y;
			mPrevX = x;

			if(!tapped) {
				updateMovePower(x, y);
			} 
			int textSize = (int) (mViewer.getTextSize() * mViewer.getScale());// todo
																				// 2.5f
			boolean ret = false;
			{
				double tS = textSize*1.2;
				if((mMovingY/tS)>=1 || (mMovingY/tS)<=-1) {
					mViewer.setPositionY(mViewer.getPositionY()+ (int)(mMovingY/tS));
					mMovingY = mMovingY%(int)tS;
				} else {
					mMoveYY =(mMoveYY+todoY)%tS;
				}
			}

			if (mMovingX < textSize || textSize < mMovingX) {
				int notMuchValue = mMovingX % textSize;
				mViewer.setPositionX(mViewer.getPositionX()
						+ (mMovingX - notMuchValue));
				mMovingX = notMuchValue;
				ret = true;
			}
			return ret;
		} else if (action == SimpleMotionEvent.ACTION_DOWN) {
			clearPower();
//			android.util.Log.v("kiyo","down");
			mIsTouched = true;
			mHeavyX = 0;
			mHeavyY = 0;
			mMoveYY = mMovingY; 
			mMovingX = 0;
			mPrevY = y;
			mPrevX = x;
//			mPrevY = -999;
//			mPrevX = -999;
		} else if (action == SimpleMotionEvent.ACTION_UP) {
//			android.util.Log.v("kiyo","up");
			if(mIsTouched&&!tapped&&mPower_time!=-1){
				mHeavyX = mPowerX * 8;
				mHeavyY = mPowerY * 8;
			} else {
				mHeavyX = 0;
				mHeavyY = 0;
			}
			mIsTouched = false;
			mPrevY = -999;
			mPrevX = -999;
			mMovingY = 0;
			mMovingX = 0;
		}

		return false;
	}

	public void clearPower() {
		mPower_time = -1;
		mPower_prevY = 0;
		mPower_prevX = 0;
	}
	public void updateMovePower(int x, int y) {
		if (mPower_time < 0) {
			mPower_time = System.currentTimeMillis();
			mPower_prevY = y;
			mPower_prevX = x;
		} else {
			mPowerY = y - mPower_prevY;
			mPower_prevY = y;
			mPowerX = x - mPower_prevX;
			mPower_prevX = x;
		}
	}

	private boolean doubleTouched() {
		SimpleStage stage = SimpleDisplayObject.getStage(this);
		if (stage == null || !stage.isSupportMultiTouch()) {
			return false;
		}
		SimplePoint[] p = stage.getMultiTouchEvent();
		if (p[0].isVisible() && p[1].isVisible()) {
			return true;
		} else {
			return false;
		}
	}

	private static class TappCheck {
		private boolean mIsTap = false;		
		private int mX = 0;
		private int mY = 0;
		private int mLength = 100;
		public boolean onTouchTest(int x, int y, int action) {
			switch(action) {
			case SimpleMotionEvent.ACTION_POINTER_DOWN:
					mIsTap = true;
					mX = x;
					mY = y;
				break;
			case SimpleMotionEvent.ACTION_MOVE:
				if(Math.abs(mX-x)>mLength||Math.abs(mY-y)>mLength){
					mIsTap = false;
				}
				break;
			case SimpleMotionEvent.ACTION_POINTER_UP:
//				mIsTap = false;
				break;
			default:
				if(Math.abs(mX-x)>mLength||Math.abs(mY-y)>mLength){
					mIsTap = false;
				}
			}

			return false;
		}
		public boolean tapped() {
			return mIsTap;
		}
	}
}