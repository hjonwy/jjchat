package sancang.jjchat.androidclient;

import java.net.URISyntaxException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

public class MainActivity extends Activity {

	EditText tvSend;
	TextView tvContent;
	Socket mSocket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvSend = (EditText) findViewById(R.id.editSend);
		tvContent = (TextView) findViewById(R.id.tvContent);

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				send();
			}
		});
				
		try {
			mSocket = IO.socket("http://jjchat.duapp.com/");
			mSocket.on("chat message", onNewMessage);
			mSocket.connect();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connect() {

		
		AsyncTask<Void, String, Void> read = new AsyncTask<Void, String, Void>() {

			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					mSocket = IO.socket("http://jjchat.duapp.com/");
					mSocket.on("chat message", onNewMessage);
					mSocket.connect();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
		
		read.execute();

	}
	
	public void send() {
		String msg = tvSend.getText().toString();
//		tvContent.append(msg +"\n");
		tvSend.setText("");
		
		mSocket.emit("chat message", msg);
	}

	private Emitter.Listener onNewMessage = new Emitter.Listener() {
	    @Override
	    public void call(final Object... args) {
	    	MainActivity.this.runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	String msg = (String) args[0];
	                
	            	tvContent.append(msg + "\n");
	            }
	        });
	    }
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	    mSocket.disconnect();
	    mSocket.off("chat message", onNewMessage);
	}
}