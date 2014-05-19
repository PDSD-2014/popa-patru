package com.patrupopa.wordscocktail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.patrupopa.wordscocktail.Game.Status;

import android.app.Activity;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

public class PlaySingleGame extends Activity implements Stopper {

    private static final String TAG = "In play single game";
	//private variables
	Game _game;
	private GameThread _thread;
	private Menu menu;
	Dictionary _trie;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     	super.onCreate(savedInstanceState);
     	
     	setContentView(R.layout.loading);
     	
		if(savedInstanceState != null) {
			try {
				restoreGame(savedInstanceState);
			} catch (Exception e) {
				
			}
			return;
		}
		try {
			String action = getIntent().getAction();
			if(action.equals("com.popapatru.wordscocktail.action.RESTORE_GAME")) {
				//restoreGame();
			} else if(action.equals("com.popapatru.wordscocktail.action.NEW_GAME")) {
				System.gc();
				loadDictionary();
				newSingleGame();
			} else {
				
			}
		} catch (Exception e) {
			
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu m) {
		menu = m;

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.single_game , menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.rotate_board:
				_game.rotateBoard();	
			break;
			case R.id.save_game:
				_thread.exit();
				saveGame();
				finish();
			break;
			case R.id.end_game:
				_game.endNow();
		}
		return true;
	}

	private void saveGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return _game.getStatus() == Game.Status.RUNNING;
	}
	
	/*function for restoring game from bundle*/
	private void restoreGame(Bundle bun) {
		_game = new Game(this,bun);

		restoreGame(_game);
	}
	private void restoreGame(Game _game2) {
		// TODO Auto-generated method stub
		
	}

	private void newSingleGame() {
		//make new instance of  the game
		_game = new Game(this,_trie);
		
		PlayView _playView = new PlayView(this,_game);
		
		/*first of all stop the thread that was running*/
		if( _thread != null ) {
			_thread.exit();
		}
		_thread = new GameThread();
		_thread.setCounter(_game);
		_thread.addWorker(_playView);
		_thread.setStopper(this);

		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(this.getWindow().getAttributes()
				.width, this.getWindow().getAttributes()
				.height);
		setContentView( _playView , lp);
		_playView.setKeepScreenOn(true);

	}

	@Override
	public void stopEvent() {
		// TODO Auto-generated method stub
		//the final event here is that the score is shown
		showScore();
	}
	
	private void showScore(){
		//stop the thread
		_thread.exit();
		//give the control to the score intent, to be implemented
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if( _game == null )
			newSingleGame();
		if( _game.getStatus() == Status.STARTING )
		{
			
			_game.start();
			_thread.start();
			return;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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
	            //int counter = 0 ;
	            try {
	            	long currentTime = System.currentTimeMillis();
//					while ((line = reader.readLine()) != null /*&& counter < 1000*/ ) 
//					{
//					    String[] strings = line.split(" ");
//					    
//					    if( strings[0].length() < 2 )
//				    		continue;
//					    boolean result = _trie.insert(strings[0].trim());
//					    if ( result  == false ) 
//					    {
//					        Log.e(TAG, "unable to add word: " + strings[0].trim());
//					    }
//				    
//					    //counter++;
//					}
					
					//second more performant method
	            	String s = null;
	            	//String[] strings = null;
	            	
					while ( (inputStream.read( barray, 0, SIZE )) != -1 )
					{
						s = new String(barray);
						
//					    strings = s.split("\r\n");
//					    for( int i = 0 ; i < strings.length; ++i )
//					    {
//					    	if( strings[i].length() < 2 )
//					    		continue;
//					    	_trie.insert(strings[i]);
//					    }
					    
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
