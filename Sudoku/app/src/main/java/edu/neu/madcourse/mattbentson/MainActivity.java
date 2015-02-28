package edu.neu.madcourse.mattbentson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import edu.neu.madcourse.mattbentson.sudoku.R;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View aboutButton = findViewById(R.id.aboutBtn);
        aboutButton.setOnClickListener(this);

        View sudokuButton = findViewById(R.id.sudokuBtn);
        sudokuButton.setOnClickListener(this);

        View errorButton = findViewById(R.id.errorBtn);
        errorButton.setOnClickListener(this);

        View dictionaryButton = findViewById(R.id.dictionaryBtn);
        dictionaryButton.setOnClickListener(this);

        View wordFadeButton = findViewById(R.id.wordFadeBtn);
        wordFadeButton.setOnClickListener(this);

        View quitButton = findViewById(R.id.quitBtn);
        quitButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutBtn:
                Intent a = new Intent(this, AboutMe.class);
                startActivity(a);
                break;
            case R.id.sudokuBtn:
                Intent s = new Intent(this, Sudoku.class);
                startActivity(s);
                break;
            case R.id.errorBtn:
                Intent e = new Intent();
                startActivity(e);
                break;
            case R.id.dictionaryBtn:
                Intent d = new Intent(this, Dictionary.class);
                startActivity(d);
                break;

            case R.id.wordFadeBtn:
                Intent w = new Intent(this, WordfadeMenu.class);
                startActivity(w);
                break;

            case R.id.quitBtn:
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
