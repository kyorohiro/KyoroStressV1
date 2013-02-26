package info.kyorohiro.helloworld.util;

import java.util.LinkedList;

public class CyclingList<X> implements CyclingListInter<X> {

	private final LinkedList<X> mList;
	private final int mMaxOfStackedElement;
	private int mNumOfAdd = 0;

	public CyclingList(int listSize) {
		mMaxOfStackedElement = listSize;
		mList = new LinkedList<X>();
	}

	public synchronized int getMaxOfStackedElement() {
		return mMaxOfStackedElement;
	}

	public synchronized void moveLast(X element, int i) {
		mList.remove(i);
		mList.add(element);
	}
	public synchronized void clear() {
		mList.clear();
		mNumOfAdd = 0;
	}

	public synchronized void head(X element) {
		mList.addFirst(element);
		if(mList.size()>mMaxOfStackedElement) {
			mList.removeLast();
		}
		mNumOfAdd--;
	}

	public synchronized void add(X element) {
		mNumOfAdd++;
		mList.add(element);
		if(mList.size() > mMaxOfStackedElement) {
			mList.removeFirst();
		}
	}

	public synchronized X[] getLast(X[] ret, int numberOfRetutnArrayElement) {
		int lengthOfList = numberOfRetutnArrayElement;
		int max = getNumberOfStockedElement();
		if (max <= lengthOfList) {
			lengthOfList = max;
		}
		for (int i = 0; i < lengthOfList; i++) {
			ret[i] = get(max - lengthOfList + i);
		}
		return ret;
	}

	public synchronized X[] getElements(X[] ret, int start, int end) {
		int max = getNumberOfStockedElement();
		if (max < end) {
			end = max;
		}
		if (max < start) {
			start = max;
		}
		if (start < 0) {
			start = 0;
		}
		if (end < 0) {
			end = 0;
		}
		if (start > end) {
			int t = start;
			start = end;
			end = t;
		}
		int lengthOfList = end - start;
		if (ret.length < lengthOfList) {
			lengthOfList = ret.length;
		}

		for (int i = 0; i < end - start; i++) {
//			android.util.Log.v("kiyohiro","k="+i+",l="+mList.size()+"r="+ret.length);
			ret[i] = (X)get(i + start);
		}
		return ret;
	}

	public synchronized X get(int i) {
		int num = i % mMaxOfStackedElement;
		//android.util.Log.v("aaa",""+num);
		return mList.get(num);
	}

	public synchronized int getNumberOfStockedElement() {
		return mList.size();
	}

	@Override
	public synchronized int getNumOfAdd() {
		return mNumOfAdd;
	}

	public synchronized void setNumOfAdd(int num) {
		mNumOfAdd = num;		
	}

	@Override
	public synchronized void clearNumOfAdd() {
		mNumOfAdd = 0;
	}

}
