package com.patrupopa.wordscocktail;

public class Pair {

	private int _x ;
	private int _y ;
	public Pair(int x, int y)
	{
		_x = x;
		_y = y;
	}
	public int GetX()
	{
		return _x;
	}
	public void SetX(int x)
	{
		_x = x;
	}
	
	public int GetY()
	{
		return _y;
	}
	public void SetY(int y)
	{
		_y = y;
	}
	public boolean equals(Object o) {
		if( ((Pair) o).GetX() == this._x && ((Pair) o).GetY() == this._y)
			return true;
		return false;
		
	};	
	
}

