import java.util.*;
import java.io.*;

public class CrosswordB
{
	private static StringBuilder[] strCol;
	private static StringBuilder[] strRow;
	private static char[][] square;
	private static DictInterface dict;
	private static int crossSize;
	private static int solutions;
	private static String dictType;
	private static int testInt;

	public static void main(String[] args) throws IOException
	{
		//setting up dictionary files and MyDictionary
		testInt = 10000;
		solutions = 0;
		dictType = args[0];
		File dictFile = new File(args[1]);
		Scanner dictReader = new Scanner(dictFile);

		if(dictType.equals("DLB"))
		{
			dict = new DLB();
		}
		else
		{
			dict = new MyDictionary();
		}
		while(dictReader.hasNextLine())
		{
			String dictWord = dictReader.nextLine();
			try
			{
			dict.add(dictWord);
			//System.out.println(dictWord);
			}
			catch(Exception e)
			{
				System.out.println(dictWord);
				System.exit(0);
			}
		}
		dictReader.close();

		//setting up crossword file
		File crossFile = new File(args[2]);
		Scanner crossReader = new Scanner(crossFile);
		crossSize = Integer.parseInt(crossReader.nextLine());

		strCol = new StringBuilder[crossSize];
		strRow = new StringBuilder[crossSize];
		square = new char[crossSize][crossSize];
		
		//initialzing stringbuilder arrays
		for(int i = 0; i < strCol.length; i++)
		{
			strCol[i] = new StringBuilder("");
		}
		for(int i = 0; i < strRow.length; i++)
		{
			strRow[i] = new StringBuilder("");
		}

		//creating crossword square from file
		int colPos = 0;
		int rowPos = 0;
		while(crossReader.hasNextLine())
		{
			String crossLine = crossReader.nextLine();
			//System.out.println(crossLine);

			for(int i = 0; i < crossSize; i++)
			{
				square[colPos][i] = crossLine.charAt(i);
			}
			colPos++;
		}

		solve(0, 0);

		//if solve ever returns to the main, then the crossword had no possible solutions
		if(dictType.equals("DLB"))
		{
			System.out.println(solutions);
		}
		else
		{
			System.out.println("No Solution");
		}
	}
	private static boolean isValid(int rowPos, int colPos, char crossLetter)
	{
		//System.out.println(rowPos + " " + colPos);
		//System.out.println(square[0][1]);

		//rowCrossedSpot and colCrossedSpot are both variables which
		//keep track of where the crossed out spaces are IE where
		//no letters may be placed. it is set to -1 so we know we havent
		//found a crossed out spot yet. This crossed out spot is never the
		//last crossed out spot added.
		int rowCrossedSpot = -1;
		int colCrossedSpot = -1;
		//count the number of crossed out spaces in the row or column
		int crossedSpotsNumRow = 0;
		int crossedSpotsNumCol = 0;
		for(int i = strRow[rowPos].length() - 1; i >= 0; i--)
		{
			if((strRow[rowPos].charAt(i) == '-' && rowCrossedSpot == -1))
			{
				//System.out.println("help");
				rowCrossedSpot = i;
				crossedSpotsNumRow++;
			}
			else if(strRow[rowPos].charAt(i) == '-')
			{
				//once we have found the crossed spot, we only check to find more crossed of spots for the count
				crossedSpotsNumRow++;
			}
		}
		for(int i = strCol[colPos].length() - 1; i >= 0; i--)
		{
			if(strCol[colPos].charAt(i) == '-' && colCrossedSpot == -1)
			{
				colCrossedSpot = i;
				crossedSpotsNumCol++;
			}
			else if(strCol[colPos].charAt(i) == '-')
			{
				crossedSpotsNumCol++;
			}
		}
		//its important we append after the for loops so we dont accidently
		//include the newest '-' character
		strRow[rowPos].append(crossLetter);
		strCol[colPos].append(crossLetter);
		//System.out.println(strRow[rowPos].toString());
		//System.out.println(rowPos);
		//System.out.println(rowCrossedSpot + " " + strRow[rowPos].toString());
		//we increment the crossed spots count if the crossLetter (the letter we are adding in)
		//is the '-' character
		if(crossLetter == '-')
		{
			crossedSpotsNumRow++;
			crossedSpotsNumCol++;
		} 
		int rowCheck;
		int colCheck;
		if(crossLetter == '-')
		{
			rowCheck = dict.searchPrefix(strRow[rowPos], rowCrossedSpot + 1, strRow[rowPos].length() - 2);
			colCheck = dict.searchPrefix(strCol[colPos], colCrossedSpot + 1, strCol[colPos].length() - 2);
		}
		else
		{
			rowCheck = dict.searchPrefix(strRow[rowPos], rowCrossedSpot + 1, strRow[rowPos].length() - 1);
			colCheck = dict.searchPrefix(strCol[colPos], colCrossedSpot + 1, strCol[colPos].length() - 1);
		}
		String testRowSubString = strRow[rowPos].substring(rowCrossedSpot + 1, strRow[rowPos].length());
		String testColSubString = strCol[colPos].substring(colCrossedSpot + 1, strCol[colPos].length());
		boolean testRow = true;
		boolean testCol = true;
		for(int i = 0; i < testRowSubString.length(); i++)
		{
			if(testRowSubString.charAt(i) != '-')
			{
				testRow = false;
				break;
			}
		}
		for(int i = 0; i < testColSubString.length(); i++)
		{
			if(testColSubString.charAt(i) != '-')
			{
				testCol = false;
				break;
			}
		}
		if(testRow == true)
		{
			rowCheck = 2;
		}
		if(testCol == true)
		{
			colCheck = 2;
		}
		if(crossedSpotsNumRow == strRow[rowPos].length())
		{
			//System.out.println("true");
			rowCheck = 2;
		}
		if(crossedSpotsNumCol == strCol[colPos].length())
		{
			//System.out.println("true");
			colCheck = 2;
		}
		strRow[rowPos].deleteCharAt(strRow[rowPos].length() - 1);
		strCol[colPos].deleteCharAt(strCol[colPos].length() - 1);

		if(rowCheck == 0 || colCheck == 0)
		{
			return false;
		}
		else if((rowCheck == 1 && colPos == crossSize - 1) || (colCheck == 1 && rowPos == crossSize - 1))
		{
			return false;
		}
		else if(crossLetter == '-' && (rowCheck == 1 || colCheck == 1))
		{
			return false;
		}
		return true;
	}

	private static void solve(int rowPos, int colPos)
	{
		if(square[rowPos][colPos] != '+')
		{
			presetLetterInCross(rowPos, colPos);
		}
		else
		{
			for(char i = 'a'; i <= 'z'; i++)
			{
				if(isValid(rowPos, colPos, i))
				{
					strRow[rowPos].append(i);
					strCol[colPos].append(i);
					if(rowPos == crossSize - 1 && colPos == crossSize - 1)
					{
						if(dictType.equals("DLB"))
						{
							solutions++;
						}
						else
						{
							for(int j = 0; j < strRow.length; j++)
							{
								System.out.println(strRow[j]);
							}
							//print stringbuilders for solution as solution has been found;
							System.exit(0);
						}
					}
					else
					{
						if(colPos == crossSize - 1)
						{
							//System.out.println("incrementing row");
							solve(rowPos + 1, 0);
						}
						else
						{
							//System.out.println("incrementing col");
							solve(rowPos, colPos + 1);
						}
					}
					//System.out.println("incrementing char");
					strRow[rowPos].deleteCharAt(strRow[rowPos].length() - 1);
					strCol[colPos].deleteCharAt(strCol[colPos].length() - 1);
				}
			}
			//System.out.println("backtracking");
		}
	}
	private static void presetLetterInCross(int rowPos, int colPos)
	{
		if(square[rowPos][colPos] != '+')
		{
			char presetLetter = square[rowPos][colPos];
			if(isValid(rowPos, colPos, presetLetter))
			{
				strRow[rowPos].append(presetLetter);
				strCol[colPos].append(presetLetter);
				if(rowPos == crossSize - 1 && colPos == crossSize - 1)
				{
					if(dictType.equals("DLB"))
					{
						solutions++;
					}
					else
					{
						for(int j = 0; j < strRow.length; j++)
						{
							System.out.println(strRow[j]);
						}
						//print stringbuilders for solution as solution has been found;
						System.exit(0);
					}
				}
				else
				{
					if(colPos == crossSize - 1)
					{
						//System.out.println("incrementing row");
						solve(rowPos + 1, 0);
					}
					else
					{
						//System.out.println("solve(rowpos, colpos + 1");
						//System.out.println("incrementing col");
						solve(rowPos, colPos + 1);
					}
				}
				//System.out.println("incrementing char");
				strRow[rowPos].deleteCharAt(strRow[rowPos].length() - 1);
				strCol[colPos].deleteCharAt(strCol[colPos].length() - 1);
			}
			//System.out.println("backtracking");
		}
	}
}	
