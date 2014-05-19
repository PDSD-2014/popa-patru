package com.patrupopa.wordscocktail;

import java.io.IOException;
import java.io.InputStream;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class PlayWithOthers extends Activity {

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
		try {
			//String url = getIntent().getData().toString();
			System.gc();
			loadDictionary();
			newGame();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
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
