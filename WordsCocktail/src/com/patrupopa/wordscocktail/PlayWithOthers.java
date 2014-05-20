package com.patrupopa.wordscocktail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.os.Build;

public class PlayWithOthers extends Activity {

	public Socket socket;
	DataOutputStream out;
	BufferedReader in;
    public static final int SERVERPORT = 28028;
    public static final String SERVER_IP = "192.168.173.1";
    
	public String Name = "";
	public int playerNo = 0;
	private String TAG = "PlayWithOthers";
	private OnlineGame onlinegame;
	Dictionary _trie;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_play_with_others);

		if (savedInstanceState == null) {

		}
		setContentView(R.layout.loading);
		
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
						
						if (playerNo > 1)
							try {
								newGame();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						else
							setContentView(R.layout.waiting);
					}
				}
			});
		}
		/*
		try {
			//String url = getIntent().getData().toString();
			System.gc();
			loadDictionary();
			newGame();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}*/
	}
	
	private void connectToServer() {
		Thread thread = new Thread(new Runnable() {
			
			private int getPlayerNo() {
				int playerNo = 0;
				try {
					playerNo = Integer.parseInt(receiveFromServer());
					sendToServer("ok");
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
					sendToServer("ok");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			private String receiveFromServer() {
				String msg = null;
				try { 
					msg= in.readLine();
					//System.out.println("received: " + msg);
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
					
					// find number of players
					playerNo = getPlayerNo();
					
					Log.d(TAG, "Players comp: " + playerNo);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
		});
		thread.start();
	}
	
	public static String StreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();

        return sb.toString();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_with_others, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void newGame() throws Exception {
		onlinegame = new OnlineGame(this, _trie);

	}
	
	private void loadDictionary() {
		// TODO Auto-generated method stub
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } finally {
	        }
	        Log.d(TAG, "DONE loading words.");
	}

}
