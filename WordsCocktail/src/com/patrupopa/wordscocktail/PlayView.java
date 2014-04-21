package com.patrupopa.wordscocktail;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;


public class PlayView extends View implements Worker {

	//private variables	
	private Game _game;
	private Paint _paint;
	private int _remainingTime;
	private int _initialTime;
	private float _width, _height;
	private float PADDING = 10;

	public PlayView( Context context , Game game) {
		super(context);
		// TODO Auto-generated constructor stub
		_game = game;
		_remainingTime = 0 ; 
		//instantiate paint class
		_paint  = new Paint();
	}

	@Override
	public void timer(int t) {
		// TODO Auto-generated method stub
		
		
	}
	
	/*this draws the field of the actual one player game*/
	@Override
	public void onDraw(Canvas canvas) 
	{
		
		//black canvas 
		canvas.drawRGB(0x00,0x00,0x00);

		if( _game.getStatus() != Game.Status.RUNNING) 
			return;

		showGrid(canvas);
		showTimer(canvas);
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

		int secRemaining = _remainingTime / 100;
		int mins = secRemaining / 60;
		int secs = secRemaining % 60;

		String time = "" + mins + ":";
		if( secs < 10 ) 
		{
			time += "0"+ (secRemaining % 60);
		} 
		else 
		{
			time += ""+ (secRemaining % 60);
		}

		color.setTextSize(15);
		canvas.drawText("Time:",PADDING,getMeasuredHeight() - 2*PADDING,color);
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
		_lat = _lat - PADDING;
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
			canvas.drawLine(PADDING , i * distance - 3 , _lat , i * distance - 3 , dark);
			canvas.drawLine(PADDING , i * distance - 1.5f , _lat , i * distance - 1.5f , orange);
			canvas.drawLine(PADDING , i * distance , _lat , i * distance , green);
			
			//these are the vertical lines
			canvas.drawLine( i * distance -3 , PADDING , i * distance -3, _lat  , dark);
			canvas.drawLine( i * distance -1.5f , PADDING , i * distance -1.5f, _lat  , orange);
			canvas.drawLine(i * distance, PADDING , i * distance, _lat , green );			
		}

		//now draw the string in every cell of the grid
		dark.setTextAlign(Paint.Align.CENTER);
		dark.setTextSize(distance-15);
		//AssetManager manager = getContext().getAssets();
//		Typeface font = Typeface.createFromAsset(getContext().getAssets(), 
//				"fonts/Cookie.ttf");
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), 
				"fonts/Roboto.ttf");
		dark.setTypeface(font);
		for ( int i = 0; i < _baux.getSize(); ++i ) 
		{
			String letter = _game.getBoard().getElementAt(i);
			int x = i%_baux.getHeight();
			int y = i/_baux.getHeight();
			canvas.drawText(letter, x*distance + distance/2 , y*distance + distance/2 + 2*PADDING ,dark);
		}
		
		
		//TODO ,implement finger tracker , color them in green
	}
	

}
