package info.kyorohiro.helloworld.display.simple.sample;

import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleGraphicUtil;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimpleMotionEvent;
//import info.kyorohiro.helloworld.textviewer.manager.StartupCommandBuffer;

public class SimpleSwitchButton extends SimpleDisplayObject {
//	private StartupCommandBuffer mBuffer = null;
//	private boolean mOn = false;
	private int mStartX = 0;
	private int mStartY = 0;
	private int mEndX = 0;
	private int mEndY = 0;
	private int mWidth = 0;
	private int mFinY = 0;
	private String mLabel = "";
	private int mPosition = 0;
	private SwithAction mSwitch = null;
	public SimpleSwitchButton(String label, int position,SwithAction _switch) {
		mLabel = label;
		mPosition = position;
//		mBuffer = buffer;
		mSwitch = _switch;
	}

	private void calcPosition() {
		int w = ((SimpleDisplayObject)getParent()).getWidth();
		int bw = w/20;
		mStartX = w-bw*3;
		mStartY = bw*mPosition;
		mEndX = w-bw;
		mEndY = (int)(mStartY+bw*0.2)+mY;
		mWidth = (mEndX-mStartX);
		mFinY = mStartY + mWidth*2;
	}
	private boolean isContain(int x, int y) {
		int expandLength = mWidth/2;
		if(mStartX-expandLength<x&&x<mEndX+expandLength) {
			if(mStartY-expandLength<y&&y<mEndY+expandLength){
				return true;
			}
		}
		return false;
	}

	@Override
	public void paint(SimpleGraphics graphics) {
		calcPosition();
		if(mSwitch.on()) {
			graphics.setColor(SimpleGraphicUtil.parseColor("#55FF0000"));
		} else {
			graphics.setColor(SimpleGraphicUtil.parseColor("#55FFFF00"));				
		}
		graphics.drawText(mLabel, mStartX, mStartY);
		graphics.drawLine(mStartX, mStartY, mEndX, mStartY);
		graphics.drawLine(mStartX, mEndY,   mEndX,   mEndY);
		if(mIsTouched) {
			int x = mStartX + (mEndX-mStartX)/2;
			graphics.drawLine(x, mStartY, x, mStartY+mY);
			graphics.drawLine(mStartX, mFinY, mEndX, mFinY);
		}
	}

	public interface SwithAction {
		boolean on();
		void on(boolean value);
	}
	

	private void onTouched() {
//		boolean  b = mBuffer.getLineView().fittableToView();
		mSwitch.on(!mSwitch.on());
	}

	private boolean mIsTouched = false;
	@SuppressWarnings("unused")
	private int mX = 0;
	private int mY = 0;
	private int mPrevX =0;
	private int mPrevY =0;
	@Override
	public boolean onTouchTest(int x, int y, int action) {
		boolean isContain = isContain(x, y);
		switch(action) {
		case SimpleMotionEvent.ACTION_DOWN:
			if(isContain) {
				mIsTouched = true;
			}
			mPrevX = x;
			mPrevY = y;
			mX = 0;
			mY = 0;                                                                                                                                                                              
			break;
		case SimpleMotionEvent.ACTION_MOVE:
			if(mIsTouched){
				mX = x-mPrevX;
				mY = y-mPrevY;
			}
			break;
		case SimpleMotionEvent.ACTION_UP:
			if(mIsTouched) {
				onTouched();
			}
			mX = 0;
			mY = 0;
			mIsTouched = false;
			break;
		}
		return super.onTouchTest(x, y, action);
	}
}