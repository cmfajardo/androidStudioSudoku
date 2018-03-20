package edu.utep.cs.cs4330.sudoku.model;

/** An abstraction of Sudoku puzzle. */
public class Board {

    /** Size of this board (number of columns/rows). */
    public final int size;
    static public int[][] battle_Board;

    public boolean[][] bool_board;
    /** Create a new board of the given size. */

    public Board(int size) {
        this.size = size;
        battle_Board = new int[size][size];
        for(int i =0; i < size; i++){
            for(int j =0; j < size; j++){
                battle_Board[i][j] = 0;
            }
        }
    }

    /** Return the size of this board. */
    public int size() {
        return size;
    }

    /**fill the 9x9 board with the solution for sudoku**/
    public static void fill9x9Board(int[][] board9x9){
        board9x9 [8]= new int[]{1, 5, 6, 2, 3, 9, 8, 7, 4};
        board9x9 [7]= new int[]{2, 8, 7, 4, 6, 1, 3, 9, 5};
        board9x9 [6]= new int[]{3, 9, 4, 8, 7, 5, 2, 6, 1};
        board9x9 [5]= new int[]{4, 6, 9, 5, 8, 3, 7, 1, 2};
        board9x9 [4]= new int[]{5, 7, 3, 6, 1, 2, 9, 4, 8};
        board9x9 [3]= new int[]{8, 1, 2, 9, 4, 7, 5, 3, 6};
        board9x9 [2]= new int[]{6, 3, 1, 7, 5, 8, 4, 2, 9};
        board9x9 [1]= new int[]{9, 4, 5, 3, 2, 6, 1, 8, 7};
        board9x9 [0]= new int[]{7, 2, 8, 1, 9, 4, 6, 5, 3};
    }
    public static void fill4x4Board(int[][] board4x4){
        board4x4 [3] = new int[]{2, 1, 3, 4};
        board4x4 [2] = new int[]{3, 4, 2, 1};
        board4x4 [1] = new int[]{4, 2, 1, 3};
        board4x4 [0] = new int[]{1, 3, 4, 2};
    }
    public static void difficultyBoard9x9(int[][] board9x9, int diffLvl){
        int numsRemoved = 50;
        if(diffLvl==1)
            numsRemoved = 20;
        else if(diffLvl==2)
            numsRemoved = 40;
        else if(diffLvl==3)
            numsRemoved = 60;

        for (int i=numsRemoved; i>0; i--)
            board9x9[(int)(Math.random()*9)][(int)(Math.random()*9)]=0;
    }
    public static void difficultyBoard4x4(int[][] board9x9, int diffLvl){
        int numsRemoved = 15;
        if(diffLvl==1)
            numsRemoved = 5;
        else if(diffLvl==2)
            numsRemoved = 10;
        else if(diffLvl==3)
            numsRemoved = 15;

        for (int i=numsRemoved; i>0; i--)
            board9x9[(int)(Math.random()*4)][(int)(Math.random()*4)]=0;

    }
}
