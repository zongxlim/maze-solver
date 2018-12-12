import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

/** Code for CS201 HW04: Maze solver. The main goal was to use the Stack ADT to solve a maze.
    @author Jed Yang, 2017-01-14
    @author Zong Xian Lim, 2018-04-15
*/
public class Maze
{
   /** Private instance variables:
       number of columns and rows in the maze.
   */
   private int numCols;
   private int numRows;

   /** Coordinates for start/end square positions */
   private int startCol, startRow;
   private int endCol, endRow;

   /** The coordinates of the square as a unit, the condition of the square, and whether or not the square is visited. 
       The number of lines of row description, the number of maze squares per row, and the total number of maze squares in the maze.
   */
   private int numOfLines, numOfBoxesPerRow, numOfBoxes;

   /** The lists containing all the condition for maze square design, all maze squares with corresponding description, maze squares 
       that are part of the solution, and a stack of maze squares part of that solution. 
   */
   private List<Character> conditions;
   private List<MazeSquare> mazeInfo;
   private Stack<MazeSquare> solutionStack;
   private List<MazeSquare> solutionList;

   /** Constructor of the maze. */
   public Maze()
   {
      this.conditions = new ArrayList<>();
      this.mazeInfo = new ArrayList<>();
      this.solutionStack = new CarlStack<>();
      this.solutionList = new ArrayList<>();
   } // end constructor

   /** Load the maze from a file.
       @return True if successful. */
   public boolean load(String fileName)
   {
      File inputFile = new File(fileName);
      Scanner scanner = null;

      try
      {
         scanner = new Scanner(inputFile);
      }
      catch (FileNotFoundException e)
      {
         System.err.println("Maze file not found: " + fileName);
         return false;
      }

      try
      {
         // reads the first three lines of the maze file
         numCols = scanner.nextInt();
         numRows = scanner.nextInt();
         startCol = scanner.nextInt();
         startRow = scanner.nextInt();
         endCol = scanner.nextInt();
         endRow = scanner.nextInt();
      }
      catch (InputMismatchException e)
      {
         System.err.println("Malformed maze file header.");
         return false;
      }

      // check that start/end coordinates are valid
      if (!inRange(startCol, 0, numCols)
         || !inRange(startRow, 0, numRows)
         || !inRange(endCol, 0, numCols)
         || !inRange(endRow, 0, numRows))
      {
         System.err.println("Start or end goal out of bounds.");
         return false;
      }
      
      String blankLine = scanner.nextLine();
      // array that will temporarily hold all characters for each line
      char[] conditionsArray = null;
      while (scanner.hasNextLine())
      {
         numOfBoxesPerRow = 0;
         String line = scanner.nextLine();
         conditionsArray = line.toCharArray();
         for (char condition: conditionsArray)
         {
            conditions.add(condition);
            numOfBoxes++;
            numOfBoxesPerRow++;
         }
         if (numOfBoxesPerRow != numCols)
         {
            System.err.println("Descriptions per row and" + 
            " number of columns are different.");
            return false;
         }
         numOfLines++;
      }

      if (numOfLines != numRows)
      {
         System.err.println("Line of descriptions and" + 
         " number of rows are different.");
         return false;
      }

      int a = 0;
      for (int row = 0; row < this.numRows; row++)
      {
         for (int col = 0; col < this.numCols; col++)
         {
            char condition = conditions.get(a);
            MazeSquare mazeSquare = new MazeSquare(col, row, condition);
            if (mazeSquare.validDescriptor(condition) == false)
            {
               System.err.println("Descriptor isn't valid, only "
               + "'L', '.', '_', and '|' allowed.");
               return false;
            }
            mazeInfo.add(mazeSquare);
            a++;
         }
      }
      return true;
   }

   /** Checks if num is in the range lower (inclusive) to upper (exclusive).
       @param num
       @param lower
       @param upper
       @return True if lower <= num < upper
   */
   private static boolean inRange(int num, int lower, int upper)
   {
      return lower <= num && num < upper;
   }

   /** Prints the Maze in a pretty way. */
   public void print()
   {
      while (!solutionStack.isEmpty())
      {
         solutionList.add(solutionStack.pop());
      }

      // top border
      for (int col = 0; col < numCols; col++)
      {
         System.out.print("+");
         System.out.print("---");
      }
      System.out.println("+");

      // one row at a time
      for (int row = 0; row < numRows; row++)
      {
         // the row of squares with vertical dividing walls
         for (int col = 0; col < numCols; col++)
         {
            // retrieve the square to be printed; this is what allows the print() function to not care about how the squares are stored
            MazeSquare square = getMazeSquare(col, row);

            // left wall of a square
            if (square.hasLeftWall())
            {
               System.out.print("|");
            }
            else
            {
               System.out.print(" ");
            }

            System.out.print(" ");

            // square with possible designation of start/finish
            if (col == startCol && row == startRow)
            {
               System.out.print("S");
            }
            else if (col == endCol && row == endRow)
            {
               System.out.print("F");
            }
            // squares that are part of the solution
            else if (partOfPath(col, row))
            {
               System.out.print("*");  
            }
            else
            {
               System.out.print(" ");
            }

            System.out.print(" ");
         }
         System.out.println("|"); // right-most wall

         // horizontal walls below the row just printed
         for (int col = 0; col < numCols; col++)
         {
            MazeSquare square = getMazeSquare(col, row);
            System.out.print("+");
            if (square.hasBottomWall())
               System.out.print("---");
            else
               System.out.print("   ");
         }
         System.out.println("+"); // right-most wall
      } // end for
   } // end print()

   /** Gets the maze square from a specific position.
       @param col Column of maze square
       @param row Row of maze square 
       @return The MazeSquare specified by column and row.
   */
   public MazeSquare getMazeSquare(int col, int row)
   {
      int totalNum = (row*(numOfBoxesPerRow)) + col;
      return mazeInfo.get(totalNum);
   } // getMazeSquare

   /** Computes and returns a solution to this maze. If there are multiple
    solutions, only one is returned, and getSolution() makes no guarantees
    about which one.  However, the returned solution will not include visits to
    dead ends or any backtracks, even if backtracking occurs during the
    solution process. 

    @return a stack of MazeSquare objects containing the sequence of squares
    visited to go from the start square (bottom of the stack) to the finish
    square (top of the stack). If there is no solution, an empty stack is
    returned.
   */
   public Stack<MazeSquare> getSolution()
   {
      // stack used to store all mazesquare accessed
      Stack<MazeSquare> stack = new CarlStack<>();
      MazeSquare firstSquare = getMazeSquare(startCol,startRow);
      stack.push(firstSquare);
      firstSquare.setVisited();
      MazeSquare square = null;
      while (!stack.isEmpty())
      {
         // indicates the change(s) made towards the stack 'stack'.
         int change = 0;
         square = stack.peek();
         if (!square.isInSolution())
         {
            square.setSolution();
            solutionStack.push(square);
         }
         // end loop if top of stack is final destination
         if (square.equals(getMazeSquare(endCol, endRow)))
         {
            break;
         }

         else
         {
            if ((square.getCol() + 1) < numOfBoxesPerRow)
            {
              MazeSquare square2 = getMazeSquare((square.getCol() + 1), square.getRow());
              if ((!square2.hasLeftWall()) && (!square2.isVisited()))
              {
               stack.push(square2);
               square2.setVisited();
               change++;
              }
            } // end right

            if ((square.getRow()-1) >= 0)
            {
              MazeSquare square3 = getMazeSquare(square.getCol(), (square.getRow() - 1));
              if ((!square3.hasBottomWall()) && (!square3.isVisited()))
              {
               stack.push(square3);
               //System.out.println("3 " + square3);
               square3.setVisited();
               change++;
              }
            } // end top

            if (!square.hasBottomWall())
            {
               if ((square.getRow() + 1) < numOfLines)
               {
                  MazeSquare square4 = getMazeSquare(square.getCol(), (square.getRow() + 1));
                  if (!square4.isVisited())
                  {
                     stack.push(square4);
                     square4.setVisited();
                     change++;
                  }
               }
            } //end bottomWall

            if (!square.hasLeftWall())
            {
               if ((square.getCol() - 1) >= 0)
               {
                  MazeSquare square5 = getMazeSquare((square.getCol() - 1), square.getRow());
                  if (!square5.isVisited())
                  {
                     stack.push(square5);
                     square5.setVisited();
                     change++;
                  }
               }
            } // end leftWall

            if (change < 1)
            {
               MazeSquare popped = stack.pop();
               solutionStack.pop();
            }
         } // end else
      } // end while loop
      return solutionStack;
   } // end getSolution()

   /** Checks if MazeSquare is part of the solution based on the column and row input
       @param col
       @param row
       @return True MazeSquare is part of the solution.
   */
   private boolean partOfPath(int col, int row)
   {
      for (MazeSquare square: solutionList)
      {
         if (square.getCol() == col && square.getRow() == row)
         {
            return true;
         }
      }
      return false;
   } // major credits to James Yan for guiding me in this portion and giving me some hints

   public static void main(String[] args)
   {
      if (args.length > 2 || args.length < 1)
      {
         System.err.println("Usage: java CommandLine mazeFile [solvedOrNot]");
         System.exit(1);
      }
      String fileName = args[0];
      Maze maze1 = new Maze();
      boolean validOrNot = maze1.load(fileName);
      if (!validOrNot)
      {
         System.err.println("The file provided contains error,"
         + " please correct error or change file.");
         System.exit(1);
      }
      if (args.length == 2 && args[1].equals("--solve"))
      {
         Stack<MazeSquare> temp = maze1.getSolution();
      }
      maze1.print();

   } // end main
} // end Maze

/** References:
    1. (https://www.quora.com/How-can-I-break-a-string-into-individual-chars-in-java) for line 96.

    Major credits to James Yan for guiding me and giving me some hints in the partOfPath method.

    Thanks!
*/
