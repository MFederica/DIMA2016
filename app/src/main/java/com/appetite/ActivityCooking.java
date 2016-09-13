package com.appetite;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appetite.R;
import com.appetite.model.Recipe;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.ISpeechDelegate;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.dto.SpeechConfiguration;
import com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import me.relex.circleindicator.CircleIndicator;

//TODO GESTIRE I PERMESSI A RUNTIME!

public class ActivityCooking extends AppCompatActivity implements ISpeechDelegate {
    private final static String TAG = ActivityCooking.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Recipe recipeSelected;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    //VOICE STUFF
    private static String voiceModel = "en-US_BroadbandModel";
    // session recognition results
    private static String mRecognitionResults = "";

    private enum ConnectionState {
        IDLE, CONNECTING, CONNECTED
    }

    ConnectionState mState = ConnectionState.IDLE;
    public JSONObject jsonVoices = null;
    private Handler mHandler = null;

    private CountDownTimer timer;
    private boolean timerRunning = false;
    private View timerLayoutView;
    private TextView timerView;
    private Ringtone r;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: " );
        super.onCreate(savedInstanceState);

        // Get the recipe from the intent
        Intent intent = getIntent();
        recipeSelected = intent.getParcelableExtra(ActivityMain.RECIPE_SELECTED);

        // Initialize view
        setContentView(R.layout.activity_cooking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each step, the ViewPager and the CircleIndicator
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.activity_cooking_indicator);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        indicator.setViewPager(mViewPager);
        mSectionsPagerAdapter.registerDataSetObserver(indicator.getDataSetObserver());

        // Initialize timer view
        CardView timerLayout = (CardView) findViewById(R.id.activity_cooking_timer);
        timerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStopTimerDialog();
            }
        });

        if (initSTT() == false) {
            Log.e(TAG, "onCreate: Error: no authentication credentials/token available, please enter your authentication information");
        }

        timerLayoutView = (View) findViewById(R.id.activity_cooking_timer);
        timerView = (TextView) findViewById(R.id.activity_cooking_timer_time);
        mHandler = new Handler();
        displayStatus("Not connected");

    }
    
    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: " );
        super.onResume(); //TODO sopra o sotto?
        if (mState == ConnectionState.IDLE) {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_free_hands), Context.MODE_PRIVATE);
            boolean allowFreeHands = sharedPref.getBoolean(getString(R.string.saved_free_hands),false);
            if(allowFreeHands) {
                // Ask for permission..
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    //..if we haven't it, ask it
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            Application.MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                } else {
                    //..if we have it, let's connect!
                    startFreeHandsMode();
                }
            }
        }
        // for the timer..
        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
        Log.i(TAG, "Registered broacast receiver");
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: " );
        stopRecognition();
        unregisterReceiver(br);
        Log.i(TAG, "Unregistered broacast receiver");
        super.onPause(); //TODO sopra o sotto?
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop: " );
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: " );
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_cooking, menu);

        // Initialize hands-free checkbox
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_free_hands), Context.MODE_PRIVATE);
        boolean allowFreeHands = sharedPref.getBoolean(getString(R.string.saved_free_hands),false);
        menu.findItem(R.id.action_free_hands).setChecked(allowFreeHands);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == android.R.id.home) {
//            Toast.makeText(ActivityCooking.this, "invoco onBackPressed()", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;

        } else if (id == R.id.timer) {
            if(timerRunning) {
                showStopTimerDialog();
            } else {
                showStartTimerDialog();
            }

        } else if (id == R.id.action_free_hands) {
            Log.d(TAG, "onOptionsItemSelected: Hands-Free Action clicked");

            // Write to sharedPreferences if we'll allow free_hands or not in the future
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_free_hands), Context.MODE_PRIVATE);
            boolean newCheckedStatus = !(sharedPref.getBoolean(getString(R.string.saved_free_hands), false));

            if(newCheckedStatus) {
                Log.d(TAG, "onOptionsItemSelected: Hands-Free Action TRUE");
                // Ask for permission..
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    //..if we haven't it, ask it
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            Application.MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                } else {
                    item.setChecked(true);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.saved_free_hands), true);
                    editor.commit();
                    //..if we have it, let's connect!

                    startFreeHandsMode();
                }
            } else {
                Log.e(TAG, "onOptionsItemSelected: Hands-Free Action FALSE");
                item.setChecked(false);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.saved_free_hands), false);
                editor.commit();

                // Disconnect if already running
                stopRecognition();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return FragmentCookingStep.newInstance(position, recipeSelected);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return recipeSelected.getStep().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
                return "STEP "+position;
        }
    }
    
    //VOICE STUFF

    public URI getHost(String url){
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean initSTT() {
        // initialize the connection to the Watson STT service
        String username = getString(R.string.STTdefaultUsername);
        String password = getString(R.string.STTdefaultPassword);
        String tokenFactoryURL = getString(R.string.STTdefaultTokenFactory);
        String serviceURL = "wss://stream.watsonplatform.net/speech-to-text/api";
        SpeechConfiguration sConfig = new SpeechConfiguration(SpeechConfiguration.AUDIO_FORMAT_OGGOPUS);
        SpeechToText.sharedInstance().initWithContext(this.getHost(serviceURL), getApplicationContext(), sConfig);
        // Basic Authentication
        SpeechToText.sharedInstance().setCredentials(username, password);
        SpeechToText.sharedInstance().setModel(voiceModel);
        SpeechToText.sharedInstance().setDelegate(this);
        return true;
    }

    public class ItemVoice {

        public JSONObject mObject = null;

        public ItemVoice(JSONObject object) {
            mObject = object;
        }

        public String toString() {
            try {
                return mObject.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    // delegages ----------------------------------------------

    public void onOpen() {
        Log.d(TAG, "onOpen");
        Log.d(TAG, "onOpen: successfully connected to the STT service");
        displayStatus("successfully connected to the STT service");
        mState = ConnectionState.CONNECTED;
    }

    public void onError(String error) {

        Log.e(TAG, error);
        mState = ConnectionState.IDLE;
    }

    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose, code: " + code + " reason: " + reason);
        Log.d(TAG, "onClose: connection closed");
        displayStatus("connection closed");
        mState = ConnectionState.IDLE;
    }

    public void onMessage(String message) {

        Log.d(TAG, "onMessage, message: " + message);
        try {
            JSONObject jObj = new JSONObject(message);
            // state message
            if(jObj.has("state")) {
                Log.d(TAG, "Status message: " + jObj.getString("state"));
            }
            // results message
            else if (jObj.has("results")) {
                //if has result
                Log.d(TAG, "Results message: ");
                JSONArray jArr = jObj.getJSONArray("results");
                for (int i=0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    JSONArray jArr1 = obj.getJSONArray("alternatives");
                    String str = jArr1.getJSONObject(0).getString("transcript");
                    // remove whitespaces if the language requires it
                    String model = voiceModel;
                    if (model.startsWith("ja-JP") || model.startsWith("zh-CN")) {
                        str = str.replaceAll("\\s+","");
                    }
                    // String strFormatted = Character.toUpperCase(str.charAt(0)) + str.substring(1);

                    if (obj.getString("final").equals("true")) {
                        // String stopMarker = (model.startsWith("ja-JP") || model.startsWith("zh-CN")) ? "。" : ". ";
                        // mRecognitionResults = strFormatted.substring(0,strFormatted.length()-1) + stopMarker; //TODO prima era +=
                        // Get a handler that can be used to post to the main thread
                        checkCommand(str);
                        Log.d(TAG, "onMessage     FINAL: " + str);
                    } else {
                        // Log.d(TAG, "onMessage NOT FINAL: "+ mRecognitionResults + strFormatted);
                    }
                    break;
                }
            } else {
                Log.d(TAG, "onMessage: unexpected data coming from stt server: \n" + message);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON");
            e.printStackTrace();
        }
    }

    private void displayStatus(final String status) {
        final Runnable runnableUi = new Runnable(){
            @Override
            public void run() {
                TextView textResult = (TextView)findViewById(R.id.activity_cooking_sttStatus);
                textResult.setText(status);
            }
        };
        new Thread(){
            public void run(){
                mHandler.post(runnableUi);
            }
        }.start();
    }

    public void onAmplitude(double amplitude, double volume) {
        //Logger.e(TAG, "amplitude=" + amplitude + ", volume=" + volume);
    }

    public void stopRecognition() {
        if (mState == ConnectionState.CONNECTED) {
            mState = ConnectionState.IDLE;
            Log.d(TAG, "onClickRecord: CONNECTED -> IDLE");
            SpeechToText.sharedInstance().stopRecognition();
        }
    }

    private void checkCommand(final String result) {
            //COMMAND NEXT
        if (result.contains(getResources().getString(R.string.STT_command_NEXT_1)) || result.contains(getResources().getString(R.string.STT_command_NEXT_2))) {
            Log.e(TAG, "checkCommands: NEXT" );
            int currentItem = mViewPager.getCurrentItem();
            if(currentItem < mSectionsPagerAdapter.getCount())
                changeStep(currentItem + 1);

            //COMMAND BACK
        } else if (result.contains(getResources().getString(R.string.STT_command_BACK_1)) || result.contains(getResources().getString(R.string.STT_command_BACK_2))) {
            Log.e(TAG, "checkCommands: BACK" );
            int currentItem = mViewPager.getCurrentItem();
            if(currentItem > 0)
                changeStep(currentItem - 1);

            //COMMAND STOP LISTENING
        } else if (result.contains(getResources().getString(R.string.STT_command_STOP_LISTENING))) {
            // Write to sharedPreferences if we'll allow free_hands or not in the future
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_free_hands), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.saved_free_hands), false);
            editor.commit();
            invalidateOptionsMenu();
            stopRecognition();
            Log.e(TAG, "checkCommands: STOP LISTENING" );

            //COMMAND START TIMER
        } else if (result.contains(getResources().getString(R.string.STT_command_START_TIMER_MINUTE)) || (((result.contains(getResources().getString(R.string.STT_command_START_TIMER_1)) || result.contains(getResources().getString(R.string.STT_command_START_TIMER_2)))) && result.contains(getResources().getString(R.string.STT_command_TIMER)))) {
            String[] numbers_0 = {"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};
            String[] numbers_1 = {"twenty", "thirty", "forty", "fifty", "sixty"};
            String[] numbers_2 = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
            int num = 0;
            for(int i = 0; i < numbers_0.length; i++) {
                if(result.contains(numbers_0[i])) {
                    num = 10 + i;
                    break;
                }
            }
            Log.e(TAG, "checkCommand: after numbers_0 " + num );
            //if no number in numbers_0 has been found num is still == 0
            if(num == 0) {
                for (int i = 0; i < numbers_1.length; i++) {
                    if (result.contains(numbers_1[i])) {
                        Log.e(TAG, "checkCommand: found "+ numbers_1[i] + ", i = " + i );
                        num = i*10 + 20;
                    }
                }
                Log.e(TAG, "checkCommand: after numbers_1 " + num );
                for (int i = 0; i < numbers_2.length; i++) {
                    if (result.contains(numbers_2[i])) {
                        num += i;
                        break;
                    }
                }
                Log.e(TAG, "checkCommand: after numbers_3 " + num );
            }
            Log.e(TAG, "checkCommand: after all " + num );
            startTimer(num * 60000);
            Log.e(TAG, "checkCommands: START TIMER" );

            //COMMAND STOP TIMER
        } else if (result.contains(getResources().getString(R.string.STT_command_STOP_TIMER_1)) || result.contains(getResources().getString(R.string.STT_command_STOP_TIMER_2)) || result.contains(getResources().getString(R.string.STT_command_STOP_TIMER_3))) {
            stopTimer();
            final Runnable runnableUi = new Runnable(){
                @Override
                public void run() {
                    timerLayoutView.setVisibility(View.GONE);
                }
            };
            new Thread(){
                public void run(){
                    mHandler.post(runnableUi);
                }
            }.start();
            Log.e(TAG, "checkCommands: END TIMER" );
        }
    }

    private void changeStep(final int position) {
        final Runnable runnableUi = new Runnable(){
            @Override
            public void run() {
                mViewPager.setCurrentItem(position);
            }
        };
        new Thread(){
            public void run(){
                mHandler.post(runnableUi);
            }
        }.start();
    }

    private void showStopTimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.activity_cooking_timer_dialog_running_message)
                .setTitle(R.string.activity_cooking_timer_dialog_title);
        builder.setPositiveButton(R.string.activity_cooking_timer_dialog_running_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                stopTimer();
                timerLayoutView.setVisibility(View.GONE);
            }
        });
        builder.setNegativeButton(R.string.activity_cooking_timer_dialog_running_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

            }
        });
        builder.show();
    }

    private void showStartTimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View viewMinutes = getLayoutInflater().inflate(R.layout.activity_cooking_timer_dialog, null);
        final NumberPicker numberPicker = (NumberPicker) viewMinutes.findViewById(R.id.timer_dialog_timePicker);
        final TextView minutesTextView = (TextView) viewMinutes.findViewById(R.id.timer_dialog_text);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal == 1) {
                    minutesTextView.setText(R.string.activity_cooking_timer_dialog_minute);
                } else {
                    minutesTextView.setText(R.string.activity_cooking_timer_dialog_minutes);
                }
            }
        });
        builder.setView(viewMinutes);
        builder.setTitle(R.string.activity_cooking_timer_dialog_title)
                .setPositiveButton(R.string.activity_cooking_timer_dialog_start, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: DONE");
                        timerLayoutView.setVisibility(View.VISIBLE);
                        //creating timer
                        startTimer(numberPicker.getValue()*60000); //TODO cambiare in 60000 se nn gia fatto
                    }
                });
        builder.show();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            if(millisUntilFinished != 0) {
                timerLayoutView.setVisibility(View.VISIBLE);
                timerView.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                ));
                Log.d(TAG, "onTick: seconds remaining: " + millisUntilFinished / 1000);
                timerRunning = true;
            } else {
                // Countdown reached
                timerLayoutView.setVisibility(View.GONE);
                timerRunning = false;
            }
        }
    }

    private void startTimer(int duration) {
        if(!timerRunning) {
            Intent intent = new Intent(getApplicationContext(), BroadcastService.class);
            intent.putExtra(BroadcastService.MILLIS, duration);
            startService(intent);
            Log.i(TAG, "Started service");
        }
    }

    private void stopTimer() {
        if(timerRunning) {
            stopService(new Intent(getApplicationContext(), BroadcastService.class));
            Log.i(TAG, "Stopped service");
            timerRunning = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Application.MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    try { //TODO rimuovere il try catch?
                        invalidateOptionsMenu();
                    } catch (NullPointerException e) {
                        Log.e(TAG, "onRequestPermissionsResult: NullPointerException" + e );
                    }
                    // Write to sharedPreferences if we'll allow free_hands or not in the future
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_free_hands), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.saved_free_hands), true);
                    editor.commit();

                    mState = ConnectionState.CONNECTING;
                    Log.d(TAG, "onClickRecord: IDLE -> CONNECTING");
                    mRecognitionResults = "";
                    SpeechToText.sharedInstance().setModel(voiceModel);
                    Log.d(TAG, "onCreate: connecting to the STT service...");
                    displayStatus("connecting to the STT service...");
                    // start recognition //TODO spostare in un retainedFragment?!
                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... none) {
                            SpeechToText.sharedInstance().recognize();
                            return null;
                        }
                    }.execute();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // Write to sharedPreferences if we'll allow free_hands or not in the future
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_free_hands), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.saved_free_hands), false);
                    editor.commit();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Connect to SpeechToText service
     */
    private void startFreeHandsMode() {
            mState = ConnectionState.CONNECTING;
            Log.d(TAG, "onClickRecord: IDLE -> CONNECTING");
            mRecognitionResults = "";
            SpeechToText.sharedInstance().setModel(voiceModel);
            Log.d(TAG, "onCreate: connecting to the STT service...");
            displayStatus("connecting to the STT service...");
            // start recognition //TODO spostare in un retainedFragment?!
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... none) {
                    SpeechToText.sharedInstance().recognize();
                    return null;
                }
            }.execute();
        }
    }