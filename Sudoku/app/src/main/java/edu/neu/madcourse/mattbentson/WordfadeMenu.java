package edu.neu.madcourse.mattbentson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import edu.neu.madcourse.mattbentson.sudoku.R;


public class WordfadeMenu extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordfade_menu);

        View playButton = findViewById(R.id.wordfadePlayBtn);
        playButton.setOnClickListener(this);

        View ackButton = findViewById(R.id.wordfadeAckBtn);
        ackButton.setOnClickListener(this);

        View instructButton = findViewById(R.id.wordfadeInstructBtn);
        instructButton.setOnClickListener(this);

        View backButton = findViewById(R.id.wordfadeBackBtn);
        backButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wordfadePlayBtn:
                Intent p = new Intent(this, WordFade.class);
                startActivity(p);
                break;
            case R.id.wordfadeAckBtn:
                Intent a = new Intent(this, WordfadeAcks.class);
                startActivity(a);
                break;
            case R.id.wordfadeInstructBtn:
                Intent i = new Intent(this, WordfadeInstructions.class);
                startActivity(i);
                break;
            case R.id.wordfadeBackBtn:
                finish();
                break;
        }
    }
}
