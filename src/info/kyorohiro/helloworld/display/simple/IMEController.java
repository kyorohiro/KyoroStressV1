package info.kyorohiro.helloworld.display.simple;

import java.security.KeyRep;
import java.util.HashMap;




//
//todo don't fix
public class IMEController {

	private boolean mIsUseBinaryKey = false;
	private KeyMap mKeyMap = new KeyMap();

	public void log(String str) {
	//	android.util.Log.v("kiyo","#=IMEC="+str);
	}
	public boolean tryUseBinaryKey(int keycode, boolean shift, boolean ctl, boolean alt) {
		if(alt==true||ctl == true||mIsUseBinaryKey||keycode == SimpleKeyEvent.KEYCODE_TAB) {
			return true;
		} else {
			return false;			
		}
	}

	public void binaryKey(MyInputConnection conn, int keycode, int scancode, char label, boolean shift, boolean ctl, boolean alt) 
	{
		log(""+keycode+",c/a"+ctl+"/"+alt);
		if(mKeyMap.containKey(keycode, shift)){
			decorateKey(conn, (char)mKeyMap.convertKey(keycode, shift), shift, ctl, alt);
		}
		else  {
			if(KeyMap.KEYCODE_BACKSPSACE == keycode) {
				conn.addCommitText(new CommitText(SimpleKeyEvent.KEYCODE_BACK));			
			}
			else if(KeyMap.KEYCODE_ENTER == keycode) {
				conn.addCommitText(new CommitText(SimpleKeyEvent.KEYCODE_ENTER));
			}
			else if(KeyMap.KEYCODE_DPAD_DOWN == keycode) {
				conn.addCommitText(new CommitText(SimpleKeyEvent.KEYCODE_DPAD_DOWN));				
			}
			else if(KeyMap.KEYCODE_DPAD_LEFT == keycode) {
				conn.addCommitText(new CommitText(SimpleKeyEvent.KEYCODE_DPAD_LEFT));
			}
			else if(KeyMap.KEYCODE_DPAD_RIGHT == keycode) {
				conn.addCommitText(new CommitText(SimpleKeyEvent.KEYCODE_DPAD_RIGHT));
			}
			else if(KeyMap.KEYCODE_DPAD_UP== keycode) {
				conn.addCommitText(new CommitText(SimpleKeyEvent.KEYCODE_DPAD_UP));
			}
			else if(SimpleKeyEvent.KEYCODE_TAB == keycode) {
				conn.addCommitText(new CommitText(SimpleKeyEvent.KEYCODE_TAB));
			} 
			//else {
			//	decorateKey(conn, label, shift, ctl, alt);
			//}
		}

	}

	public void decorateKey(MyInputConnection conn, char c, boolean shift, boolean ctl, boolean alt) {
		CommitText v = new CommitText(""+c, 1);
		v.pushingCtrl(ctl);
		v.pushingAlt(alt);
		conn.addCommitText(v);
	}

	public void decorateKey(MyInputConnection conn, CharSequence text, int pos,  boolean shift, boolean ctl, boolean alt) {
		CommitText v = new CommitText(text, pos);
		v.pushingCtrl(ctl);
		v.pushingAlt(alt);
		conn.addCommitText(v);
	}

	public static class KeyMap{
		private static HashMap<Integer, Character> sMMap = new HashMap<Integer, Character>();
		private static HashMap<Integer, Character> sRMap = new HashMap<Integer, Character>();
		private static int KEYCODE_ENTER = 66;
		private static int KEYCODE_BACKSPSACE = 67;
		public static final int KEYCODE_DPAD_UP = 0x00000013;
		public static final int KEYCODE_DPAD_DOWN = 0x00000014;
		public static final int KEYCODE_DPAD_LEFT = 0x00000015;
		public static final int KEYCODE_DPAD_RIGHT = 0x00000016;
		public static final int KEYCODE_ESCAPE = 111;

		public boolean containKey(int key, boolean shift) {
			HashMap<Integer, Character> t = null;
			if(!shift) {
				t = sMMap;
			} else {
				t = sRMap;
			}
			if(t.containsKey(key)) {
				return true;
			} else {
				return false;
			}			
		}
		public int convertKey(int key, boolean shift) {
			HashMap<Integer, Character> t = null;
			if(!shift) {
				t = sMMap;
			} else {
				t = sRMap;
			}
			if(t.containsKey(key)) {
				return (int)t.get(key);
			} else {
				return key;
			}
		}

		static {
			sMMap.put(KEYCODE_ESCAPE, (char)0x1b);
			sRMap.put(KEYCODE_ESCAPE, (char)0x1b);
			sMMap.put(7, '0');
			sMMap.put(8, '1');
			sRMap.put(8, '!');
			sMMap.put(9, '2');
			sRMap.put(9, (char)('\"'));
			sMMap.put(10, '3');
			sRMap.put(10, (char)('#'));
			sMMap.put(11, '4');
			sRMap.put(11, (char)('$'));
			sMMap.put(12, '5');
			sRMap.put(12, (char)('%'));
			sMMap.put(13, '6');
			sRMap.put(13, (char)('&'));
			sMMap.put(14, '7');
			sRMap.put(14, (char)('\''));
			sMMap.put(15, '8');
			sRMap.put(15, (char)('('));
			sMMap.put(16, '9');
			sRMap.put(16, (char)(')'));
			sMMap.put(17, '*');
			sMMap.put(18, '#');

			sMMap.put(29, 'a');
			sMMap.put(30, 'b');
			sMMap.put(31, 'c');
			sMMap.put(32, 'd');
			sMMap.put(33, 'e');
			sMMap.put(34, 'f');
			sMMap.put(35, 'g');
			sMMap.put(36, 'h');
			sMMap.put(37, 'i');
			sMMap.put(38, 'j');
			sMMap.put(39, 'k');
			sMMap.put(40, 'l');
			sMMap.put(41, 'm');
			sMMap.put(42, 'n');
			sMMap.put(43, 'o');
			sMMap.put(44, 'p');
			sMMap.put(45, 'q');
			sMMap.put(46, 'r');
			sMMap.put(47, 's');
			sMMap.put(48, 't');
			sMMap.put(49, 'u');
			sMMap.put(50, 'v');
			sMMap.put(51, 'w');
			sMMap.put(52, 'x');
			sMMap.put(53, 'y');
			sMMap.put(54, 'z');

			sRMap.put(29, 'A');
			sRMap.put(30, 'B');
			sRMap.put(31, 'C');
			sRMap.put(32, 'D');
			sRMap.put(33, 'E');
			sRMap.put(34, 'F');
			sRMap.put(35, 'G');
			sRMap.put(36, 'H');
			sRMap.put(37, 'I');
			sRMap.put(38, 'J');
			sRMap.put(39, 'K');
			sRMap.put(40, 'L');
			sRMap.put(41, 'M');
			sRMap.put(42, 'N');
			sRMap.put(43, 'O');
			sRMap.put(44, 'P');
			sRMap.put(45, 'Q');
			sRMap.put(46, 'R');
			sRMap.put(47, 'S');
			sRMap.put(48, 'T');
			sRMap.put(49, 'U');
			sRMap.put(50, 'V');
			sRMap.put(51, 'W');
			sRMap.put(52, 'X');
			sRMap.put(53, 'Y');
			sRMap.put(54, 'Z');

			sMMap.put(55, ',');
			sRMap.put(55, '<');
			sMMap.put(56, '.');
			sRMap.put(56, '>');
			sMMap.put(62, ' ');
			sMMap.put(69, '-');
			sRMap.put(69, '=');
			sMMap.put(70, '^');
			sRMap.put(70, '~');
			sMMap.put(71, '[');
			sRMap.put(71, '{');
			sMMap.put(72, ']');
			sRMap.put(72, '}');//_\_\
			sMMap.put(73, '\\');//73
			sRMap.put(73, '_');//73 |\|
//			sRMap.put(73, '|');//73
			sMMap.put(74, ';');
			sRMap.put(74, '+');
			sMMap.put(75, ':');
			sRMap.put(75, '*');
			sMMap.put(76, '/');
			sRMap.put(76, '?');			
			sMMap.put(77, '@');
			sRMap.put(77, '`');
			sMMap.put(81, '+');
			sMMap.put(0x9a, '/');//154
			sMMap.put(0x9b, '*');//155	
			sMMap.put(0x9c, '-');//156
			sMMap.put(0x9d, '+');//157
			sMMap.put(0xd8, '\\');
//			sRMap.put(0xd8, '_');
			sRMap.put(0xd8, '|');
		}
	}
}
