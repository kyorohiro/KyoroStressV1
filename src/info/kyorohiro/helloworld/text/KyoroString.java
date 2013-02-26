package info.kyorohiro.helloworld.text;

import info.kyorohiro.helloworld.display.simple.SimpleFont;

//
// todo refactring
//
public class KyoroString  implements CharSequence {
	public char[] mContent = null;
	private float[] mCashWidth = null;
	private byte[] mCashByte = null;

	private int _mFontSize = 0;
	private long mLinePosition = 0;
	private boolean mIncludeLF = false;
	private boolean mIncludeCRLF = false;
	private int mColor = 0xFF000000;

	// file buffer
	private long mBeginPoint = -1;
	private long mEndPoint = -1;
	private boolean mIsCahsed = false;
	private int mPargedLF_CRLF = 0;
	private int mPargedEND = 0;
	private boolean mIsNowLoading = false;
	private String mType = "";
	private String mEXtra = "";

	public void setType(String type) {
		mType = type;
	}

	public String getType() {
		return mType;
	}

	public void setExtra(String extra) {
//		android.util.Log.v("kiyo","extra="+extra);
		mEXtra = extra;
	}

	public String getExtra() { 
		return mEXtra;
	}

	public void isNowLoading(boolean v) {
		mIsNowLoading = v;
	}	
	public boolean isNowLoading() {
		return mIsNowLoading;
	}
	
	public static KyoroString newKyoroStringWithLF(CharSequence content, int color) {
		if(content.charAt(content.length()-1)!='\n'){
			content = ""+content+"\n";//todo \r\n or \n
		}
		return new KyoroString(content, color);
	}

	public KyoroString(CharSequence content) {
		init(content, 0xFF000000);
	}

	public KyoroString(CharSequence content, int color) {
		init(content, color);
	}

	public KyoroString(char[] content, int length) {
		init(content, 0, length);
	}

	public KyoroString(char[] content, int start, int end) {
		init(content, start, end);
	}

	private void init(CharSequence content, int color) {
		int len = content.length();
		char[] contentBuffer = new char[len];
		for(int i=0;i<len;i++){
			contentBuffer[i] = content.charAt(i);
		}
		init(contentBuffer, 0, len);
		mColor = color;
	}

	private void init(char[] content, int start, int end) {
		int length = end-start;
		mContent = new char[length];
//		android.util.Log.v("kiyo","dd="+start+",end="+end+","+length+",c="+content.length);
		System.arraycopy(content, start, mContent, 0, length);
		if(mContent.length >0 && mContent[length-1]=='\n'){
			mIncludeLF = true;
			if(mContent.length>1&&mContent[length-2]=='\r') {
				mIncludeCRLF = true;
			}
		} else {
			mIncludeLF = false;
		} 
	}


	public void pargeLF(boolean includeCR) {
		mPargedLF_CRLF = length()-lengthWithoutLF(includeCR)+mPargedEND;
	}

	public void pargeEnd() {
		mPargedEND++;
	}

	public void releaseParge() {
		mPargedLF_CRLF = 0;
		mPargedEND = 0;
	}

	@Override
	public char charAt(int index) {
		return mContent[index];
	}

	public char[] getChars() {
		return mContent;
	}

	public int lengthWithoutLF(boolean includeCR) {
		if(includeCR&&includeCRLF()){
			return length()-2;
		}
		else if(includeLF()) {
			return length()-1;
		} else {
			return length();
		}
	}

	@Override
	public int length() {
		return mContent.length-mPargedLF_CRLF-mPargedEND;
	}

	public KyoroString newKyoroString(int start, int end) {
		KyoroString ret = new KyoroString(mContent, start, end);
		ret.setColor(getColor());
		return ret;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return newKyoroString(start, end);
	}

	@Override
	public String toString() {
		return new String(mContent, 0, length());
	}

	public boolean includeCRLF(){
		return mIncludeCRLF;
	}

	public boolean includeLF(){
		return mIncludeLF;
	}

	public void setBeginPointer(long pointer){
		mBeginPoint = pointer;
	}

	public long getBeginPointer(){
		return mBeginPoint;
	}

	public void setEndPointer(long pointer){
		mEndPoint = pointer;
	}

	public long getEndPointer(){
		return mEndPoint;
	}

	public void setLinePosition(long linePosition) {
		mLinePosition = linePosition;
	}

	public long getLinePosition() {
		return mLinePosition;
	}

	public int getColor() {
		return mColor;
	}
	
	public void setColor(int color) {
		mColor = color;
	}

	public void setCash(float[] buffer, int len, int fontSize) {
		_mFontSize = fontSize;
		if(mCashWidth == null) {
			// todo maybe be throw exception o rreturn false;
			return;
		}
		if(len >mCashWidth.length){
			len = mCashWidth.length;
		}
		System.arraycopy(buffer, 0, mCashWidth, 0, len);
	}

	public float getCashZoomSize(int size) {
		float ret = (float)size/_mFontSize;
		return ret;
	}

	public void setCashWidths(SimpleFont font, int fontSize) {
//		System.out.println("cash[A]="+font.getFontSize()+","+toString());
		if(mCashWidth == null||mCashByte.length <mContent.length) {
			mCashWidth = new float[mContent.length];
		}
		_mFontSize = (int)font.getFontSize();
//		System.out.println("cash[B]=s="+font.getFontSize()+",l="+length()+",f="+font.getFontSize());
		font.getTextWidths(this, 0, length(), mCashWidth, font.getFontSize());
		mIsCahsed = true;
	}

	public void setCashContent(byte[] content, int len) {
		if(mCashByte==null||mCashByte.length!=len){
			mCashByte = new byte[len];
		}
		System.arraycopy(content, 0, mCashByte, 0, len);
	}

	public byte[] getCashContent() {
		return mCashByte;
	}

	public static float[] EMPTY = new float[0];
	public float[] getCashWidths() {
		if(mCashWidth == null){
			return EMPTY;
		} else {
			return mCashWidth;
		}
	}

	public boolean useCashContent() {
		if(mCashByte == null|| mCashByte.length ==0){
			return false;
		}
		return true;
	}
 	public boolean use() {
 		return mIsCahsed;
	}

}
