package info.kyorohiro.helloworld.util;

public interface CyclingListInter<N> {
	public N get(int i);
	public int getNumOfAdd();
	public void clearNumOfAdd();
	public void add(N element);
	public int getNumberOfStockedElement();
	public int getMaxOfStackedElement();
	public void head(N element);
	public void clear();
	public N[] getLast(N[] ret, int numberOfRetutnArrayElement);
	public N[] getElements(N[] ret, int start, int end) ;
}
