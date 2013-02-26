package info.kyorohiro.helloworld.util;


import java.util.Collections;
import java.util.LinkedList;

//
// now creating
public class AutocandidateList {
	private LinkedList<String> mList = new LinkedList<String>();

	public void sort() {
		Collections.sort(mList);
	}

	public String candidateText(String start) {
		sort();
		
		boolean find = false;
		String ret = "";
//		android.util.Log.v("kiyo","ret=0:"+ret+","+mList.size());
		for(int i=0;i<mList.size();i++) {
//			android.util.Log.v("kiyo","ret="+i+":"+mList.get(i));
			if(mList.get(i).startsWith(start)) {
				String tmp = mList.get(i);
				if(!find){
					ret = mList.get(i);
//					android.util.Log.v("kiyo","ret="+ret);
				} else {
					int len = (tmp.length()<ret.length()?tmp.length():ret.length());
					int s= start.length();
					int j=s;
					for(;j<len;j++) {
						if(tmp.charAt(j) != ret.charAt(j)) {
							break;
						}
					}
					if(ret.length() != j) { 
						ret = ret.substring(0, j);
					}
					//android.util.Log.v("kiyo","ret="+ret);
				}
				find = true;
			}
			else if(find){
				break;
			}
		}
		return ret;
	}

	public void add(String t) {
		mList.add(t);
	}

	public void clear() {
		mList.clear();
	}
}
