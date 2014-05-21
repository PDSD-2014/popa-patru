package com.patrupopa.wordscocktail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.patrupopa.wordscocktail.Game.Status;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.os.Build;

public class PlayWithOthers extends Activity implements Stopper {

	public Socket socket;
	DataOutputStream out;
	BufferedReader in;
    public static final int SERVERPORT = 28028;
    public static final String SERVER_IP = "192.168.173.1";
    
	public String Name = "";
	public int playerNo = 0;
	public String board = "";
	private String TAG = "PlayWithOthers";
	private Game _onlinegame = null;
	private GameThread _thread;
	Dictionary _trie;
	private Handler handler = new Handler(){
			  @Override
			  public void handleMessage(Message msg) {
			    newOnlineGame();
			    Log.d(TAG, "BOARD to play: " + _onlinegame.getBoard().toString());
			    if( _onlinegame.getStatus() == Status.STARTING )
				{
					
					_onlinegame.start();
					_thread.start();
					return;
				}
			  }
			};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			return;
		}
		
		if (Name == "") {
			setContentView(R.layout.enter_name);
			Button b = (Button) findViewById(R.id.get_name);
			b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					EditText editText = (EditText) findViewById(R.id.name);
					if (editText.getText().length() > 0) {
						// get user name
						Name = editText.getText().toString();
						Log.d(TAG, "Name: " + Name);
						
						// make server connection
						connectToServer();
						setContentView(R.layout.waiting);
					}
				}
			});
		}
		String action = getIntent().getAction();
		Log.d(TAG, action);
		loadDictionary();
		_onlinegame = new Game(this, _trie);
		board = _onlinegame.getBoard().toString();
	}
	
	private void connectToServer() {
		Thread thread = new Thread(new Runnable() {
			
			private void negotiateBoard() {
				board = receiveFromServer();
				Log.d(TAG, "Received board " + board);
				if (board != null)
					_onlinegame.setBoard(board);
			}
			
			private void sendBoard() {
				while (board == "") {
					continue;
				}
				Log.d(TAG, "Board: " + board);
				sendToServer(board);
			}
			
			private int getPlayerNo() {
				int playerNo = 0;
				try {
					playerNo = Integer.parseInt(receiveFromServer());
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}
				return playerNo;
			}
			
			private void sendUserName() {
				sendToServer(Name);
				try {
					Name = receiveFromServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			private String receiveFromServer() {
				String msg = null;
				try { 
					msg= in.readLine();
				} catch (Exception exception) {
					exception.printStackTrace();
					msg = null;
				}
				return msg;
			}
			
			private void sendToServer(String msg) {
				if(msg.indexOf('\n') == -1) {
					msg +='\n';
				}
				try {
					out.writeBytes(msg);
				} catch (Exception exception) {
					exception.printStackTrace();
				} 
			}
			
            @Override
            public void run() {
            	try {
					InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
					socket = new Socket(serverAddr, SERVERPORT);
					out = new DataOutputStream(socket.getOutputStream());
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
					// send username
					sendUserName();
					Log.d(TAG, "Name comp: " + Name);
					
					// send local board to server
					sendBoard();
					
					// find number of players
					playerNo = getPlayerNo();
					
					if (playerNo < 2)
						getPlayerNo();
					Log.d(TAG, "Players comp: " + playerNo);

					Log.d(TAG, "Game can now begin");
					
					// receive board from server
					negotiateBoard();
					
					// start new game with your friends!
					Message msg = new Message();
					handler.sendMessage(msg);
					
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
		});
		thread.start();
	}
	
	
	public void newOnlineGame() {
		Log.d(TAG, "ONLINE GAME");
		
		PlayView _playView = new PlayView(this, _onlinegame);
		
		// first of all stop the thread that was running
		if (_thread != null) {
			_thread.exit();
		}
		_thread = new GameThread();
		_thread.setCounter(_onlinegame);
		_thread.addWorker(_playView);
		_thread.setStopper(this);

		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(this.getWindow().getAttributes()
				.width, this.getWindow().getAttributes()
				.height);
		setContentView( _playView , lp);
		_playView.setKeepScreenOn(true);
		Log.d(TAG, "ONLINE GAME end constr");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_with_others, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.rotate_board:
			_onlinegame.rotateBoard();	
		break;
		case R.id.save_game:
			_thread.exit();
			//saveGame();
			finish();
		break;
		case R.id.end_game:
			//_onlinegame.endNow();
			//_thread.exit();
			//finish();
		}
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return _onlinegame.getStatus() == Game.Status.RUNNING;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void showScore(){
		//stop the thread
		_thread.exit();
		//give the control to the score intent, to be implemented
		Bundle bun = new Bundle();
		Intent scoreIntent = new Intent("com.popapatru.wordscocktail.action.FINAL_SCORE");
		
		//and then will get it from here
		scoreIntent.putExtras(bun);

		startActivity(scoreIntent);
		_onlinegame.setStatus(Game.Status.FINISHED);
		finish();
	}
	
	private void loadDictionary() {
			Log.d(TAG, "Loading dictionary...");
	        InputStream inputStream = getResources().openRawResource(R.raw.dictionary);
	        int SIZE = 64000;
	        byte[] barray = new byte[SIZE];
	        _trie = new Dictionary();
	        
	        long diff = 0 ; 
	        try {
	            String line;
	            try {
	            	long currentTime = System.currentTimeMillis();
	            	String s = null;
					while ( (inputStream.read( barray, 0, SIZE )) != -1 )
					{
						s = new String(barray);
					    int pos = 0, end;
					    String aux = null;
			            while ((end = s.indexOf("\r\n", pos)) >= 0) {
			                aux = s.substring(pos,end).trim();
			            	int length = aux.length(); 
			            	if( length > 1 && length <= 8 )
			            		_trie.insert(aux);
			                pos = end + 1;
			            }
					}
					diff = System.currentTimeMillis() - currentTime;
					
				} catch (IOException e) {
					e.printStackTrace();
				}
	        } finally {
	        }
	        Log.d(TAG, "DONE loading words.");
	}

	@Override
	public void stopEvent() {
		showScore();
	}

}
