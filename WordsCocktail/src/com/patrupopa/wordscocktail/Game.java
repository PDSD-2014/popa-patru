package com.patrupopa.wordscocktail;

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
	private int _timeLimit;
	private int _minWordLength;

	
	public Game(PlaySingleGame playSingleGame, Bundle bun) {
		// TODO Auto-generated constructor stub
	}

	public Game(Context c) {
		// TODO Auto-generated constructor stub
		_context = c;
		setStatus(Status.RUNNING);
		setWordCounter(0);
		setBoardSize(16);
		setMinWordLength(3);
		setTimeLimit(60);
		generateBoard();
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
		setStatus(Status.RUNNING);
		setWordCounter(0);
		setBoardSize(16);
		setMinWordLength(3);
		setTimeLimit(60);
		//TODO the size of the board could be variable, or 
		//you could set the time limit , or you could set the dictionary
		//setPreferences(preferences);
	}
	
	private void setMinWordLength(int i) {
		// TODO Auto-generated method stub
		_minWordLength = i;
	}

	private void setTimeLimit(int i) {
		// TODO Auto-generated method stub
		_timeLimit = i;
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
		return 0;
	};
	
	public void start()
	{
		if( _status != Status.STARTING ) {
			return;
		}

		//_start = new Date();
		_status = Status.RUNNING;
	}

}
