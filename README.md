# maze-solver
An assignment from my Data Structures Course (Spring 2018) that utilizes the Java's Stack ADT to solve a maze via Maze.java. The command line should follow the syntax: 

java Maze <mazeFile> [--solve]
 
A solved maze will be displayed after! The mazes (maze.txt, maze1.txt, maze2.txt, maze3.txt, maze4.txt, & maze5.txt) are rectangular and have walls along the entire outside of the maze, with no gaps in these outer walls. A “start square” (S) and a “finish square” (F) is indicated and the goal of the maze-solver is to get from S to F.

The below is taken from the instructions of the assignment (Carleton College CS 201: Data Structures (Spring 2018)
HW04: Maze solver by Jed Yang)
"Maze files have the following structure:

<Number of columns> <Number of rows>
<0-based column number of the start square> <0-based row number of the start square> 
<0-based column number of the finish square> <0-based row number of the finish square> 
<Row 0 description>
<Row 1 description>
...
Each row description includes a single character for each square in that row, and each character describes the left and bottom walls for its square. Specifically:

L (letter L) means that the square has both a left wall and a bottom wall
| (vertical bar or pipe) means that the square has a left wall, but no bottom wall
_ (underscore) means that the square has a bottom wall, but no left wall
. (dot) means that the square has neither a bottom wall nor a left wall
Putting this together in a small example, if the input file contains the following:

3 2     [The maze has 3 columns and 2 rows]
0 0     [The start square is at the upper left]
2 0     [The finish square is at the upper right]
L.|     [(0,0) has left and bottom walls; (1,0) has neither left nor bottom; (2,0) has just left]
L__     [(0,1) has left and bottom walls; (1,1) has just bottom; (2,1) has just bottom]
which yields this maze:
+---+---+---+
| S     | F |
+---+   +   +
|           |
+---+---+---+
"
Mazes in maze2.txt and maze5.txt are meant to test the edge cases for my maze solvers and cannot be solved! 
