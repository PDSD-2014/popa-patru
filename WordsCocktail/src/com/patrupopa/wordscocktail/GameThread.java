package com.patrupopa.wordscocktail;

import java.util.LinkedList;
import java.util.ListIterator;

import android.os.Handler;

public class GameThread implements Runnable{

	private boolean _stop ;
	private LinkedList<Worker> _workers;
	private Stopper _stopper;
	private Counter _counter;
	private Handler _handler;
	private long _delay;
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if( _stop == true )
		{
			return;
		}
		if( _counter == null )
			return;
		
		int time = _counter.timer();
		
		if( _workers == null )
			return;
		
		ListIterator<Worker> iter = _workers.listIterator();

		while(iter.hasNext()) {
			Worker w = iter.next();
			w.timer(time);
		}

		if ( time < 0 ) {
			if( _stopper != null) {
				_stopper.stopEvent();
			}
		}

		_handler.postDelayed(this,_delay);
		
	}
	
	public void start(){
		_stop = false;
		_handler.postDelayed(this, _delay);
	}
	public GameThread()
	{
		_workers = new LinkedList<Worker>();
		_stop = false;
		_handler = new Handler();
		_delay = 100;//these are milliseconds
		
	}
	public void exit() {
		// TODO Auto-generated method stub

		_stop = true;		
	}
	
	public void addWorker(Worker w) {
		// TODO Auto-generated method stub
		_workers.add(w);
	}
	public void setCounter(Counter cnt) {
		// TODO Auto-generated method stub
		_counter = cnt;
		
	}
	public void setStopper(Stopper s) {
		// TODO Auto-generated method stub
		_stopper = s;
		
	}

}
