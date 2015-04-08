package edu.neu.madcourse.mattbentson;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import edu.neu.madcourse.mattbentson.sudoku.R;


public class Communication extends Activity implements View.OnClickListener{

    private EditText valueText;
    private TextView scoreList;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_ALERT_TEXT = "alertText";
    public static final String PROPERTY_TITLE_TEXT = "titleText";
    public static final String PROPERTY_CONTENT_TEXT = "contentText";
    public static final String PROPERTY_NTYPE = "nType";
    List<String> regIds = new ArrayList<String>();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "GCM Sample Demo";
    TextView mDisplay;
    EditText mMessage;
    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    Context context;
    String regid;
    AtomicInteger msgId = new AtomicInteger();

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);

        valueText = (EditText) findViewById(R.id.communicationObjectValue);
        scoreList = (TextView) findViewById(R.id.communicationObjectList);

        Button sendScoreButton = (Button) findViewById(R.id.communicationSendScoreButton);
        sendScoreButton.setOnClickListener(this);

        Button getScoresButton = (Button) findViewById(R.id.communicationGetScoresButton);
        getScoresButton.setOnClickListener(this);

        Button registerButton = (Button) findViewById(R.id.communicationRegisterBtn);
        registerButton.setOnClickListener(this);

        Button unRegisterButton = (Button) findViewById(R.id.communicationUnregisterBtn);
        unRegisterButton.setOnClickListener(this);

        Button sendMessageButton = (Button) findViewById(R.id.communicationSendMessageBtn);
        sendMessageButton.setOnClickListener(this);

        Button clearMessageButton = (Button) findViewById(R.id.communicationClearMessageBtn);
        clearMessageButton.setOnClickListener(this);

        mDisplay = (TextView) findViewById(R.id.communicationDisplayText);
        mMessage = (EditText) findViewById(R.id.communicationMessageText);
        gcm = GoogleCloudMessaging.getInstance(this);
        context = getApplicationContext();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.communicationSendScoreButton:
                sendData();
                break;
            case R.id.communicationGetScoresButton:
                getData();
                break;
            case R.id.communicationSendMessageBtn:
                String message = ((EditText) findViewById(R.id.communicationMessageText))
                        .getText().toString();
                if (message != "") {
                    regIds.clear();
                    getIdsToSend(message);
                } else {
                    Toast.makeText(context, "Sending Context Empty!",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.communicationClearMessageBtn:
                mMessage.setText("");
                break;
            case R.id.communicationUnregisterBtn:
                deleteInParse();
                unregister();
                break;
            case R.id.communicationRegisterBtn:
                if (checkPlayServices()) {
                    regid = getRegistrationId(context);
                    if (TextUtils.isEmpty(regid)) {
                        registerInBackground();
                    }
                }
                break;
        }
    }

    private void sendData()
    {
        if(!valueText.getText().toString().equals("")) {
            ParseObject object = new ParseObject("HighScore");
            object.put("score", valueText.getText().toString());
            object.saveInBackground();
            valueText.setText("");
        }
    }

    private void getData()
    {
        scoreList.setText("High scores:");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HighScore");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for(ParseObject parseObject : parseObjects) {
                        scoreList.setText(scoreList.getText() + "\n" + parseObject.getString("score"));
                    }
                }
            }
        });
    }

    private void deleteInParse()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RegistrationID");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for(ParseObject parseObject : parseObjects) {
                        if(parseObject.getString("identifier").equals(regid))
                        {
                            parseObject.deleteInBackground();
                        }
                    }
                }
            }
        });
    }

    private void getIdsToSend(final String message)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RegistrationID");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for(ParseObject parseObject : parseObjects) {
                        regIds.add(parseObject.getString("identifier"));
                    }

                    sendMessage(message);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_communication, menu);
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("NewApi")
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        Log.i(TAG, String.valueOf(registeredVersion));
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(Communication.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private static void setRegisterValues() {
        CommunicationConstants.alertText = "Register Notification";
        CommunicationConstants.titleText = "Register";
        CommunicationConstants.contentText = "Registering Successful!";
    }

    private static void setUnregisterValues() {
        CommunicationConstants.alertText = "Unregister Notification";
        CommunicationConstants.titleText = "Unregister";
        CommunicationConstants.contentText = "Unregistering Successful!";
    }

    private static void setSendMessageValues(String msg) {
        CommunicationConstants.alertText = "Message Notification";
        CommunicationConstants.titleText = "Sending Message";
        CommunicationConstants.contentText = msg;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    setRegisterValues();
                    regid = gcm.register(CommunicationConstants.GCM_SENDER_ID);


                    // implementation to store and keep track of registered devices here


                    msg = "Device registered, registration ID=" + regid;
                    sendRegistrationIdToBackend();
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() {
        ParseObject object = new ParseObject("RegistrationID");
        object.put("identifier", regid);
        object.saveInBackground();
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void unregister() {
        Log.d(CommunicationConstants.TAG, "UNREGISTER USERID: " + regid);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    msg = "Sent unregistration";
                    setUnregisterValues();
                    gcm.unregister();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                removeRegistrationId(getApplicationContext());
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                ((TextView) findViewById(R.id.communicationDisplayText))
                        .setText(regid);
            }
        }.execute();
    }

    private void removeRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(CommunicationConstants.TAG, "Removing regId on app version "
                + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.commit();
        regid = null;
    }

    @SuppressLint("NewApi")
    private void sendMessage(final String message) {
        if (regid == null || regid.equals("")) {
            Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (message.isEmpty()) {
            Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";

                int nIcon = R.drawable.ic_stat_cloud;
                int nType = CommunicationConstants.SIMPLE_NOTIFICATION;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.alertText", "Notification");
                msgParams.put("data.titleText", "Notification Title");
                msgParams.put("data.contentText", message);
                msgParams.put("data.nIcon", String.valueOf(nIcon));
                msgParams.put("data.nType", String.valueOf(nType));
                setSendMessageValues(message);
                GcmNotification gcmNotification = new GcmNotification();

                for(int i = 0; i < regIds.size(); i++)
                {
                    Log.d("Outputting reg ids",regIds.get(i)+"");
                }

                gcmNotification.sendNotification(msgParams, regIds,
                        edu.neu.madcourse.mattbentson.Communication.this);

                msg = "sending information...";

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }
}
