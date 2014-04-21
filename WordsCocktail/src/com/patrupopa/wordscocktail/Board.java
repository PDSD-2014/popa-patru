package com.patrupopa.wordscocktail;

public class Board {
	
	//private variables
	private int _size;
	private int _width;
	private int _height;
	private String[] _board;
	
	public Board(String [] board)
	{
		_board = board;
		_width = 4;
		_height = 4;
		_size = _width*_height;
		
	}
	
	public int getWidth()
	{
		
		return _width;
	}
	
	public int getHeight()
	{
		return _height;
		
	}
	public int getSize()
	{
		return _size;
	}

	public String getElementAt(int i)
	{
		
		return _board[i];
	}
	
	public String getElementAt(int x , int y)
	{
		//TODO
		return null;
		
	}
}
