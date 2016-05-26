package sancang.jjchat.androidclient;

import sancang.jjchat.androidclient.ChatService.ChatServiceBinder;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements ServiceConnection, OnClickListener {

	private EditText tvSend;
	private TextView tvContent;
	private Button btnSend;

	private TextView tvSettings;

	private Intent chatServiceIntent;
	private ChatService chatService;
	private Handler msgHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_title_bar);

		//cancel the notification if exists.
		NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notiMgr.cancel(R.layout.activity_main);
		
		//start and bind ChatService
		chatServiceIntent = new Intent(getApplicationContext(), ChatService.class);
		startService(chatServiceIntent);
		bindService(chatServiceIntent, this, BIND_AUTO_CREATE);

		tvSend = (EditText) findViewById(R.id.editSend);
		tvContent = (TextView) findViewById(R.id.tvContent);

		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);

		tvSettings = (TextView) findViewById(R.id.tvSettings);
		tvSettings.setOnClickListener(this);

		//set handler to receive message from ChatService
		msgHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {				
				ChatMessage chatMsg = (ChatMessage) msg.obj;
				onNewMessage(chatMsg);
			}
		};

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(chatService != null){
			chatService.listenMessage(null);
			chatService = null;
		}
	}


	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		// TODO Auto-generated method stub
		ChatServiceBinder chatServiceBinder = (ChatServiceBinder) binder;
		chatService = chatServiceBinder.getChatService();
		chatService.listenMessage(msgHandler);
	}


	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSend:
			sendMsg();
			break;
		case R.id.tvSettings://start the preference activity
			startActivity(new Intent(MainActivity.this, PrefActivity.class));
			break;
		default:
			break;
		}
	}

	private void onNewMessage(ChatMessage msg){
		switch (msg.msgType) {
		case Msg_Chat:
			tvContent.append(msg.sender + ": " + msg.content + "\n");
			break;
		case Msg_Login:

			break;
		default:
			break;
		}
	}

	private void sendMsg() {

		if(chatService != null){
			String content = tvSend.getText().toString();
			chatService.sendMessage(content);
		}

		tvSend.setText("");
	}
}