package edu.neu.madcourse.mattbentson;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

import edu.neu.madcourse.mattbentson.sudoku.R;

public class WordFadeKeypad extends Dialog {

    private final Button keys[] = new Button[21];
    private View keypad;

    private final List<String> rack;
    private final WordFadeGrid grid;

    private String mode;

    public WordFadeKeypad(Context context, List<String> rack , WordFadeGrid grid, String mode) {
        super(context);
        this.rack = rack;
        this.grid = grid;
        this.mode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.wordfade_keypad_title);
        setContentView(R.layout.wordfade_keypad);
        findViews();
        setText();
        for (int i = rack.size(); i < keys.length; i++ )
        {
            keys[i].setVisibility(View.INVISIBLE);
            keys[i].setText("");
        }
        setListeners();
    }

    /*Return the chosen tile to the caller*/
    private void returnResult(CharSequence letter) {
        if(mode.equals("")) {
            grid.setSelectedTile(letter);
        }else{
            grid.dump(letter);
        }
        dismiss();
    }

    private void findViews() {
        keypad = findViewById(R.id.wordFadeKeypad);
        keys[0] = (Button) findViewById(R.id.wordFadeKeypad_1);
        keys[1] = (Button) findViewById(R.id.wordFadeKeypad_2);
        keys[2] = (Button) findViewById(R.id.wordFadeKeypad_3);
        keys[3] = (Button) findViewById(R.id.wordFadeKeypad_4);
        keys[4] = (Button) findViewById(R.id.wordFadeKeypad_5);
        keys[5] = (Button) findViewById(R.id.wordFadeKeypad_6);
        keys[6] = (Button) findViewById(R.id.wordFadeKeypad_7);
        keys[7] = (Button) findViewById(R.id.wordFadeKeypad_8);
        keys[8] = (Button) findViewById(R.id.wordFadeKeypad_9);
        keys[9] = (Button) findViewById(R.id.wordFadeKeypad_10);
        keys[10] = (Button) findViewById(R.id.wordFadeKeypad_11);
        keys[11] = (Button) findViewById(R.id.wordFadeKeypad_12);
        keys[12] = (Button) findViewById(R.id.wordFadeKeypad_13);
        keys[13] = (Button) findViewById(R.id.wordFadeKeypad_14);
        keys[14] = (Button) findViewById(R.id.wordFadeKeypad_15);
        keys[15] = (Button) findViewById(R.id.wordFadeKeypad_16);
        keys[16] = (Button) findViewById(R.id.wordFadeKeypad_17);
        keys[17] = (Button) findViewById(R.id.wordFadeKeypad_18);
        keys[18] = (Button) findViewById(R.id.wordFadeKeypad_19);
        keys[19] = (Button) findViewById(R.id.wordFadeKeypad_20);
        keys[20] = (Button) findViewById(R.id.wordFadeKeypad_21);
    }

    private void setListeners() {
        for (int i = 0; i < keys.length; i++) {
            keys[i].setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    Button b = (Button) v;
                    returnResult(b.getText());
                }});
        }
        keypad.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                return;
            }});
    }

    private void setText()
    {
        for(int i = 0; i < rack.size(); i++)
        {
            keys[i].setText(rack.get(i));
        }
    }
}
