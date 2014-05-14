package com.patrupopa.wordscocktail;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


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
	private ArrayList<LetterProb> alphabet;
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
		alphabet = new ArrayList<LetterProb>();
		generateBoard();
		goodWords = new ArrayList<String>();
		

		//TODO the size of the board could be variable, or 
		//you could set the time limit , or you could set the dictionary
		//setPreferences(preferences);
	}

	private void generateBoard() {
		// TODO Auto-generated method stub
		
		InputStream alphabet_str = 
				_context.getResources().openRawResource(R.raw.prob_alphapbet);
		BufferedReader alphabet_rdr = new BufferedReader(new InputStreamReader(alphabet_str));
		//populate the alphabet with the probabilities
		
		try {
			for( String line = alphabet_rdr.readLine();line != null; line=alphabet_rdr.readLine()) {
				
				String probabs[] = line.split(" ");
				LetterProb letter = new LetterProb(probabs[0]);
				
				for( int i = 1; i < probabs.length; ++i ) 
				{
					Integer probability = Integer.parseInt(probabs[i]);
					if( probability != null )
						letter.addProbability(probability);
				}
				alphabet.add(letter);
				
			}
		} catch (Exception e) {
			// 
		}
		
		//now generate the letters in random manner based on  the alphabet above
		int total = 0;
		Random rng = new Random();
		String b[] = new String[_boardSize];

		for( int i=0; i < alphabet.size(); ++i ) 
		{
			LetterProb lp = alphabet.get(i);
			if( lp != null )
			{
				int aux = lp.getTop();
				total += aux;
			}
		}

		// get the letters randomly based o the bigger probabilities
		for( int i = 0 ; i < _boardSize; ++i ) 
		{
			LetterProb letter = null;
			int remaining = rng.nextInt(total);
			
			for( int j=0 ; j < alphabet.size(); ++j ) 
			{
				letter = alphabet.get(j);
				int prob = letter.getTop();
				remaining -= prob;
				if ( prob > 0 && remaining <= 0) 
				{
					break;
				}
			}
			b[i] = letter.getLetter();
			total -= letter.removeTop();
			total += letter.getTop();
		}

		// now shuffle the letters random manner, by generating positions
		String aux = null;
		int newPos = 0;
		for( int pos = 0 ; pos < _boardSize; ++pos ) 
		{
			newPos = rng.nextInt(pos+1);
			aux = b[pos];
			b[pos] = b[newPos];
			b[newPos] = aux;
		}
		
		
		//String[] b = {"C","A","R","D","D","I","A","N","I","R","I","N","B","A","N","I"};
		
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
		alphabet = new ArrayList<LetterProb>();
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
