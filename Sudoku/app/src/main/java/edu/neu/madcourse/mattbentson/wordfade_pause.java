package edu.neu.madcourse.mattbentson;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.mattbentson.sudoku.R;


public class wordfade_pause extends Dialog {

    Button resumeBtn;
    WordFade wordfade;

    public wordfade_pause( ){
        super(null);
    }

    public wordfade_pause(Context context) {
        super(context);
        wordfade = (WordFade) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordfade_pause);
        resumeBtn = (Button) findViewById(R.id.wordfadeResumeBtn);
        resumeBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                wordfade.callResume();
                dismiss();
            }});
    }
}
