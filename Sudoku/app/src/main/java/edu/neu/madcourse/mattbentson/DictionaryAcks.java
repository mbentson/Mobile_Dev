package edu.neu.madcourse.mattbentson;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import edu.neu.madcourse.mattbentson.sudoku.R;


public class DictionaryAcks extends Activity implements View.OnClickListener {

    TextView linkText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_acks);

        View backButton = findViewById(R.id.dictAckBackBtn);
        backButton.setOnClickListener(this);

        linkText = (TextView) findViewById(R.id.dictAckTxt);
        linkText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dictAckBackBtn:
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dictionary_acks, menu);
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
