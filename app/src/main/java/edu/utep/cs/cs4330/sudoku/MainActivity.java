package edu.utep.cs.cs4330.sudoku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.cs4330.sudoku.model.Board;

import static edu.utep.cs.cs4330.sudoku.model.Board.battle_Board;

/**
 * HW1 template for developing an app to play simple Sudoku games.
 * You need to write code for three callback methods:
 * newClicked(), numberClicked(int) and squareSelected(int,int).
 * Feel free to improved the given UI or design your own.
 *
 * <p>
 *  This template uses Java 8 notations. Enable Java 8 for your project
 *  by adding the following two lines to build.gradle (Module: app).
 * </p>
 *
 * <pre>
 *  compileOptions {
 *  sourceCompatibility JavaVersion.VERSION_1_8
 *  targetCompatibility JavaVersion.VERSION_1_8
 *  }
 * </pre>
 *
 * @authors Yoonsik Cheon, Carlos Fajardo, Miguel Nunez
 */
public class MainActivity extends AppCompatActivity{

    private Board board;
    private static int[][] solution_Board9x9 = new int[9][9];
    private static int[][] solution_Board4x4 = new int[4][4];
    private static int difficulty;

    private TextView displayNum;

    private BoardView boardView;
    public static int currNumber;

    /** All the number buttons. */
    private List<View> numberButtons;
    private static final int[] numberIds = new int[] {
            R.id.n0, R.id.n1, R.id.n2, R.id.n3, R.id.n4,
            R.id.n5, R.id.n6, R.id.n7, R.id.n8, R.id.n9
    };

    /** Width of number buttons automatically calculated from the screen size. */
    private static int buttonWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayNum = findViewById(R.id.displayNum);

        /**By default the board size is 9**/
        board = new Board(9);
        boardView = findViewById(R.id.boardView);
        boardView.setBoard(board);
        boardView.addSelectionListener(this::squareSelected);
        Board.fill9x9Board(solution_Board9x9);
        Board.fill9x9Board(battle_Board);
        Board.difficultyBoard9x9(battle_Board,3);
        Board.fill4x4Board(solution_Board4x4);

        numberButtons = new ArrayList<>(numberIds.length);
        for (int i = 0; i < numberIds.length; i++) {
            final int number = i; // 0 for delete button
            View button = findViewById(numberIds[i]);
            button.setOnClickListener(e -> numberClicked(number));
            numberButtons.add(button);
            setButtonWidth(button);
        }
    }

    /** Callback to be invoked when the new button is tapped. */
    public void newClicked(View view) {

        setContentView(R.layout.activity_main);

        displayNum = findViewById(R.id.displayNum);
        currNumber = 0;

        boardView = findViewById(R.id.boardView);
        boardView.setBoard(board);
        boardView.addSelectionListener(this::squareSelected);

        numberButtons = new ArrayList<>(numberIds.length);

        for (int i = 0; i < numberIds.length; i++) {
            final int number = i; // 0 for delete button
            View button = findViewById(numberIds[i]);
            button.setOnClickListener(e -> numberClicked(number));
            numberButtons.add(button);
            setButtonWidth(button);
        }
        // disable the numbers above 4 whan the grid is 4x4
        if(board.size == 4){
            for(int i=board.size+1; i<numberIds.length; i++){
                View button = findViewById(numberIds[i]);
                button.setEnabled(false);
            }
            Board.fill4x4Board(battle_Board);
            Board.difficultyBoard4x4(battle_Board, difficulty);
        }
        else{
            Board.fill9x9Board(battle_Board);
            Board.difficultyBoard9x9(battle_Board, difficulty);
        }
            toast("New clicked.");
    }

    /** Callback to be invoked when a number button is tapped.
     *
     * @param n Number represented by the tapped button
     *          or 0 for the delete button.
     */
    static boolean numClicked;

    public void numberClicked(int n) {

        numClicked = true;
        currNumber = n;
        /**tell user what number is selected**/
        if(n>0) {
            displayNum.setText("Number: " + n);
        }
        else
            displayNum.setText("Delete");
    }


    /**
     * Callback to be invoked when a square is selected in the board view.
     *
     * @param x 0-based column index of the selected square.
     * @param x 0-based row index of the selected square.
     */
    private void squareSelected(int x, int y) {

        if(numClicked && validateInput(x,y,currNumber)){
            boardView.squareX = x;
            boardView.squarey = y;
            Board.battle_Board[x][y]=currNumber;
            boardView.invalidate();
        }

       //toast(String.format("Square selected: (%d, %d)", x, y));
    }

    /** Show a toast message. */
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /** Set the width of the given button calculated from the screen size. */
    private void setButtonWidth(View view) {
        if (buttonWidth == 0) {
            final int distance = 2;
            int screen = getResources().getDisplayMetrics().widthPixels;
            buttonWidth = (screen - ((9 + 1) * distance)) / 9; // 9 (1-9)  buttons in a row
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = buttonWidth;
        view.setLayoutParams(params);
    }
    /**validate input in Sudoku(no repeats in row, column and subgrids)**/
    private boolean validateInput(int x, int y, int toInsert) {
        if(toInsert == 0) {
            return true;
        }
        for (int i = 0; i < board.size; i++) {
            if (Board.battle_Board[x][i] == toInsert) {
                return false;
            }
            if (Board.battle_Board[i][y] == toInsert) {
                return false;
            }
        }
        for (int i = 0; i < Math.sqrt(board.size); i++) {
            for (int j = 0; j < Math.sqrt(board.size); j++) {
                if (Board.battle_Board[x - (x % (int) Math.sqrt(board.size)) + i][y - (y % (int) Math.sqrt(board.size)) + j] == toInsert) {
                    return false;
                }
            }
        }
        return true;
    }
    /**Switch case to handle when the user selects a 4x4 or a 9x9**/
    public void onRadioButtonSizeClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was selected
        switch(view.getId()) {
            case R.id.radio4x4:
                if (checked)
                    // set the board size to 4x4
                    board = new Board(4);
                    break;
            case R.id.radio9x9:
                if (checked)
                    // set the board size to 9x9
                    board = new Board(9);
                    break;
        }
    }

    public void onRadioButtonDifficultyClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was selected
        switch(view.getId()) {
            case R.id.radioEasy:
                if (checked)
                    // set the difficulty to easy
                    difficulty = 1;
                break;
            case R.id.radioMedium:
                if (checked)
                    // set the difficulty to medium
                    difficulty = 2;
                break;
            case R.id.radioHard:
                if (checked)
                    // set the difficulty to hard
                    difficulty = 3;
                break;
        }
    }

}
