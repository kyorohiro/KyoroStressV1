package info.kyorohiro.helloworld.display.simple.sample;

import info.kyorohiro.helloworld.display.simple.SimpleFont;
import info.kyorohiro.helloworld.text.KyoroString;
import info.kyorohiro.helloworld.util.arraybuilder.CharArrayBuilder;

//
//�@���̃R�[�h����A����Ȃ��@�\���폜����K�v������B
//�@--> LineView�̃J�[�\���֘A�̃R�[�h���C������^�C�~���O��
//     �悳��
public class MyBreakText extends BreakText {

	public MyBreakText(SimpleFont font) {
		super(font, 400);
	}
	
	public void setBufferWidth(int w){
		setWidth(w);
	}

	public int breakText(CharArrayBuilder b, int width) {

		//long time1 = 0;
		//long time2 = 0;
		//time1 = System.currentTimeMillis();		
		int len = BreakText.breakText(this, b.getBuffer(), 0, b.length(), width);
		//time2 = System.currentTimeMillis();
		//android.util.Log.v("kiyo","time a="+(time2-time1));

		//int len = b.getCurrentBufferedMojiSize();
		return len;
	}

	@Override
	public int breakText(CharArrayBuilder mBuffer) {
		return breakText(mBuffer, getWidth());
	}
	

	@Override
	public int getTextWidths(KyoroString text, int start, int end, float[] widths, float textSize) {
		int ret = 0;
//		long time1 = 0;
//		long time2 = 0;
		//time1 = System.currentTimeMillis();
		ret = getTextWidths(text.getChars(), start, end, widths, textSize);
		//time2 = System.currentTimeMillis();
		//android.util.Log.v("kiyo","time a="+(time2-time1));
		return ret;
	}

	@Override
	public int getTextWidths(char[] buffer, int start, int end, float[] widths, float textSize) {
		SimpleFont font = getSimpleFont();
	//	long time1 = 0;
	//	long time2 = 0;
	//	long time3 = 0;
		try {
//			time1 = System.currentTimeMillis();
			int ret = 0;
			ret = font.getTextWidths(buffer, start, end, widths,textSize);

//			time2 = System.currentTimeMillis();
			// �ȉ��̃R�[�h��SimpleFont�ɂ���ق����悢����
			int t=0;
			for(int i=start;i<end;i++){
				t = font.lengthOfControlCode(buffer[i], (int)textSize);
				if(t!=0){
					widths[i-start] = t;
				}
			//	if(buffer[i]=='\t'){
			//		android.util.Log.v("kiyo","time ["+i+"]"+buffer[i]+"="+widths[i-start]);
			//	}
			}
//			time3 = System.currentTimeMillis();
//			android.util.Log.v("kiyo","time a="+(time2-time1)+",b="+(time3-time2));
			return ret;
		}catch(Throwable t){
			return 0;
		}
	}

}
