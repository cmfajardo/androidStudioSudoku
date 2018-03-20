package edu.utep.cs.cs4330.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.cs4330.sudoku.model.Board;

import static edu.utep.cs.cs4330.sudoku.model.Board.battle_Board;

/**
 * A special view class to display a Sudoku board modeled by the
 * {@link edu.utep.cs.cs4330.sudoku.model.Board} class. You need to write code for
 * the <code>onDraw()</code> method.
 *
 * @see edu.utep.cs.cs4330.sudoku.model.Board
 * @author cheon
 */
public class BoardView extends View {

    /** To notify a square selection. */
    public interface SelectionListener {

        /** Called when a square of the board is selected by tapping.
         * @param x 0-based column index of the selected square.
         * @param y 0-based row index of the selected square. */
        void onSelection(int x, int y);
    }

    /** Listeners to be notified when a square is selected. */
    private final List<SelectionListener> listeners = new ArrayList<>();

    /** Number of squares in rows and columns.*/
    private int boardSize;
    public int squareX;
    public int squarey;

    //message to display in an error
    public String msg;

    /** Board to be displayed by this view. */
    private Board board;

    /** Width and height of each square. This is automatically calculated
     * this view's dimension is changed. */
    private float squareSize;

    /** Translation of screen coordinates to display the grid at the center. */
    private float transX;

    /** Translation of screen coordinates to display the grid at the center. */
    private float transY;

    /** Paint to draw the background of the grid. */
    private final Paint boardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        int boardColor = Color.rgb(201, 186, 145);
        boardPaint.setColor(boardColor);
        boardPaint.setAlpha(80); // semi transparent
    }

    /** Create a new board view to be run in the given context. */
    public BoardView(Context context) { //@cons
        this(context, null);
    }

    /** Create a new board view by inflating it from XML. */
    public BoardView(Context context, AttributeSet attrs) { //@cons
        this(context, attrs, 0);
    }

    /** Create a new instance by inflating it from XML and apply a class-specific base
     * style from a theme attribute. */
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSaveEnabled(true);
        getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    /** Set the board to be displayed by this view. */
    public void setBoard(Board board) {
        this.board = board;
        boardSize = board.size;
    }

    /** Draw a 2-D graphics representation of the associated board. */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(transX, transY);

        if (board != null) {
            drawGrid(canvas);
            drawSquares(canvas);
        }
        canvas.translate(-transX, -transY);
    }

    /** Draw horizontal and vertical grid lines. */
    private void drawGrid(Canvas canvas) {
        final float maxCoord = maxCoord();
        canvas.drawRect(0, 0, maxCoord, maxCoord, boardPaint);
        // set colors for the lines to draw
        Paint darkBlue = new Paint();
        darkBlue.setColor(getResources().getColor(R.color.colorPrimaryDark));

        Paint pink = new Paint();
        pink.setColor(getResources().getColor(R.color.colorAccent));

        //draw grid lines for the board
        for (int i=0; i<board.size; i++){
            canvas.drawLine(0, i*squareSize, getWidth(), i*squareSize, darkBlue);
            canvas.drawLine(0, i*squareSize+1, getWidth(), i*squareSize+1, darkBlue);
            canvas.drawLine(i*squareSize, 0, i*squareSize, getHeight(), darkBlue);
            canvas.drawLine(i*squareSize+1, 0, i*squareSize+1, getHeight(), darkBlue);
        }

        //draw grid lines for subsquares
        for (int i=0; i<board.size; i++){
            if (i % ((int) Math.sqrt(board.size)) != 0) {
                continue;
            }
            canvas.drawLine(0, i*squareSize, getWidth(), i*squareSize, pink);
            canvas.drawLine(0, i*squareSize+1, getWidth(), i*squareSize+1, pink);
            canvas.drawLine(i*squareSize, 0, i*squareSize, getHeight(), pink);
            canvas.drawLine(i*squareSize+1, 0, i*squareSize+1, getHeight(), pink);
            }
        }


    /** Draw all the squares (numbers) of the associated board. */
    private void drawSquares(Canvas canvas) {

        Paint darkBlue = new Paint();
        darkBlue.setColor(Color.BLACK);
        darkBlue.setTextSize(55);
       // canvas.drawText("0", 100, 100, darkBlue);

        float temp = maxCoord() / board.size;
        float temp2 = temp / 2;

        float firstFor;
        float tempx;
        float tempy;
        Paint.FontMetrics fm = darkBlue.getFontMetrics();
        float x = squareSize / 2;
        float y = squareSize / 2 - (fm.ascent + fm.descent) / 2;


        for (int i = 0; i < battle_Board.length; i++) {
            //draw numbers inside the individual square of the board
            for (int j = 0; j < battle_Board[i].length; j++) {
                tempx = (temp * i) + temp2;
                tempy = (temp * j) + temp2;
                if (battle_Board[i][j] != 0) {
                    canvas.drawText(String.valueOf(battle_Board[i][j]), tempx-20, tempy+20, darkBlue);
                }

            }

        }


    }
    /** Overridden here to detect tapping on the board and
     * to notify the selected square if exists. */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int xy = locateSquare(event.getX(), event.getY());
                if (xy >= 0) {
                    // xy encoded as: x * 100 + y
                    notifySelection(xy / 100, xy % 100);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    /**
     * Given screen coordinates, locate the corresponding square of the board, or
     * -1 if there is no corresponding square in the board.
     * The result is encoded as <code>x*100 + y</code>, where x and y are 0-based
     * column/row indexes of the corresponding square.
     */
    private int locateSquare(float x, float y) {
        x -= transX;
        y -= transY;
        if (x <= maxCoord() &&  y <= maxCoord()) {
            final float squareSize = lineGap();
            int ix = (int) (x / squareSize);
            int iy = (int) (y / squareSize);
            return ix * 100 + iy;
        }
        return -1;
    }

    /** To obtain the dimension of this view. */
    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener
            =  new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            squareSize = lineGap();
            float width = Math.min(getMeasuredWidth(), getMeasuredHeight());
            transX = (getMeasuredWidth() - width) / 2f;
            transY = (getMeasuredHeight() - width) / 2f;
        }
    };

    /** Return the distance between two consecutive horizontal/vertical lines. */
    protected float lineGap() {
        return Math.min(getMeasuredWidth(), getMeasuredHeight()) / (float) boardSize;
    }

    /** Return the number of horizontal/vertical lines. */
    private int numOfLines() { //@helper
        return boardSize + 1;
    }

    /** Return the maximum screen coordinate. */
    protected float maxCoord() { //@helper
        return lineGap() * (numOfLines() - 1);
    }

    /** Register the given listener. */
    public void addSelectionListener(SelectionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /** Unregister the given listener. */
    public void removeSelectionListener(SelectionListener listener) {
        listeners.remove(listener);
    }

    /** Notify a square selection to all registered listeners.
     *
     * @param x 0-based column index of the selected square
     * @param y 0-based row index of the selected square
     */
    private void notifySelection(int x, int y) {
        for (SelectionListener listener: listeners) {
            listener.onSelection(x, y);
        }
    }

}
