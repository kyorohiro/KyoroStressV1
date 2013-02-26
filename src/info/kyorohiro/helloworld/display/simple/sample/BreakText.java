package info.kyorohiro.helloworld.display.simple.sample;

import info.kyorohiro.helloworld.display.simple.SimpleFont;
import info.kyorohiro.helloworld.text.KyoroString;
import info.kyorohiro.helloworld.util.arraybuilder.CharArrayBuilder;
import info.kyorohiro.helloworld.util.arraybuilder.FloatArrayBuilder;

//todo android dependent now
public abstract class BreakText {
	private SimpleFont mFont = null;
	private int mWidth = 100;
	public abstract int breakText(CharArrayBuilder mBuffer);
	public abstract int getTextWidths(KyoroString text, int start, int end, float[] widths, float textSize);
	public abstract int getTextWidths(char[] buffer, int start, int end, float[] widths, float textSize);
	private static FloatArrayBuilder mWidths = new FloatArrayBuilder();

	public BreakText(SimpleFont font, int width) {
		setSimpleFont(font);
		mWidth = width;
	}

	public void setSimpleFont(SimpleFont font) {
		mFont = font;
	}

	public int getWidth() {
		return mWidth;
	}

	public void setWidth(int width) {
		mWidth = width;
	}

	public SimpleFont getSimpleFont() {
		return mFont;
	}

	public static synchronized int breakText(BreakText breaktext, char[] text, int index, int count, int width) {
		float s = breaktext.getSimpleFont().getFontSize();
		mWidths.setBufferLength(count);
		int len = text.length;
		
		float[] ws = mWidths.getBuffer();
		breaktext.getTextWidths(text, index, index+count, ws, s);
		float l=0;
		
		for(int i=0;i<len;i++) {
			l+=ws[i];
			if(l>=width){
				return  (i<=0?0:i);
			}
		}
		///android.util.Log.v("test","#ret2="+len+":"+l+":"+s+":"+width);
		return len;
	}

	public static synchronized int breakText(BreakText breaktext, KyoroString text, int index, int count) {
		//android.util.Log.v("test","#getWidth="+breaktext.getWidth());
		return  breakText(breaktext, text.getChars(), index, count, breaktext.getWidth());
	}

}
