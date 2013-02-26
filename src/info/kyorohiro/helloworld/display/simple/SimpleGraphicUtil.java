package info.kyorohiro.helloworld.display.simple;


import info.kyorohiro.helloworld.text.KyoroString;
import info.kyorohiro.helloworld.util.arraybuilder.CharArrayBuilder;
import info.kyorohiro.helloworld.util.arraybuilder.FloatArrayBuilder;

public class SimpleGraphicUtil {

	private static final int sControlCodeColoe = SimpleGraphicUtil.parseColor("#99FF9911");
	private static final int sControlCodeColoe20 = SimpleGraphicUtil.parseColor("#22FF9911");
	public static final int BLACK = parseColor("#FF000000");
	public static final int WHITE = parseColor("#FFFFFFFF");
	public static final int GREEN = parseColor("#FF00FF00");
	public static final int YELLOW = parseColor("#FFFFFF00");
	public static final int RED = parseColor("#FFFF0000");

	public static void fillRect(SimpleGraphics graphics, int x, int y, int w, int h) {
		drawRect(graphics, SimpleGraphics.STYLE_FILL, x, y, w, h);
	}

	public static void drawRect(SimpleGraphics graphics, int x, int y, int w, int h) {
		drawRect(graphics, SimpleGraphics.STYLE_STROKE, x, y, w, h);
	}
	
	public static void drawRect(SimpleGraphics graphics,int style, int x, int y, int w, int h) {
		int s = graphics.getStyle();
		graphics.setStrokeWidth(1);
		graphics.setStyle(style);
		graphics.startPath();
		graphics.moveTo(x, y);
		graphics.lineTo(x, h+y);
		graphics.lineTo(x+w, h+y);
		graphics.lineTo(x+w, y);
		graphics.lineTo(x, y);
		graphics.endPath();
		graphics.setStyle(s);
	}

	public static void drawControlCodeRect(SimpleGraphics graphics,int style, int x, int y, int w, int h) {
		graphics.drawLine(x+w, h/2+y, x+w, y);
		graphics.drawLine(x+w, y, x, y);
	}

	private static FloatArrayBuilder mCash = new FloatArrayBuilder();
	public static void drawString(SimpleGraphics graphics, KyoroString text, int x, int y, FloatArrayBuilder cash) {
		SimpleFont font = graphics.getSimpleFont();
		int textSize = graphics.getTextSize();
		char[] buffer = text.getChars();
		int len = text.length();
		if(cash == null) {
			cash = mCash;
		}
		cash.setBufferLength(len);
		//float[] widths = cash.getAllBufferedMoji(); 
		int start = 0;
		int end = 0;
		//long sTime = 0;
		//long eTime = 0;
		try{
			//sTime = System.currentTimeMillis();
			if(!text.use()&&text.length()!=0){
				text.setCashWidths(font, textSize);
			}
			end = text.length();
			float zoom = text.getCashZoomSize(textSize);
			graphics.drawPosText(buffer, text.getCashWidths(), zoom, start, end,x, y);
			drawControlCode(graphics, text, zoom, x, y, textSize);
		}finally{
			//eTime = System.currentTimeMillis();
			//android.util.Log.v("time","time[--2--]="+(eTime-sTime));
		}
	}
	
	private static CharArrayBuilder slineBuffer = new CharArrayBuilder();
	private static FloatArrayBuilder sWidthBuffer = new FloatArrayBuilder();
	private static synchronized int drawControlCode(SimpleGraphics graphics, KyoroString st, float zoom, int x, int y, int textSize) {
		char[] buffer = st.getChars();
		slineBuffer.setBufferLength(buffer.length*2);
		char[] line = slineBuffer.getBuffer();
		sWidthBuffer.setBufferLength(buffer.length*2);
		float[] widths = sWidthBuffer.getBuffer();
		int j=0;
		float t=0;
		int c = graphics.getColor();
		graphics.setStrokeWidth(1);
		for(int i=0;i<buffer.length;i++){
			if(SimpleFont.isControlCode(buffer[i], textSize)) {
				String r = Integer.toHexString(buffer[i]);
				if(buffer[i] == '\r'||buffer[i] == '\n') {
					graphics.setColor(sControlCodeColoe);
				}else {	
					graphics.setColor(sControlCodeColoe20);
				}
				SimpleGraphicUtil.drawControlCodeRect(graphics, SimpleGraphics.STYLE_STROKE, (int)(x+t*zoom), y, (int)(st.getCashWidths()[i]*zoom), -textSize);
				for(int n=0;n<r.length();n++){
					line[j] = r.charAt(n);
					widths[j] = st.getCashWidths()[i]/r.length();
					t += widths[j];
					j++;
				}
			} else {
				line[j] = ' ';
				widths[j] = st.getCashWidths()[i];
				t += widths[j];
				j++;
			}
		}
		graphics.setColor(sControlCodeColoe20);
		int ts = graphics.getTextSize();
		graphics.setTextSize(ts/3);
		graphics.drawPosText(line, widths, zoom, 0, j, x, y);
		graphics.setTextSize(ts);
		graphics.setColor(c);


//		eTime = System.currentTimeMillis();
//		android.util.Log.v("time","time[--1--]="+(eTime-sTime));
		return 0;
	}

	public static int parseColor(String colorSource) {
		String c = colorSource.replaceAll(" ","").replaceAll("#","");
		return (int)Long.parseLong(c, 16);
	}

	public static int argb(int a, int r, int g, int b) {
		int ret = ((a&0xff)<<(3*8))|((r&0xff)<<(2*8))|((g&0xff)<<(1*8))|((b&0xff)<<(0*8));
		return ret;
	}
	public static int colorA(int c) {
		int ret = 0xff&(c>>(3*8));
		return ret;		
	}
	public static int colorR(int c) {
		int ret = 0xff&(c>>(2*8));
		return ret;		
	}
	public static int colorG(int c) {
		int ret = 0xff&(c>>(1*8));
		return ret;		
	}
	public static int colorB(int c) {
		int ret = 0xff&(c>>(0*8));
		return ret;		
	}

}
