package com.patrupopa.wordscocktail;

import java.util.ArrayList;

public class LetterProb {
	private String _letter;
	private ArrayList<Integer> _probabilities;
	public LetterProb(String letter)
	{
		_letter = letter;
		_probabilities = new ArrayList<Integer>();
	}

	public void addProbability(Integer prob)
	{
		_probabilities.add(prob);
	}
	public void addProbabilities(ArrayList<Integer> probs)
	{
		_probabilities.addAll(probs);
	}
	
	public int getTop()
	{
		return _probabilities.get(0).intValue();
	}
	
	public int removeTop()
	{
		return _probabilities.remove(0).intValue();
	}
	public String getLetter()
	{
		return _letter;
	}
	
	
}
