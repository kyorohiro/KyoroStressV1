package info.kyorohiro.helloworld.display.simple;

//
// todo don't fix
public interface  MyInputConnection {
	CharSequence getComposingText();
	void addCommitText(CommitText text);
	void setIMEController(IMEController controller);
	CommitText popFirst();
}
