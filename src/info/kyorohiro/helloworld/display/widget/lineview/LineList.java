package info.kyorohiro.helloworld.display.widget.lineview;

import info.kyorohiro.helloworld.display.simple.SimpleDisplayObject;
import info.kyorohiro.helloworld.display.simple.SimpleDisplayObjectContainer;
import info.kyorohiro.helloworld.display.simple.SimpleGraphics;
import info.kyorohiro.helloworld.display.simple.SimpleStage;
import info.kyorohiro.helloworld.util.CyclingListInter;
import android.graphics.Color;
import android.view.MotionEvent;

public class LineList extends SimpleDisplayObject {
	private int mNumOfLine = 100;
	private int mPosition = 0;
	private int mStartPosition = 0;
	private int mEndPosition = 0;
	private int mBlank = 0;
	private int mListHeight = 40;
	private ListItemUIEvent mEvent = null;

	private ListDatamTemplate mTemplate = new ListDatamTemplate() {
		@Override
		public void paint(SimpleGraphics graphics) {
			graphics.setColor(Color.GREEN);
			Object o = getListDatam();
			if (o != null) {
				graphics.drawText(getListDatam().toString(), 0, 0);
			}
		}
	};

	public void setOnListItemUIEvent(ListItemUIEvent event) {
		mEvent = event;
	}

	private CyclingListInter<Object> mList = null;

	public LineList(CyclingListInter<Object> inputtedText, int height) {
		mList = inputtedText;
		mListHeight = height;
	}

	public synchronized void setTemplate(ListDatamTemplate template) {
		mTemplate = template;
	}

	public void setCyclingList(CyclingListInter<Object> inputtedText) {
		mList = inputtedText;
	}

	public CyclingListInter<Object> getCyclingList() {
		return mList;
	}

	public int getListHeight() {
		return 40;
	}

	@Override
	public void paint(SimpleGraphics graphics) {
		CyclingListInter<Object> showingText = mList;
		updateStatus(graphics, showingText);
		drawBG(graphics);
		int start = start(showingText);
		int end = end(showingText);
		int blank = blank(showingText);

		Object[] list = null;
		if (start > end) {
			list = new Object[0];
		} else {
			list = new Object[end - start];
			list = showingText.getElements(list, start, end);
		}

		showLineDate(graphics, list, blank);
		mStartPosition = start;
		mEndPosition = end;
		mBlank = blank;
	}

	public int start(CyclingListInter<?> showingText) {
		int numOfStackedString = showingText.getNumberOfStockedElement();
		int referPoint = numOfStackedString - (mPosition + mNumOfLine);
		int start = referPoint;
		if (start < 0) {
			start = 0;
		}
		return start;
	}

	public int end(CyclingListInter<?> showingText) {
		int numOfStackedString = showingText.getNumberOfStockedElement();
		int referPoint = numOfStackedString - (mPosition + mNumOfLine);
		int end = referPoint + mNumOfLine;
		if (end < 0) {
			end = 0;
		}
		if (end >= numOfStackedString) {
			end = numOfStackedString;
		}
		return end;
	}

	public int blank(CyclingListInter<?> showingText) {
		int numOfStackedString = showingText.getNumberOfStockedElement();
		int referPoint = numOfStackedString - (mPosition + mNumOfLine);
		int blank = 0;
		boolean uppserSideBlankisViewed = (referPoint) < 0;
		if (uppserSideBlankisViewed) {
			blank = -1 * referPoint;
		}
		return blank;
	}

	private void showLineDate(SimpleGraphics graphics, Object[] list, int blank) {
		for (int i = 0; i < list.length; i++) {
			if (list[i] == null) {
				continue;
			}
			int x = getWidth() / 20;
			int y = getListHeight() * (blank + i /*+ 1*/);
			mTemplate.setPoint(x, y);
			mTemplate.setListDatam(list[i]);

			// todo
			SimpleGraphics tmp = graphics.getChildGraphics(graphics,
					graphics.getGlobalX() + mTemplate.getX(),
					graphics.getGlobalY() + mTemplate.getY());
			mTemplate.paint(tmp);
		}
	}

	private void drawBG(SimpleGraphics graphics) {
		graphics.drawBackGround(Color.parseColor("#cc795514"));
		graphics.setColor(Color.parseColor("#ccc9f486"));
	}

	private void updateStatus(SimpleGraphics graphics,
			CyclingListInter<?> showingText) {
		mNumOfLine = getHeight() / getListHeight();
		int blankSpace = mNumOfLine / 2;
		if (mPosition < -(mNumOfLine - blankSpace)) {
			setPosition(-(mNumOfLine - blankSpace) - 1);
		} else if (mPosition > (showingText.getNumberOfStockedElement() - blankSpace)) {
			setPosition(showingText.getNumberOfStockedElement() - blankSpace);
		}
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	public int getPosition() {
		return mPosition;
	}

	@Override
	public boolean onTouchTest(int x, int y, int action) {
		if(getX()< x && x < (getX()+getWidth())){
			int t = y-mBlank*getListHeight();
			int index = t/getListHeight();
			CyclingListInter<Object> list = getCyclingList();
			index = mStartPosition+index;
			if(mEvent != null){
				if(0<=index && index < list.getNumberOfStockedElement()){
//					android.util.Log.v("kiyohiro","i="+index+",s="+list.getNumberOfStockedElement());
					mEvent.selected(list.get(index), action, index);
				}
			}
		}
		return super.onTouchTest(x, y, action);
	}

	public static class ListDatamTemplate extends SimpleDisplayObjectContainer {
		private Object mData = null;

		public void setListDatam(Object listDatam) {
			mData = listDatam;
		}

		public Object getListDatam() {
			return mData;
		}
	}

	public static interface ListItemUIEvent {
		public static String STATE_DOWN = "down";
		public static String STATE_UP = "up";
		public static String STATE_MOVE = "move";

		public void selected(Object obj, int state, int index);
	}
}
