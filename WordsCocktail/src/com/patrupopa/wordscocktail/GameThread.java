package com.patrupopa.wordscocktail;

import java.util.LinkedList;

import android.R.bool;

public class GameThread implements Runnable{

	private boolean _stop ;
	private LinkedList<Worker> workers;
	private Stopper _stopper;
	private Counter _counter;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		_stop = false;
		
	}
	public void start(){
		_stop = false;
	}
	public GameThread()
	{
		workers = new LinkedList<>();
		_stop = false;
		
	}
	public void exit() {
		// TODO Auto-generated method stub
		_stop = true;		
	}
	
	public void addWorker(Worker w) {
		// TODO Auto-generated method stub
		workers.add(w);
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
