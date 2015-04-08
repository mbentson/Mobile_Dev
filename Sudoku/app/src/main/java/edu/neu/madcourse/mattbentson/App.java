package edu.neu.madcourse.mattbentson;

import android.app.Application;
import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "ayRPTKZG1p725fll9yXmHGdLb55H9wDv0LY6YJWJ", "ZGSzrwgXhmSVzMy4TBS42qIkttAI08pnz9NkuSgx");
    }
}