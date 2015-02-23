package edu.neu.madcourse.mattbentson;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import edu.neu.madcourse.mattbentson.sudoku.R;

public class WordFade extends Activity {
    GridView grid;
    int[] imageId = new int[961];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_fade);
        for (int i = 0; i < 961; i++)
        {
            imageId[i] = R.drawable.test;
        }
        final WordFadeGrid adapter = new WordFadeGrid(WordFade.this, imageId);
        grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(WordFade.this, "You Clicked at " + position, Toast.LENGTH_SHORT).show();
                adapter.changePic(R.drawable.applogo,position);
                adapter.notifyDataSetChanged();
                grid.setAdapter(adapter);
            }
        });
    }
}