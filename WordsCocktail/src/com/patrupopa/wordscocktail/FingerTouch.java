package com.patrupopa.wordscocktail;

import java.util.ArrayList;

import android.util.Log;

public class FingerTouch {
	private float  _left;
	private float _top;
	private float _right;
	private float _bottom;
	private int _width;
	private int _height;
	private ArrayList<Integer> _positions;
	private Game _game;
	
	public FingerTouch(float left, float top, float right, float bottom,
			int width, int height, Game g) {
		_left = left;
		_top = top;
		_bottom = bottom;
		_right = right;
		_width = width;
		_height = height;
		_game = g;
		_positions= new ArrayList<Integer>();
	}
	
	public void Touching( float x , float y )
	{
		if( x < _left || x > _right || x < _top || x > _bottom )
			return;
		int row = detectRow(y);
		int col = detectColumn(x);
		int value = row*_width + col;
		if( _positions.contains(value) == false )
		{
			_positions.add(new Integer(value));
		}
	}

	private int detectColumn(float x) {
		float colWidth = ( _right - _left)/_width;
		return (int) ((x-_left )/colWidth);
	}

	private int detectRow(float y)
	{
		float rowHeight = (_bottom - _top)/_height;
		return (int) ((y-_top )/rowHeight);
	}

	public String NotTouching(float x , float y) {
		if( x < _left || x > _right || x < _top || x > _bottom )
			return null;
		if(_game == null )
			return null;
		if(_game.getBoard() == null )
			return null;
		
		String word="";
		for (int i = 0; i < _positions.size(); i++)
		{
			word += _game.getBoard().getElementAt(_positions.get(i));
		}
		
		return word;
	}
	
	public ArrayList<Integer> getPositions() {
		return _positions;
	}
	
	public void reset() {
		_positions.clear();
	}
	

}
