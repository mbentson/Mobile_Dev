package edu.neu.madcourse.mattbentson;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.mattbentson.sudoku.R;
public class Dictionary extends Activity implements View.OnClickListener {

    List<String> wordList = new ArrayList<>();
    TextView listOfWords;
    EditText textEdit;
    int pointer = 0;
    String word;
    String tempWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        View clearButton = findViewById(R.id.dictionaryClearBtn);
        clearButton.setOnClickListener(this);

        View ackButton = findViewById(R.id.dictionaryAckBtn);
        ackButton.setOnClickListener(this);

        View backButton = findViewById(R.id.dictionaryBackBtn);
        backButton.setOnClickListener(this);

        listOfWords = (TextView) findViewById(R.id.listOfWords);
        textEdit = (EditText) findViewById(R.id.textInput);
        textEdit.setHint("Enter a Word");
        textEdit.addTextChangedListener(watch);

    }

    public void readList(String file) {
        try {
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open(file + ".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String word;
            if (is != null) {
                while ((word = reader.readLine()) != null) {
                    wordList.add(word);
                }
            }
            is.close();
        } catch (IOException e) {

        }
    }

    TextWatcher watch = new TextWatcher(){
        @Override
        public void afterTextChanged(Editable arg0) {

        }
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {

        }
        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {
            word = textEdit.getText().toString();
            if(word.length() == 2)
            {
                readList(word);
            } else {
                if(word.length() >= 3)
                {
                    for(int i = pointer; i < wordList.size(); i++)
                    {
                        tempWord = wordList.get(i);
                        if(tempWord.startsWith(word))
                        {
                            if(tempWord.length() == word.length() && (!listOfWords.getText().toString().contains(word + "\n")))
                            {
                                listOfWords.append(word + "\n");
                                beep();
                            }
                            break;
                        }
                    }
                }
            }
        }
    };

    public void beep()
    {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dictionaryClearBtn:
                textEdit.setText("");
                listOfWords.setText("");
                wordList.clear();
                break;
            case R.id.dictionaryAckBtn:
                Intent i = new Intent(this, DictionaryAcks.class);
                startActivity(i);
                break;
            case R.id.dictionaryBackBtn:
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dictionary, menu);
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
