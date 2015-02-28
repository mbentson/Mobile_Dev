package edu.neu.madcourse.mattbentson;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LogicalBoard {

    private int width;
    private int height;

    private final WordFadeGrid wordFadeGrid;

    private String[][] gridLetters;
    private boolean[][] locked;
    List playedX = new ArrayList<>();
    List playedY = new ArrayList<>();
    List totalX = new ArrayList<>();
    List totalY = new ArrayList<>();

    boolean firstTurn = true;

    private class playedWord{
        String word;
        List x = new ArrayList<>();
        List y = new ArrayList<>();
        public playedWord(String word)
        {
            this.word = word;
            fillCoordinates();
        }

        private void fillCoordinates()
        {
            for(int i = 0; i < totalX.size(); i++)
            {
                x.add(totalX.get(i));
                y.add(totalY.get(i));
            }
        }

        public boolean contains(int xCoord,int yCoord)
        {
            for(int i = 0; i < x.size(); i++)
            {
                if(((int) x.get(i) == xCoord)&&((int) y.get(i) == yCoord))
                {
                    return true;
                }
            }

            return false;
        }

        public String getPlayedWord()
        {
            return this.word;
        }
    }
    List<playedWord> playedWords = new ArrayList<>();
    List turnsLeft = new ArrayList<>();

    public LogicalBoard(int w, int h, WordFadeGrid g) {
        wordFadeGrid = g;
        width = w;
        height = h;
        SetupPositions();
    }

    private class BoardPosition extends RectF {
        public BoardPosition(float left, float top, float right, float bottom) {
            super(left, top, right, bottom);

            filled = false;
        }

        private boolean filled;

        public void setFilled(boolean filled) {
            this.filled = filled;
        }

        public boolean isFilled() {
            return filled;
        }
    }

    BoardPosition[][] grid;

    private void SetupPositions() {
        grid = new BoardPosition[31][31];
        gridLetters = new String[31][31];
        locked = new boolean[31][31];

        for(int i = 0; i < 31; i++)
        {
            for(int j = 0; j < 31; j++)
            {
                grid[j][i] = new BoardPosition(j*width,i*height,(j+1)*width,(i+1)*height);
                gridLetters[j][i] = "";
                locked[j][i] = false;
            }
        }
    }

    public RectF getPositionToFill(float x, float y, CharSequence letter) {
        RectF toReturn;
        for(int i = 0; i < grid.length; i++)
        {
            for(int j = 0; j < grid[i].length; j++)
            {
                if (grid[i][j].contains(x, y))
                {
                    toReturn = new RectF(grid[i][j]);
                    if(!grid[i][j].filled) {
                        gridLetters[i][j] = (String) letter;
                        if(letter != "") {
                            grid[i][j].filled = true;
                            playedX.add(i);
                            playedY.add(j);
                            return toReturn;
                        }
                    }else{
                        if(!locked[i][j])
                        {
                            grid[i][j].filled = false;
                            letter = gridLetters[i][j];
                            gridLetters[i][j] = "";
                            playedX.remove(playedX.indexOf(i));
                            playedY.remove(playedY.indexOf(j));
                            wordFadeGrid.clearSelectedTile(toReturn,letter);
                        }
                    }
                }
            }
        }

        return null;
    }
    
    public boolean checkFill(float x, float y)
    {
        for(int i = 0; i < grid.length; i++)
        {
            for(int j = 0; j < grid[i].length; j++)
            {
                if (grid[i][j].contains(x, y))
                {
                    return grid[i][j].isFilled();
                }
            }
        }

        return false;
    }

    public String submit()
    {
        if(playedX.size() == 0)
        {
            return "Nothing has been played";
        }
        totalX.clear();
        totalY.clear();
        String word = "";
        String direction = "";

        if(checkX() == true) {
            direction = "x";
        }else{
            if(checkY() == true)
            {
                direction = "y";
            }else {
                return "Invalid Letter Placement";
            }
        }
        word = getWord(direction);

        if(word.equals("   "))
        {
            return "Invalid Letter Placement";
        }

        if(word.length() <= 3)
        {
            return "A word is not long enough";
        }

        if(!checkDictionary(word))
        {
            return "A word is invalid";
        }

        String allWordsString = checkAllWords(direction);
        if(!allWordsString.equals(""))
        {
            return allWordsString;
        }

        lockAll();
        wordFadeGrid.setScore(word);
        wordFadeGrid.reRack();
        firstTurn = false;
        wordFadeGrid.beep();

        checkPlayedOn();

        playedWords.add(new playedWord(word));
        turnsLeft.add(10);

        playedX.clear();
        playedY.clear();

        wordFadeGrid.restartTimer();

        checkTurnsLeft();

        return word + " Successfully Submitted";
    }

    private void checkPlayedOn()
    {
        for(int i = 0; i < playedWords.size(); i++)
        {
            for(int j = 0; j < totalX.size(); j++)
            {
                if(playedWords.get(i).contains((int) totalX.get(j),(int) totalY.get(j)))
                {
                    turnsLeft.set(i, 11);
                }
            }
        }
    }

    private void checkTurnsLeft()
    {
        for(int i = 0; i < playedWords.size(); i++)
        {
            turnsLeft.set(i, (int) turnsLeft.get(i) - 1);
            if((int) turnsLeft.get(i) < 3)
            {
                wordFadeGrid.showMessage(playedWords.get(i).getPlayedWord() + " has " + turnsLeft.get(i) + " turns left");
            }
            if((int) turnsLeft.get(i) == 0)
            {
                wordFadeGrid.gameOver();
            }
        }
    }
    private boolean checkX()
    {
        int x = (int) playedX.get(0);

        for(int i = 0; i < playedX.size(); i++)
        {
            if(((int) playedX.get(i)) != x)
            {
                return false;
            }
        }

        return true;
    }

    private boolean checkY()
    {
        int y = (int) playedY.get(0);

        for(int i = 0; i < playedY.size(); i++)
        {
            if(((int) playedY.get(i)) != y)
            {
                return false;
            }
        }

        return true;
    }

    private void lockAll()
    {
        for(int i = 0; i < playedX.size(); i++)
        {
            locked[(int)playedX.get(i)][(int)playedY.get(i)] = true;
        }
    }

    private String getWord(String direction)
    {
        String word = "";
        int first;
        int last;

        if(direction.equals("y"))
        {
            int y = (int)playedY.get(0);
            first = getMinX();
            while((first != 0) && !gridLetters[first - 1][y].equals(""))
            {
                first = first - 1;
            }

            last = getMaxX();
            while((last != 30) && !gridLetters[last + 1][y].equals(""))
            {
                last = last + 1;
            }

            for(int i = first; i <= last; i++)
            {
                if(gridLetters[i][y].equals(""))
                {
                    word = "   ";
                    break;
                }
                word += gridLetters[i][y];
                totalX.add(i);
                totalY.add(y);
            }

            if(!firstTurn) {
                int totalCount = 0;
                for (int i = first; i <= last; i++) {
                    int count = 0;
                    if ((y != 0) && gridLetters[i][y - 1].equals("")) {
                        count++;
                    }
                    if ((y != 30) && gridLetters[i][y + 1].equals("")) {
                        count++;
                    }
                    if ((count == 2) || ((y == 0) && (count == 1)) || ((y == 30) && (count == 1))) {
                        totalCount++;
                    }
                }
                if (totalCount == word.length()) {
                    word = "   ";
                }
            }
        }else{
            int x = (int)playedX.get(0);
            first = getMinY();
            while((first != 0) && !gridLetters[x][first - 1].equals(""))
            {
                first = first - 1;
            }

            last = getMaxY();
            while((last != 30) && !gridLetters[x][last + 1].equals(""))
            {
                last = last + 1;
            }

            for(int i = first; i <= last; i++)
            {
                if(gridLetters[x][i].equals(""))
                {
                    word = "   ";
                    break;
                }
                word += gridLetters[x][i];
                totalX.add(x);
                totalY.add(i);
            }

            if(!firstTurn) {
                int totalCount = 0;
                for (int i = first; i <= last; i++) {
                    int count = 0;
                    if ((x != 0) && gridLetters[x - 1][i].equals("")) {
                        count++;
                    }
                    if ((x != 30) && gridLetters[x + 1][i].equals("")) {
                        count++;
                    }
                    if ((count == 2) || ((x == 0) && (count == 1)) || ((x == 30) && (count == 1))) {
                        totalCount++;
                    }
                }
                if (totalCount == word.length()) {
                    word = "   ";
                }
            }
        }

        return word;
    }

    private int getMinX()
    {
        int min = (int) playedX.get(0);

        for(int i = 0; i < playedX.size(); i++)
        {
            if(((int)playedX.get(i)) < min)
            {
                min = (int) playedX.get(i);
            }
        }

        return min;
    }

    private int getMaxX()
    {
        int max = (int) playedX.get(0);

        for(int i = 0; i < playedX.size(); i++)
        {
            if(((int)playedX.get(i)) > max)
            {
                max = (int) playedX.get(i);
            }
        }

        return max;
    }

    private int getMinY()
    {
        int min = (int) playedY.get(0);

        for(int i = 0; i < playedY.size(); i++)
        {
            if(((int)playedY.get(i)) < min)
            {
                min = (int) playedY.get(i);
            }
        }

        return min;
    }

    private int getMaxY()
    {
        int max = (int) playedY.get(0);

        for(int i = 0; i < playedY.size(); i++)
        {
            if(((int)playedY.get(i)) > max)
            {
                max = (int) playedY.get(i);
            }
        }

        return max;
    }

    private boolean checkDictionary(String word)
    {
        return wordFadeGrid.checkWord(word);
    }

    private String checkAllWords(String direction)
    {
        String message = "";
        String tempWord = "";

        if(direction.equals("y"))
        {
            int y = (int) playedY.get(0);
            for(int i = 0; i < playedX.size(); i++)
            {
                if(((y != 0) && !gridLetters[(int)playedX.get(i)][y - 1].equals("")) || ((y != 30) && !gridLetters[(int)playedX.get(i)][y + 1].equals("")))
                {
                    tempWord = getNewWord(direction,(int) playedX.get(i),y);

                    if(tempWord.length() <= 3)
                    {
                        return "A word is too small";
                    }
                    if(!checkDictionary(tempWord))
                    {
                        return "A word is invalid";
                    }
                }
            }
        }else{
            int x = (int) playedX.get(0);
            for(int i = 0; i < playedY.size(); i++)
            {
                if(((x != 0) && !gridLetters[x - 1][(int)playedY.get(i)].equals("")) || ((x != 30) && !gridLetters[x + 1][(int)playedY.get(i)].equals("")))
                {
                    tempWord = getNewWord(direction,x ,(int) playedY.get(i));

                    if(tempWord.length() <= 3)
                    {
                        return "A word is too small";
                    }
                    if(!checkDictionary(tempWord))
                    {
                        return "A word is invalid";
                    }
                }
            }
        }
        return message;
    }

    private String getNewWord(String direction, int x, int y)
    {
        String word = "";
        int first;
        int last;

        if(direction.equals("y"))
        {
            first = y;
            while((first != 0) && !gridLetters[x][first - 1].equals(""))
            {
                first = first - 1;
            }

            last = y;
            while((last != 30) && !gridLetters[x][last + 1].equals(""))
            {
                last = last + 1;
            }

            for(int i = first; i <= last; i++)
            {
                word += gridLetters[x][i];
            }
        }else{
            first = x;
            while((first != 0) && !gridLetters[first - 1][y].equals(""))
            {
                first = first - 1;
            }

            last = x;
            while((last != 30) && !gridLetters[last + 1][y].equals(""))
            {
                last = last + 1;
            }

            for(int i = first; i <= last; i++)
            {
                word += gridLetters[i][y];
            }
        }

        return word;
    }
}
