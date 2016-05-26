package sancang.jjchat.androidclient;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import sancang.jjchat.androidclient.ChatMessage.MessageType;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class ChatService extends Service {

	public class ChatServiceBinder extends Binder{

		public ChatService getChatService(){
			return ChatService.this;
		}
	}

	private ChatServiceBinder binder;
	private Handler listenHandler;
	private Socket mSocket;
	private NotificationManager notificationManager;
	private Builder notificationBuilder;

	//the raw message to send message 
	public  void sendMessage(String msg) {
		mSocket.emit("chat", msg);
	}

	public void listenMessage(Handler handler) {
		listenHandler = handler;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		binder = new ChatServiceBinder();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationBuilder = new Builder(this);
		notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
		notificationBuilder.setContentTitle("New Message");
		notificationBuilder.setContentText("new message");

		Intent notiIntent = new Intent(this, MainActivity.class);
		notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT));

		//connect the cloud chat server
		connectChatServer();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		mSocket.disconnect();
		mSocket.off("chat", onNewMessage);
	}

	private void connectChatServer(){
		try {
			mSocket = IO.socket("http://jjchat.duapp.com/");
			mSocket.on("chat", onNewMessage);
			mSocket.connect();

			JSONObject login = new JSONObject();
			login.put("name", "Android");
			mSocket.emit("login", login);

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Emitter.Listener onNewMessage = new Emitter.Listener() {
		@SuppressLint("NewApi")
		@Override
		public void call(final Object... args) {

			try {
				JSONObject msg = (JSONObject) args[0];
				String sender = msg.getString("sender");
				String content = msg.getString("content");

				if(listenHandler == null){
					//send a notifier if necessary
					//Toast.makeText(ChatService.this, "No listner now.", Toast.LENGTH_SHORT).show();
					notificationBuilder.setContentText(content);
					notificationManager.notify(R.layout.activity_main, notificationBuilder.build());

					return;
				}

				ChatMessage chatMsg = new ChatMessage(MessageType.Msg_Chat);
				chatMsg.sender = sender;
				chatMsg.content = content;

				Message message = new Message();
				message.obj = chatMsg;

				listenHandler.sendMessage(message);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
