package com.patrupopa.wordscocktail;

import java.util.HashMap;

//Dictionary implemented using a Trie Tree.
public class Dictionary {
	private HashMap<Character,Node> roots = new HashMap<Character,Node>();
	
	/**
	 * Search through the dictionary for a word.
	 * @param string The word to search for.
	 * @return Whether or not the word exists in the dictionary.
	 */
	public boolean search(String string) 
	{
		Character key = Character.valueOf(string.charAt(0));
		boolean contains =roots.containsKey(key);
		contains =roots.containsKey(string.charAt(0));
		if (roots.containsKey(string.charAt(0))) 
		{
			if ( string.length() == 1 && roots.get(string.charAt(0)).endOfWord) {
				return true;
			}
			boolean variable = searchFor(string.substring(1),roots.get(string.charAt(0)));
					
			return variable;
		} 
		else 
		{
			return false;
		}	
	}
	
	/**
	 * Insert a word into the dictionary.
	 * @param string The word to insert.
	 */
	public boolean insert(String string) 
	{
		Character key = Character.valueOf(string.charAt(0));
		boolean contains = roots.containsKey(key);
		
		if ( contains == false ) 
		{
			roots.put(key , new Node());
		}
		
		return insertWord(string.substring(1),roots.get(key));
		
	}
	
	//Recursive method that inserts a new word into the trie tree.
	private boolean insertWord(String string, Node node) {
		final Node nextChild;
		Character key = Character.valueOf(string.charAt(0));
		boolean contains = node.children.containsKey(key);
		
		if ( contains == true ) 
		{
			nextChild = node.children.get(key);
		} else 
		{
			nextChild = new Node();
			node.children.put(key, nextChild);
		}
				
		if (string.length() == 1) 
		{
			nextChild.endOfWord = true;
			return true;
		} 
		else 
		{
			insertWord(string.substring(1),nextChild);
		}
		return true;
	}
	

	//Recursive method that searches through the Trie Tree to find the value.
	private boolean searchFor(String string, Node node) {
		if ( string.length() == 0 )  
		{
			if (node.endOfWord) 
			{
				return true;
			} else 
			{
				return false;
			}
		}
		
		if (node.children.containsKey(string.charAt(0))) 
		{
			return searchFor(string.substring(1),node.children.get(string.charAt(0)));
		} 
		else 
		{
			return false;
		}
	}
}