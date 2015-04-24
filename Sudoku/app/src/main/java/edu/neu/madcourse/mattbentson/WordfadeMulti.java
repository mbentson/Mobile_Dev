package edu.neu.madcourse.mattbentson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.mattbentson.sudoku.R;


public class WordfadeMulti extends Activity implements View.OnClickListener {

    final ArrayList<String> listOfNames = new ArrayList<>();
    String nameToPlay = "";
    String idToPlay = "";
    TextView username;
    String playerNum = "";
    int scoreOne = 0;
    int scoreTwo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordfade_multi);

        View ackBtn = findViewById(R.id.wordfadeMultiAcksBtn);
        ackBtn.setOnClickListener(this);

        View findGameBtn = findViewById(R.id.findGameBtn);
        findGameBtn.setOnClickListener(this);

        View quitBtn = findViewById(R.id.multiBackBtn);
        quitBtn.setOnClickListener(this);

        username = (TextView) findViewById(R.id.usernameText);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wordfadeMultiAcksBtn:
                Intent w2 = new Intent(this, WordfadeMulti.class);
                startActivity(w2);
                break;

            case R.id.findGameBtn:
                checkForGame();
                break;

            case R.id.multiBackBtn:
                finish();
                break;
        }
    }

    boolean gameFound = false;
    public void checkForGame()
    {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("FinishedGames");
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null){
                    for (ParseObject parseObject : parseObjects) {
                        if(findInGame(parseObject.getString("gameName")))
                        {
                            if(findTurn(parseObject.getString("gameName")))
                            {
                                nameToPlay = "";
                                playerNum = "one";
                                scoreOne = parseObject.getInt("one");
                                scoreTwo = parseObject.getInt("two");
                                parseObject.deleteInBackground();
                                showResults();
                                playerNum = "";
                            }
                        }
                    }
                }
            }
        });

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("MultiplayerGames");
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null){
                    for (ParseObject parseObject : parseObjects) {
                        if(findInGame(parseObject.getString("gameName")))
                        {
                            gameFound = true;
                            if(findTurn(parseObject.getString("gameName")))
                            {
                                playerNum = "two";
                                sendMessage("You were invited to play " + nameToPlay);
                                startGame();
                            } else {
                                sendMessage("You're opponent has not played yet");
                            }
                        }
                    }
                    if(!gameFound)
                    {
                        findGame();
                    }
                }
            }
        });
    }

    public boolean findInGame(String gameName)
    {
        if(gameName.contains(username.getText().toString()))
        {
            return true;
        } else {
            return false;
        }
    }

    public boolean findTurn(String gameName)
    {
        if(gameName.indexOf(username.getText().toString()) == 0)
        {
            nameToPlay = gameName.substring(gameName.indexOf("-") + 1);
            return false;
        } else {
            nameToPlay = gameName.substring(0,gameName.indexOf("-"));
            return true;
        }
    }

    boolean isWaitingPlayer = false;
    boolean addPlayer = true;
    public void findGame()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("WaitingPlayers");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null){
                    listOfNames.clear();
                    for (ParseObject parseObject : parseObjects) {
                        if(!parseObject.getString("username").equals(username.getText().toString())) {
                            isWaitingPlayer = true;
                            addToList(parseObject.getString("username"));
                            idToPlay = parseObject.getObjectId();
                        } else {
                            sendMessage("Username is already waiting for a game");
                            addPlayer = false;
                        }
                    }
                    if(isWaitingPlayer)
                    {
                        setListView();
                    } else {
                        if(addPlayer) {
                            ParseObject object = new ParseObject("WaitingPlayers");
                            object.put("username", username.getText().toString());
                            object.saveInBackground();
                            sendMessage("Sorry there is no one to play with");
                        }
                        addPlayer = true;
                    }
                }
            }
        });
    }

    public void sendMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    public void addToList(String name)
    {
        listOfNames.add(name);
    }

    public void startGame()
    {
        Intent p = new Intent(this, WordFade.class);
        startActivityForResult(p, 1);
    }

    public void startNewGame()
    {
        playerNum = "one";
        ParseObject object = new ParseObject("MultiplayerGames");
        object.put("gameName", username.getText().toString() + "-" + nameToPlay);
        object.put("one",0);
        object.put("two",0);
        object.saveInBackground();
        startGame();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (requestCode == 1) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("MultiplayerGames");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null){
                        for (ParseObject parseObject : parseObjects) {
                            if(parseObject.getString("gameName").contains(username.getText())) {
                                parseObject.put(playerNum, Integer.parseInt(data.getStringExtra("score")));
                                parseObject.saveInBackground();
                                if(playerNum.equals("two"))
                                {
                                    scoreOne = parseObject.getInt("one");
                                    scoreTwo = parseObject.getInt("two");
                                    ParseObject object2 = new ParseObject("FinishedGames");
                                    object2.put("gameName", username.getText().toString() + "-" + nameToPlay);
                                    object2.put("one",scoreOne);
                                    object2.put("two",scoreTwo);
                                    object2.saveInBackground();
                                    parseObject.deleteInBackground();
                                    showResults();
                                }
                            } else {
                            }
                        }
                    }
                }
            });
            if(playerNum.equals("one"))
            {
                Toast.makeText(this,"Your score has been sent",Toast.LENGTH_LONG).show();
            }

            Intent w2 = new Intent(this, WordfadeMulti.class);
            finish();
            startActivity(w2);
        }
    }

    public void showResults()
    {
        if(playerNum.equals("two"))
        {
            if(scoreOne > scoreTwo)
            {
                Toast.makeText(this,"You lost " + scoreOne + " to " + scoreTwo,Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"You won " + scoreTwo + " to " + scoreOne,Toast.LENGTH_LONG).show();
            }
        } else {
            if(scoreOne > scoreTwo)
            {
                Toast.makeText(this,"You won your last game " + scoreOne + " to " + scoreTwo,Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"You lost your last game " + scoreTwo + " to " + scoreOne,Toast.LENGTH_LONG).show();
            }
        }
    }
    private void setListView()
    {
        final ListView listview = (ListView)findViewById(R.id.multiListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1,
                android.R.id.text1,
                listOfNames);
        //assign adapter to the list view
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nameToPlay = (String)listview.getItemAtPosition(position);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("WaitingPlayers");
                query.getInBackground(idToPlay , new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            object.deleteInBackground();
                        } else {
                            // something went wrong
                        }
                    }
                });
                startNewGame();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wordfade_multi, menu);
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
