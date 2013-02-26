package info.kyorohiro.helloworld.display.simple;

import info.kyorohiro.helloworld.text.KyoroString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.WeakHashMap;

//
// 依存関係を無視して、
//
public class MessageDispatcher {
	private static MessageDispatcher sInstance = new MessageDispatcher();
	private MessageDispatcher() {
	}

	public static MessageDispatcher getInstance() {
		if(sInstance == null) {
			sInstance = new MessageDispatcher();
		}
		return sInstance;
	}

//	private LinkedList<Receiver> mReceivers = new LinkedList<Receiver>();
	private WeakHashMap<String, Receiver> mReveivers = new WeakHashMap<String, Receiver>();
//	private HashMap<String, Receiver> mReveivers = new HashMap<String, Receiver>();

	public synchronized void send(KyoroString message) {
		if(message == null) {
			return;
		}
//		android.util.Log.v("kiyo","#send:"+message+","+message.getType()+","+message.getExtra()+","+message.getType());
		Receiver r = mReveivers.get(message.getType());
		if(r != null) {
			r.onReceived(message, message.getType());
		}
	}

	public void addReceiver(Receiver r) {
		mReveivers.put(r.getType(), r);
	}

	public void remove(Receiver r) {
		Receiver t = mReveivers.get(r.getType());
		if(t!=null&&t==r) {
			mReveivers.remove(r.getType());
		}
	}
	public static interface Receiver {
		public String getType();
		public void onReceived(KyoroString message, String type);
	}
}
