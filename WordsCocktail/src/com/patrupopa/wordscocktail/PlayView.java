package com.patrupopa.wordscocktail;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;


public class PlayView extends View implements Worker {

	//private variables	
	private Game _game;
	private int _remainingTime;
	private int _initialTime;
	private float _width, _height;
	private float PADDING = 10;
	private FingerTouch _fingerTouch;
	private ArrayList<Integer> _goodCells;
	private int _redrawFreq;
	public static final int REDRAW_DELAY = 15;

	public PlayView( Context context , Game game) {
		super(context);
		// TODO Auto-generated constructor stub
		_game = game;
		_remainingTime = 0 ; 
		_goodCells = new ArrayList<Integer>();
		_redrawFreq = REDRAW_DELAY;
		
		
	}

	@Override
	public void timer(int t) {
		// TODO Auto-generated method stub
		_remainingTime = t;
		_redrawFreq--;
		if( _redrawFreq < 0 )
		{
			//reset frequency of updated screen
			_redrawFreq = REDRAW_DELAY;
			invalidate();
		}
		
	}
	
	/*this draws the field of the actual one player game*/
	@Override
	public void onDraw(Canvas canvas) 
	{
		
		//black canvas 
		canvas.drawRGB(0x00,0x00,0x00);

		if( _game.getStatus() != Game.Status.RUNNING) 
			return;
		
		
		//this draws the grid on which the player has to match words
		showGrid(canvas);
		//highlight the final word
		highlight(canvas);
		//draw letters in board
		drawStrings(canvas);
		//this shows how much time the player has left
		showTimer(canvas);
		//this shows  the number of dictionary words the player has found  until
		//current time
		showWordCount(canvas);
		//draw the score also
		showScore(canvas);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//check the class the keeps track of finger movement
		//to be instantiated
		if( _fingerTouch == null )
			return false;
		int touchAction = event.getAction();
		switch(touchAction)
		{
		//TODO
		case MotionEvent.ACTION_DOWN :
			
		case MotionEvent.ACTION_MOVE:
			_fingerTouch.Touching(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			//get the resulting word
			String result = _fingerTouch.NotTouching(event.getX(), event.getY());
			if( result == null )
				break;
			if( _game.goodWord(result) == true )
			{
				_goodCells.addAll(_fingerTouch.getPositions());
				invalidate();
				
			}
			_fingerTouch.reset();
			break;
		}
		
		//invalidate();
		
		return true;
	}
	
	private void highlight(Canvas canvas)
	{
		//draw them , and then make sure you delete it
		Paint green = new Paint();
		green.setARGB(255	,0,255,0);
		float _lat = Math.min( getMeasuredHeight(), getMeasuredWidth() );
		_lat = _lat - 2*PADDING;
		float distance = _lat/4f;
		float _paddingX = 0f;
		float _paddingY = 0f;
		for ( int i = 0; i < _goodCells.size() ; ++i ) 
		{
			int val = _goodCells.get(i);
			int x = val%_game.getBoard().getHeight();
			int y = val/_game.getBoard().getHeight();
			if( y == 0 )
			{
				_paddingX  = PADDING;
			}
			else 
			{
				_paddingX = PADDING;
			}
			if( x == 0 )
			{
				_paddingY  = PADDING;
			}
			else 
			{
				_paddingY = PADDING;
			}
			float left = distance*x + _paddingX;
			float top = distance*y + _paddingY;
			float right = distance*(x+1) - 3f ;
			float bottom = distance*(y+1) -3f ;
			canvas.drawRect(left, top, right, bottom, green);
		}
		_goodCells.clear();
	}
	private void showScore(Canvas canvas) {
		// TODO Auto-generated method stub
		if(_game == null )
		{
			return;
		}
		Paint color = new Paint();
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), 
				"fonts/Roboto.ttf");
		color.setTypeface(font);
		color.setARGB(255,255,255,255);
		color.setTextSize(15);
		
		canvas.drawText("Score:",12*PADDING,getMeasuredHeight() - 3*PADDING,color);
		canvas.drawText(_game.getWordCount() + "/" + _game.getMaxWordCount(),
				12*PADDING,getMeasuredHeight() - PADDING,color);
	}

	private void showWordCount(Canvas canvas) {

		// TODO Auto-generated method stub
		if(_game == null )
		{
			return;
		}
		Paint color = new Paint();
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), 
				"fonts/Roboto.ttf");
		color.setTypeface(font);
		color.setARGB(255,255,255,255);
		color.setTextSize(15);
		
		
		canvas.drawText("Words:",5*PADDING,getMeasuredHeight() - 3*PADDING,color);
		canvas.drawText( _game.getWordCount()+"/" + _game.getMaxWordCount(),
				5*PADDING,getMeasuredHeight() - PADDING,color);		
		
	}

	private void showTimer(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint color = new Paint();
		if( _remainingTime < 1000 ) 
		{
			color.setARGB(255,255,0,0);
		} 
		else if ( _remainingTime < 3000 ) 
		{
			color.setARGB(255,255,140,0);
		} 
		else 
		{
			color.setARGB(255,255,255,255);
		}

		
		int mins = _remainingTime / 60;
		int secs = _remainingTime % 60;

		String time = "" + mins + ":"+ secs;
		color.setTextSize(15);
		canvas.drawText("Time:",PADDING,getMeasuredHeight() - 3*PADDING,color);
		canvas.drawText(time,PADDING,getMeasuredHeight() - PADDING,color);
		
	}

	private void showGrid(Canvas canvas) {
		// TODO Auto-generated method stub
		if( _game == null )
		{
			return;
		}
		// Draw the background...
		Paint background = new Paint();
		background.setARGB( 255,255, 255, 255 );
		//let s draw a square actually
		Board _baux = _game.getBoard();
		if( _baux == null )
		{
			return;
		}
		float _lat = Math.min( getMeasuredHeight(), getMeasuredWidth() );
		_lat = _lat - 2*PADDING;
		int top = getPaddingTop();
		int left = getPaddingLeft();
		int right = getPaddingRight();
		int bottom = getPaddingBottom();
		canvas.drawRect( PADDING , PADDING , _lat , _lat , background);
		
	
		// Define the 3 colors for the grid lines  
		
		Paint dark = new Paint();
		dark.setARGB(255,0,0,0);
		
		Paint green = new Paint();
		green.setARGB(255,0,255,0);
		
		Paint orange = new Paint();
		orange.setARGB(255,255,165,0);
		
		float distance = _lat/4f;
		
		// Draw the grid lines
		for ( int i = 1; i < _baux.getHeight(); ++i ) 
		{
			//these are the horizontal lines
			canvas.drawLine(PADDING , i * distance , _lat , i * distance  , dark);
			canvas.drawLine(PADDING , i * distance + 1.5f , _lat , i * distance + 1.5f , orange);
			canvas.drawLine(PADDING , i * distance + 3f , _lat , i * distance + 3f , green);
			
			//these are the vertical lines
			canvas.drawLine( i * distance , PADDING , i * distance , _lat  , dark);
			canvas.drawLine( i * distance + 1.5f , PADDING , i * distance + 1.5f, _lat  , orange);
			canvas.drawLine(i * distance +3f , PADDING , i * distance + 3f, _lat , green );			
		}

		//after drawing the board , let s instantiate the FingerTouch class
		//the finger touch receives the left margin and the right margin, and the width
		//and height of a cell 
		_fingerTouch = new FingerTouch(PADDING, PADDING, _lat, _lat,_baux.getWidth(), _baux.getHeight(),_game);
		
		
		//TODO ,implement finger tracker , color them in green
	}
	private void drawStrings(Canvas canvas)
	{
		//now draw the string in every cell of the grid
		Paint dark = new Paint();
		dark.setARGB(255,0,0,0);
		dark.setTextAlign(Paint.Align.CENTER);
		float cellWidth = (Math.min( getMeasuredHeight(), getMeasuredWidth() ) - PADDING);
		cellWidth = cellWidth/(float)_game.getBoard().getHeight();	
		dark.setTextSize(cellWidth-15);
		//AssetManager manager = getContext().getAssets();
		//				Typeface font = Typeface.createFromAsset(getContext().getAssets(), 
		//						"fonts/Cookie.ttf");
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), 
				"fonts/Roboto.ttf");
		dark.setTypeface(font);
		for ( int i = 0; i < _game.getBoard().getSize(); ++i ) 
		{
			String letter = _game.getBoard().getElementAt(i);
			int x = i%_game.getBoard().getHeight();
			int y = i/_game.getBoard().getHeight();
			canvas.drawText(letter, x*cellWidth + cellWidth/2 , y*cellWidth + 
					cellWidth/2 + 2*PADDING ,dark);
		}
	}
	

}
