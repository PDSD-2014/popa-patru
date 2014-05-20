package com.patrupopa.wordscocktail;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//sets the main view, always the one with choice screen
		setContentView(R.layout.choice_screen);

		// multiplayer
		Button b = (Button) findViewById(R.id.multiplayer);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent("com.popapatru.wordscocktail.action.MULTIPLAYER_GAME"));
			}
		});
		
		// single player
		b = (Button) findViewById(R.id.one_player);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent("com.popapatru.wordscocktail.action.NEW_GAME"));
			}
		});
		
		// instructions
		b = (Button) findViewById(R.id.about);
		try
		{
			b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					//setContentView(R.layout.instructions);
					startActivity(new Intent("com.popapatru.wordscocktail.action.INSTR"));
				}
			});
			
		}catch (Exception e) {
			// 
		}
		
		//preferences
		b = (Button) findViewById(R.id.preferences);
		try
		{
			b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					//setContentView(R.layout.instructions);
					startActivity(new Intent("com.popapatru.wordscocktail.action.SETTINGS"));
				}
			});
			
		}catch (Exception e) {
			// 
		}
		
		//restore_game
		if( savedGame() ) 
		{
			b = (Button) findViewById(R.id.restore_game);
			b.setEnabled(true);
			b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(savedGame()) 
					{
						startActivity(new 
							Intent("com.popapatru.wordscocktail.action.RESTORE_GAME"));
					} else {
						
				
					}
				}
			});
		}
		else
		{
			b = (Button) findViewById(R.id.restore_game);
			b.setEnabled(false);
		}
//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if ( id == R.id.save_game ) 
		{
			return true;
		}
		
		if ( id == R.id.end_game ) 
		{
			return true;
		}
		
		if ( id == R.id.rotate_board ) 
		{
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.choice_screen, container,
					false);
			return rootView;
		}
	}
	
	public boolean savedGame() {
		Resources res = getResources();
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",
			MODE_PRIVATE);
		boolean yes = (prefs.getInt("boardSize",0) == 16);
		return yes;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//restore_game
		if( savedGame() ) 
		{
			Button b = (Button) findViewById(R.id.restore_game);
			b.setEnabled(true);
			b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(savedGame()) 
					{
						startActivity(new 
							Intent("com.popapatru.wordscocktail.action.RESTORE_GAME"));
					} else {
						
				
					}
				}
			});
		}
		else
		{
			Button b = (Button) findViewById(R.id.restore_game);
			b.setEnabled(false);
		}
	}

}
