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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;

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
     	Log.d(TAG, "here");
     	if(savedInstanceState != null) {
			try {
				restoreGame(savedInstanceState);
			} catch (Exception e) {
				
			}
			return;
		}
		setContentView(R.layout.loading);
		try {
			System.gc();
			loadDictionary();
			String action = getIntent().getAction();
			if(action.equals("com.popapatru.wordscocktail.action.RESTORE_GAME")) 
			{
				restoreGame();
			} else if(action.equals("com.popapatru.wordscocktail.action.NEW_GAME")) {
				
				newSingleGame();
				Log.d(TAG, "after new single game");
			} else {
				
			}
		} catch (Exception e) {
			
		}
    }
    private void restoreGame() {
		// TODO Auto-generated method stub
    	Resources res = getResources();
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",
			this.MODE_PRIVATE);

		_game = new Game(this,prefs,_trie);
		
		clearSavedGame();

		restoreGame(_game);
		
	}
		// TODO Auto-generated method stub
	private void clearSavedGame() {
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",
			this.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("boardSize" , 0 );
		editor.commit();

	}
		
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
    	// TODO Auto-generated method stub
    	super.onSaveInstanceState(outState);
    	//saveGame(outState);
    	
    }
    //ths is auxiliary for restore game
    private void saveGame(Bundle state) {
		if( _game.getStatus() == Game.Status.RUNNING ) 
		{
			// Log.d(TAG,"Saving");
			_game.pause();
			_game.save(state);
		}
	}
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	saveGame();
		_thread.exit();
		_game.pause();
		super.onPause();
		
		
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
				//_game.endNow();
				_thread.exit();
				finish();
		}
		return true;
	}

	private void saveGame() {
		// TODO Auto-generated method stub
		if( _game.getStatus() == Game.Status.RUNNING) 
		{
			SharedPreferences prefs = getSharedPreferences(
				 "prefs_game_file",this.MODE_PRIVATE);
			_game.save(prefs.edit());
		}
		
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

		PlayView pv = new PlayView(this,_game2);

		if( _thread != null) {
			_thread.exit();
		}
		_thread = new GameThread();
		_thread.setCounter(_game);
		_thread.addWorker(pv);
		_thread.setStopper(this);

		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(this.getWindow().getAttributes()
				.width, this.getWindow().getAttributes()
				.height);
		setContentView( pv , lp);
		pv.setKeepScreenOn(true);
		
	}

	private void newSingleGame() {
		//make new instance of  the game
		_game = new Game(this, _trie);

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
		//the final event here is that the score is shown
		showScore();
	}
	
	private void showScore(){
		//stop the thread
		_thread.exit();
		//give the control to the score intent, to be implemented
		Bundle bun = new Bundle();
		_game.save(bun);
		Intent scoreIntent = new Intent("com.popapatru.wordscocktail.action.FINAL_SCORE");
		
		//and then will get it from here
		scoreIntent.putExtras(bun);

		startActivity(scoreIntent);
		_game.setStatus(Game.Status.FINISHED);
		finish();
	}
	
	@Override
	protected void onResume() {
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
		super.onDestroy();
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
					//second more performant method
	            	String s = null;
	            	//remove false from here!!!!!!
					while ( (inputStream.read( barray, 0, SIZE )) != -1 )
					{    
					    int pos = 0, end;
					    String aux = null;
			            while ((end = s.indexOf("\r\n", pos)) >= 0) {
			                aux = s.substring(pos,end).trim();
			            	int length = aux.length(); 
			            	if( length > 1 && length <= 6 )
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

}
