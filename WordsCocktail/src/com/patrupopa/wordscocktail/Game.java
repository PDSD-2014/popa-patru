package com.patrupopa.wordscocktail;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.patrupopa.wordscocktail.Game.Status;

public class Game implements Counter {
	
	/*the status of the game*/
	public enum Status { 
		STARTING, 
		RUNNING, 
		PAUSED,
		FINISHED 
	}

	private Status _status;
	private Board _board;
	private int _wordCounter;
	private Context _context;
	private int _boardSize;
	
	private long _timeTicking;
	private int _maxTime;
	
	private int _minWordLength;
	private ArrayList<String> goodWords;
	private long _startTime; 

	public Game(PlaySingleGame playSingleGame, Bundle bun) {
		// TODO Auto-generated constructor stub
	}

	public Game(Context c) {
		// TODO Auto-generated constructor stub
		_context = c;
		setStatus(Status.STARTING);
		setWordCounter(0);
		setBoardSize(16);
		setMinWordLength(3);
		setTimeLimit(60);
		generateBoard();
		goodWords = new ArrayList<String>();

		//TODO the size of the board could be variable, or 
		//you could set the time limit , or you could set the dictionary
		//setPreferences(preferences);
	}

	private void generateBoard() {
		// TODO Auto-generated method stub
		String[] b = {"C","A","R","D","D","I","A","N","I","R","I","N","B","A","N","I"};
		if( b.length < getBoardSize())
		{
			return;
		}
		_board = new Board(b);
	}

	public Board getBoard()
	{
		
		return _board;
	}
	public Game(Context c, SharedPreferences preferences) {
		
		_context = c;
		setStatus(Status.STARTING);
		setWordCounter(0);
		setBoardSize(16);
		setMinWordLength(3);
		//in seconds
		setTimeLimit(60);
		generateBoard();
		goodWords = new ArrayList<String>();
		//TODO the size of the board could be variable, or 
		//you could set the time limit , or you could set the dictionary
		//setPreferences(preferences);
	}
	
	private void setMinWordLength(int i) {
		// TODO Auto-generated method stub
		_minWordLength = i;
	}

	//milliseconds
	private void setTimeLimit(int i) {
		// TODO Auto-generated method stub
		_timeTicking = i;
		//this is in milliseconds
		_maxTime = i*1000;
	}

	private void setBoardSize(int i) {
		// TODO Auto-generated method stub
		_boardSize = i;
		
	}

	public int getBoardSize() {
		// TODO Auto-generated method stub
		return _boardSize;
		
	}

	private void setWordCounter(int i) {
		// TODO Auto-generated method stub
		
		_wordCounter = i;
	}

	public void setStatus(Status status)
	{
		_status = status;
		
	}
	public void rotateBoard() {
		// TODO Auto-generated method stub
		
	}

	public void endNow() {
		// TODO Auto-generated method stub
		
	}

	public Status getStatus() {
		// TODO Auto-generated method stub
		return _status;
	}

	@Override
	public int timer() {
		// TODO Auto-generated method stub
		_timeTicking--;
		if(_timeTicking < 0 )
		{
			//reset timeTicking
			_timeTicking = 0;
			//reset status of current game thread
			_status = Status.FINISHED;
		}
		else
		{
			long currentTime = System.currentTimeMillis();
			long milliseconds = ( currentTime - _startTime );
			//do not forget to convert back to seconds
			_timeTicking = ((_maxTime - milliseconds)/1000);
		}
		return (int)_timeTicking;
	};

	/*this method is invoked in order to start this session of the game*/
	public void start()
	{
		if( _status != Status.STARTING ) {
			return;
		}

		//when starting change the status to RUNNING
		_status = Status.RUNNING;
		_startTime = System.currentTimeMillis();
		
	}

	public String getWordCount() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMaxWordCount() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean goodWord(String result) {
		// TODO Auto-generated method stub
		return true;
	}

	
}
