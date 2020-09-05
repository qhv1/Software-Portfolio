import java.io.*;
import java.util.*;
public class DLB implements DictInterface
{
	private Node root;

	public DLB()
	{
		root = new Node((char)0);
	}

	public boolean add(String theWord)
	{
		//check to see if an empty string was inputted, which should not be added
		if(theWord.equals(""))
		{
			return false;
		}
		boolean isAdded = false;
		String word = theWord;
		int wordPos = 0;
		Node theNode = root;
		//if the the root wasn't initalized yet, set root to equal first letter of string
		if(theNode.getValue() == 0)
		{
			theNode.setValue(word.charAt(0));
			//System.out.println(word.charAt(0) + " added");
		}
		while(true)
		{
			if(theNode.getValue() == word.charAt(wordPos))
			{
				//if the wordPos is equal to the word length - 1, then we
				//are at the end of the string. Now we need to see if we add
				//it as a child or a sibiling
				if(wordPos >= word.length() - 1)
				{
					if(theNode.hasNextDown() == false)
					{
						theNode.setDownNode(new Node('^'));
						//System.out.println("added ^ down");
					}
					else
					{
						theNode = theNode.nextDown();
						while(theNode.hasNextRight())
						{
							theNode = theNode.nextRight();
						}
						theNode.setRightNode(new Node('^'));
						//System.out.println("added ^ right");
					}
					//System.out.println("added ^ down");
					wordPos = 0;
					theNode = root;
					isAdded = true;
					break;
				}
				//if no downNode is found, set the child to be a new node
				else if(!(theNode.hasNextDown()))
				{
					theNode.setDownNode(new Node(word.charAt(wordPos + 1)));
					//System.out.println("added " + word.charAt(wordPos + 1) + " down");
					wordPos++;
					theNode = theNode.nextDown();
				}
				//else, we found the letter and we go down the trie
				else
				{
					wordPos++;
					theNode = theNode.nextDown();
				}
			}
			//if the value doesn't equal the char that theNode has, then we need to
			//move right(or to the sibling) until we find the letter
			else if(theNode.getValue() != word.charAt(wordPos))
			{
				if(!theNode.hasNextRight())
				{
					theNode.setRightNode(new Node(word.charAt(wordPos)));
					//System.out.println("added " + word.charAt(wordPos) + " right");
				}
				theNode = theNode.nextRight();
			}
			else
			{
				return isAdded;
			}
		}
		return isAdded;
		//System.out.println(root.getValue());
	}
	public int searchPrefix(StringBuilder inputStringBuilder)
	{
		return searchPrefix(inputStringBuilder, 0, inputStringBuilder.length() - 1);
	}
	public int searchPrefix(StringBuilder inputStringBuilder, int start, int end)
	{
		Node currentNode = root;
		int foundPrefix = 0;
		int foundWord = 0;

		int i = start;
		//System.out.println(inputStringBuilder.toString());
		while(i <= end)
		{
			if(currentNode.getValue() == inputStringBuilder.charAt(i))
			{

				//System.out.println(currentNode.getValue() + " value equals, moving down");
				i++;
				if(i > end)
				{
					break;
				}
				currentNode = currentNode.nextDown();
			}
			else
			{
				//System.out.println(currentNode.getValue() + " moved right");
				currentNode = currentNode.nextRight();
				if(currentNode == null)
				{
					return 0;
				}
				if(currentNode.getValue() == inputStringBuilder.charAt(end) && i == end)
				{
					break;
				}
			}
		}
		//System.out.println(currentNode.getValue());
		Node endCharacterNode = currentNode.nextDown();

		if(endCharacterNode.getValue() == '^' && (!endCharacterNode.hasNextRight()))
		{
			return 2;
		}
		while(endCharacterNode != null)
		{
			if(endCharacterNode.getValue() == '^')
			{
				return 3;
			}
			endCharacterNode = endCharacterNode.nextRight();
		}
		return 1;
	}
	private class Node
	{
		private Node nextRight;
		private Node nextDown;
		private char character;

		//Methods and Constructors
		private Node(char theChar)
		{
			character = theChar;
		}
		private void setValue(char theChar)
		{
			character = theChar;
		}
		private void setDownNode(Node theNode)
		{
			nextDown = theNode;
		}
		private void setRightNode(Node theNode)
		{
			nextRight = theNode;
		}
		private Node nextDown()
		{
			return nextDown;
		}
		private Node nextRight()
		{
			return nextRight;
		}
		private char getValue()
		{
			return character;
		}
		private boolean hasNextRight()
		{
			if(nextRight == null)
			{
				return false;
			}
			return true;
		}
		private boolean hasNextDown()
		{
			if(nextDown == null)
			{
				return false;
			}
			return true;
		}
	}	
}