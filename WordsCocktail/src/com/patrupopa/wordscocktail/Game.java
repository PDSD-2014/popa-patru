package com.patrupopa.wordscocktail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;


import android.preference.PreferenceManager;
import android.util.Log;

import com.patrupopa.wordscocktail.Game.Status;


public class Game implements Counter {
	
	/*the status of the game*/
	public enum Status { 
		STARTING, 
		RUNNING, 
		PAUSED,
		FINISHED 
	}

	private static final String TAG = "Game.java";

	private Status _status;
	private Board _board;
	private int _wordCounter;
	private int _totalWords;
	private Context _context;
	private int _boardSize;
	
	private long _timeTicking;
	private int _maxTime;
	
	private ArrayList<String> goodWords;
	private ArrayList<String> _guessedWords;
	private ArrayList<LetterProb> alphabet;
	private long _startTime; 
	
	private int _currentScore ;
	String _foundWords ;
	String _badWords ;
	
	Dictionary _trie;
	
	
	//to change
	private final static int _timeLimit = 60;
	
	public Game(PlaySingleGame playSingleGame, Bundle bun) {
		Log.d("GAME", "Game(PlaySingleGame playSingleGame, Bundle bun)");
	}

	public Game(Context c, Dictionary trie) {
		Log.d("GAME", "Game(Context c, Dictionary trie)");
		
		_context = c;
		setStatus(Status.STARTING);
		
		setBoardSize(16);
		readPreferences(c);
		//setTimeLimit(_timeLimit);
		alphabet = new ArrayList<LetterProb>();
//		if( _trie == null)
//			loadDictionary();
		_trie = trie;
		generateBoard();
		goodWords = new ArrayList<String>();
		
		_wordCounter = -1;
		_totalWords = 0 ; 
		//computeMaxWordCount();
		increaseWordCounter();
		_currentScore = 0 ; 
		_foundWords  = "";
		_badWords  = "";
		_guessedWords = new ArrayList<String>();

		//TODO the size of the board could be variable, or 
		//you could set the time limit , or you could set the dictionary
		//setPreferences(preferences);
	}
	
	//this constructor is for when you want to restore game from preferences
public Game(Context c, SharedPreferences preferences, Dictionary trie) {
		Log.d("GAME", "Game(Context c, SharedPreferences preferences, Dictionary trie)");
	
		_context = c;
		setStatus(Status.STARTING);
		_trie = trie;
		setBoardSize(preferences.getInt("boardSize", 16));
		
		String [] board = preferences.getString("gameBoard", "").split(",");
		
		_board = new Board(board);
		
		_wordCounter = preferences.getInt("wordCount", 0);
		_totalWords =  preferences.getInt("totalWords", 0);
		_currentScore = preferences.getInt("score",0); 
		_timeTicking = preferences.getLong("timeTicking", 0);
		_startTime = preferences.getLong("startTime", 0);
		_maxTime = preferences.getInt("maxTime", 0);
		
		String test = preferences.getString("goodWords", "");
		String [] aux;
		goodWords = new ArrayList<String>();
		if(test != "" )
		{
			aux = test.split(",");
			
			for(int i = 0 ; i< aux.length ; i++ )
			{
				goodWords.add(aux[i]);
			}
		}
		test = preferences.getString("guessedWords", "");
		_guessedWords = new ArrayList<String>();
		if(test != "" )
		{
			aux = test.split(",");
			
			for(int i = 0 ; i< aux.length ; i++ )
			{
				_guessedWords.add(aux[i]);
			}
		}
		
		_foundWords = preferences.getString("words", null);
		_badWords = preferences.getString("badWords", null);
		
	}

	private void loadDictionary() {
	// TODO Auto-generated method stub
		Log.d(TAG, "Loading dictionary...");
        InputStream inputStream = _context.getResources().openRawResource(R.raw.infl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        _trie = new Dictionary();
        
   
        
        try {
            String line;
            try {
				while ((line = reader.readLine()) != null) 
				{
				    String[] strings = line.split(" ");
				    
				    if( strings[0].length() < 2 )
			    		continue;
				    boolean result = _trie.insert(strings[0].trim());
				    //out.write(strings[0].trim());
				    
				    if ( result  == false ) 
				    {
				        Log.e(TAG, "unable to add word: " + strings[0].trim());
				    }
			    
				}
				//out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } finally {
            try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        Log.d(TAG, "DONE loading words.");
	
}

	private void generateBoard() {
		// TODO Auto-generated method stub
		
		//end of comment
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
		
		
		if( b.length < getBoardSize())
		{
			return;
		}
		//uncomment this
		_board = new Board(b);
		
		//hard coded version, comment out!!!!!!!
//		String[] hc = {"A","D","O","D","H","A","A","N","I","R","I","N","B","A","N","I"};
//		_board = new Board(hc);
	}

	public Board getBoard() {
		return _board;
	}
	
	public void setBoard(String board) {
		_board.fromString(board);;
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

	public void increaseWordCounter() {
		// TODO Auto-generated method stub
		
		_wordCounter++;
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
		return _wordCounter + "";
	}
	public String getMaxWordCount() {
		return _totalWords + "";
	}
	
	
	private void computeMaxWordCount() {
		// TODO Auto-generated method stub
		//how many words could you form with this grid ??
		for ( int i = 0; i < 16; i++ )
		{
			LinkedList<Pair> visited = new LinkedList<Pair>();
			Pair p = new Pair(i/4, i%4);
			visited.add(p);
			for (int  j = 0 ; j < 16 ; j++)
			{
				if( i == j )
					continue;
				Pair endNode = new Pair(j/4, j%4);
				BreadthFirst(visited, endNode);	
			}
		}
		
	}

	//receives a string and checks whether or not this is a word from the dictionary
	public boolean goodWord(String result) {
		// TODO Auto-generated method stub
		if( _trie == null || result.length() < 2 )
		{
			return false;
		}
		return _trie.search(result);
	}

	//visited will initially contain first node, the start node
	private void BreadthFirst(LinkedList<Pair> visited , Pair endNode) 
	{
		if( visited.size() > 5 )
			return;
	    Pair last = visited.getLast();
		LinkedList<Pair> nodes = adjacentNodes(last);
		int size = nodes.size();
	    // Examine adjacent nodes only if visited is smaller than
		
		    for(Pair node : nodes) 
		    {
		        if (visited.contains(node))
		        {
		            continue;
		        }
	
		        if ( node.equals(endNode)  )
		        {
		            visited.addLast(node);
	
		            printPath(visited);
	
		            visited.removeLast();
	
		            break;
		         }
		     }
		
	    // In breadth-first, recursion needs to come after visiting adjacent nodes
	    for (Pair node : nodes)
	    {      
	        if( visited.contains(node) || node.equals(endNode) ||  visited.size() > 5 )
	        {
	            continue;
	        }

	        visited.addLast(node);

	        // Recursion
	        BreadthFirst(visited,endNode);

	        visited.removeLast();
	    }
	}

	private void printPath(LinkedList<Pair> visited) {
		// TODO Auto-generated method stub
		String path = "";
		Log.d(TAG, "In printing path.........");
		for (Pair node: visited)
		{
			int x  = node.GetX();
			int y = node.GetY();
			if (x >= 0 && x < 4 && y >= 0 && y < 4 )
			{
				path += _board.getElementAt(node.GetX(), node.GetY());
				path += "";
			}
			else 
			{
				Log.d(TAG, "Caution x or y is out of limits");
			}
		}
		if(goodWord(path.toLowerCase()))
		{
			_totalWords++;
			goodWords.add(path);
		}
		Log.d(TAG, path);
		
	}

	//will return maximum 8 neighbours
	private LinkedList<Pair> adjacentNodes(Pair last) {
		// TODO Auto-generated method stub
		LinkedList<Pair> result = new LinkedList<Pair>(); 
		int x = last.GetX();
		int y = last.GetY();
		if( x - 1 >= 0  )
		{
			Pair p = new Pair(x-1,y);
			result.add(p);
			
			if( y-1 >= 0 )
			{
				p = new Pair(x-1,y-1);
				result.add(p);
				
			}
			if ( y+1 < 4 )
			{
				p = new Pair(x-1,y+1);
				result.add(p);
				
			}
		}
		if( x + 1 < 4  )
		{
			Pair p = new Pair(x+1,y);
			result.add(p);
			if( y-1 >= 0 )
			{
				p = new Pair(x+1,y-1);
				result.add(p);
				
			}
			if ( y+1 < 4 )
			{
				p = new Pair(x+1,y+1);
				result.add(p);
				
			}
		}

		if( y+1 < 4  )
		{
			Pair p = new Pair(x,y+1);
			result.add(p);
		}
		if( y-1 >= 0  )
		{
			Pair p = new Pair(x,y-1);
			result.add(p);
			
		}
		return result;
	}
	
	public int getCurrentScore()
	{
		return _currentScore;
	}
	
	public void incrementScore(int length)
	{
		_currentScore += length*length;
	}
	
	//saving game from Bundle
	public void save(Bundle bun) {

		bun.putString("words",_foundWords);
		bun.putString("badWords",_badWords);
		bun.putInt("wordCount",_wordCounter);
		bun.putInt("score",_currentScore);
	}
	//saving game from preferences editor, to databasae, in case of onPause()
	public void save(SharedPreferences.Editor editor) {

		String [] aux = _board.getBoard();
		String b = "";
		for(int i = 0 ; i < aux.length; i ++ )
		{
			b += aux[i] + ",";
		}
		editor.putString("gameBoard",b);
		editor.putInt("wordCount",_wordCounter);
		editor.putInt("totalWords",_totalWords);
		editor.putInt("boardSize",_board.getSize());
		editor.putLong("timeTicking",_timeTicking);
		editor.putLong("startTime",_startTime);
		editor.putInt("maxTime", (int)(_timeTicking *1000));
		editor.putString("words",_foundWords);
		editor.putString("badWords",_badWords);
		editor.putInt("score",_currentScore);
		b = "";
		for(int i = 0 ; i < goodWords.size() ; i++)
			b += goodWords.get(i) + ",";
		editor.putString("goodWords", b );
		b = "";
		for(int i = 0 ; i < _guessedWords.size() ; i++)
			b += _guessedWords.get(i) + ",";
		editor.putString("guessedWords",b  );
		editor.commit();
	}

	
	public void appendToWordsFound(String toAppend)
	{
		_foundWords += toAppend + "\n";
		_guessedWords.add(toAppend);
		
	}
	public void appendToBadWords(String toAppend)
	{
		if( toAppend.length() > 0 )
			_badWords += toAppend + "\n";
	}
	
	public boolean isAlready(String word)
	{
		return _guessedWords.contains(word);
	}
	
	public void pause() 
	{
		if( _status == Game.Status.RUNNING )
		{
			_status = Game.Status.PAUSED; 
		}
	}
	
	private void readPreferences(Context c) {
		SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences(c);

		int aux = Integer.parseInt(
			prefs.getString("timeLimit","60"));
		setTimeLimit(aux);
	}
}
