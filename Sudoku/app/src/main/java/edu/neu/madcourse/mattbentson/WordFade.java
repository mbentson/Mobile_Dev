package edu.neu.madcourse.mattbentson;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import edu.neu.madcourse.mattbentson.sudoku.R;

public class WordFade extends Activity {

    String[] letterBag = {
            "E","E","E","E","E","E","E","E","E","E","E","E",
            "A","A","A","A","A","A","A","A","A",
            "I","I","I","I","I","I","I","I","I",
            "O","O","O","O","O","O","O","O",
            "N","N","N","N","N","N",
            "R","R","R","R","R","R",
            "T","T","T","T","T","T",
            "L","L","L","L",
            "S","S","S","S",
            "U","U","U","U",
            "D","D","D","D",
            "G","G","G",
            "B","B",
            "C","C",
            "M","M",
            "P","P",
            "F","F",
            "H","H",
            "V","V",
            "W","W",
            "Y","Y",
            "K",
            "J",
            "X",
            "Q",
            "Z"};

    String[] scrambledLetters = new String[letterBag.length];

    List<String> scoreLetters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));
    List scoreValues = new ArrayList<>(Arrays.asList(1,3,3,2,1,4,2,2,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10));

    List<String> turnRack = new ArrayList<>();
    List<String> realRack = new ArrayList<>();

    WordFadeGrid grid;
    Dictionary dictionary;

    TextView scoreText;
    TextView timerText;

    int num = 0;

    CountDownTimer timer;
    long timeLeft = 0;

    boolean music = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_fade);
        grid = new WordFadeGrid(this);
        dictionary = new Dictionary(this);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gridLayout);
        layout.addView(grid);
        scrambleLetters();
        dealRack();
        scoreText = (TextView) findViewById(R.id.wordfadeScoreValue);
        timerText = (TextView) findViewById(R.id.timerText);
        setListeners();
        setTurnRack();
        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.wepresson);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop(this);
    }

    private void setTurnRack()
    {
        for(int i = 0; i < realRack.size(); i++)
        {
            turnRack.add(realRack.get(i));
        }
    }

    private void scrambleLetters()
    {
        Random r = new Random();
        List<String> tempLetterBag = new ArrayList<>();
        for(int i = 0; i < letterBag.length; i++)
        {
            tempLetterBag.add(letterBag[i]);
        }

        int num;

        for(int i = 0; i < scrambledLetters.length; i++)
        {
            num = r.nextInt(tempLetterBag.size());
            scrambledLetters[i] = tempLetterBag.get(num);
            tempLetterBag.remove(num);
        }
    }

    private void dealRack()
    {
        for(int i = 0; i < 21; i++)
        {
            realRack.add(scrambledLetters[i]);
            num++;
        }
    }

    public void showKeypad(String mode)
    {
        Dialog v = new WordFadeKeypad(this, turnRack, grid,mode);
        v.show();
    }

    public void removeFromRack(CharSequence letter)
    {
        turnRack.remove((String) letter);
    }

    public void addToRack(CharSequence letter)
    {
        turnRack.add((String) letter);
    }
    private void setListeners()
    {
        Button submit = (Button) findViewById(R.id.wordfadeSubmitBtn);
        Button split = (Button) findViewById(R.id.wordfadeSplitBtn);
        Button dump = (Button) findViewById(R.id.wordfadeDumpBtn);
        Button music = (Button) findViewById(R.id.wordfadeMusicBtn);
        Button pause = (Button) findViewById(R.id.wordfadePauseBtn);
        Button quit = (Button) findViewById(R.id.wordfadeQuitBtn);

        submit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showMessage(grid.callSubmit());
            }});

        split.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showMessage(callSplit());
            }});

        dump.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showMessage(callDump());
            }});

        music.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                toggleMusic();
            }});

        pause.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                callPause();
            }});

        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callQuit();
            }
        });


    }

    private void toggleMusic()
    {
        if(music)
        {
            Music.stop(this);
            music = false;
        }else{
            Music.play(this, R.raw.wepresson);
            music = true;
        }
    }

    boolean dontShow = false;
    public void showMessage(String message)
    {
        if(message != "" && !dontShow) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    public void setScore(String word)
    {
        int total = 0;

        for(int i = 0; i < word.length(); i++)
        {
            total += (int) scoreValues.get(scoreLetters.indexOf(word.substring(i,i+1)));
        }

        int score = Integer.parseInt((String) scoreText.getText());

        scoreText.setText((score + total) + "");
    }

    private String callSplit()
    {
        int letters = realRack.size();

        int points = 0;

        if(letters == 21)
        {
            return "You already have 21 letters";
        }
        if(letters > 10)
        {
            points =  -5;
        }else {
            if (letters > 4) {
                points = 0;
            }else {
                if (letters == 4) {
                    points = 4;
                }else{
                    if(letters == 3)
                    {
                        points =  6;
                    }else{
                        if(letters == 2)
                        {
                            points =  8;
                        }else{
                            if(letters == 1)
                            {
                                points =  10;
                            }else{
                                points = 15;
                            }
                        }
                    }
                }
            }
        }

        realRack.add(scrambledLetters[num]);
        turnRack.add(scrambledLetters[num]);
        num++;

        scoreText.setText((Integer.parseInt((String) scoreText.getText()) + points) + "");

        checkScore();
        return "Received: " + scrambledLetters[num - 1] + ", " + points + " points awarded";
    }

    private String callDump()
    {
        if(realRack.size() > 18)
        {
            return "You have more than 18 letters";
        }else{
            showKeypad("dump");
        }
        return "";
    }

    public void dump(CharSequence letter)
    {
        int points = -5;

        realRack.remove(letter);
        turnRack.remove(letter);

        for(int i = 0; i < 3; i++)
        {
            String l = scrambledLetters[num];
            realRack.add(l);
            turnRack.add(l);
            points -= 2 * (int) scoreValues.get(scoreLetters.indexOf(l));
            num++;
        }

        scoreText.setText((Integer.parseInt((String)scoreText.getText()) + points) + "");
        checkScore();
        showMessage("Received: " + scrambledLetters[num - 1] + ", " + scrambledLetters[num - 2] + ", and " + scrambledLetters[num - 3] + ", " + -1*points + " points deducted");
    }

    private void callPause()
    {
        timer.cancel();

        Dialog v = new wordfade_pause(this);
        v.show();
    }

    public void callResume()
    {
        timer = new CountDownTimer(timeLeft, 1000) {

            public void onTick(long millisUntilFinished) {
                timerText.setText(": " + millisUntilFinished / 1000);
                timeLeft = millisUntilFinished;
            }

            public void onFinish() {
                scoreText.setText((Integer.parseInt((String) scoreText.getText()) - 5) + "");
                checkScore();
                restartTimer();
            }
        };

        timer.start();
    }

    private void callQuit()
    {
        finish();
    }

    public void reRack()
    {
        realRack.clear();
        for(int i = 0; i < turnRack.size(); i++)
        {
            realRack.add(turnRack.get(i));
        }
    }

    public void beep()
    {
        dictionary.beep();
    }

    public boolean checkWord(String word)
    {
        return dictionary.checkWord(word);
    }

    private void checkScore()
    {
        int score = Integer.parseInt((String)scoreText.getText());
        if(score < 0)
        {
            showMessage("Negative Score, GAME OVER");
            dontShow = true;
            gameOver();
        }
    }

    public void gameOver()
    {
        timer.cancel();
        finish();
    }

    private void startTimer()
    {
        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerText.setText(": " + millisUntilFinished / 1000);
                timeLeft = millisUntilFinished;
            }

            public void onFinish() {
                scoreText.setText((Integer.parseInt((String) scoreText.getText()) - 5) + "");
                checkScore();
                restartTimer();
            }
        };

        timer.start();
    }

    public void restartTimer()
    {
        timer.cancel();

        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerText.setText(": " + millisUntilFinished / 1000);
                timeLeft = millisUntilFinished;
            }

            public void onFinish() {
                scoreText.setText((Integer.parseInt((String) scoreText.getText()) - 5) + "");
                checkScore();
                restartTimer();
            }
        };

        timer.start();
    }
}
