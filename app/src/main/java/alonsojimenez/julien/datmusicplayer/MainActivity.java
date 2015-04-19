package alonsojimenez.julien.datmusicplayer;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Ice.AsyncResult;
import Ice.Communicator;
import Player.ServerPrx;
import PocketSphinxIce.*;


public class MainActivity extends ActionBarActivity
{
    /*private static final int REC_SR = 16000;
    private static final int REC_CHAN = AudioFormat.CHANNEL_IN_MONO;
    private static final int REC_ENC = AudioFormat.ENCODING_PCM_16BIT;
    AudioRecord audioRecord = null;
    int bufferSize;
    short[] recordData;
    int position;*/
    boolean recording = false;
    SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ServerHandler.initCommunicator();
        ServerHandler.initServer();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(new recognitionListener());

        VoiceServerHandler.initCommunicator();
        VoiceServerHandler.initServer();

        /*bufferSize = AudioRecord.getMinBufferSize(REC_SR, REC_CHAN, REC_ENC);
        recordData = new short[50*bufferSize];
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                REC_SR, REC_CHAN,
                REC_ENC, bufferSize);*/

        setContentView(R.layout.activity_main);

        setupSearchInputHandlers();
    }

    public void onAction(View v)
    {
        Intent intent = null;

        if(findViewById(R.id.add) == v)
            intent = new Intent(this, AddActivity.class);

        else if(findViewById(R.id.artistSearchButton) == v)
        {
            TextView temp = (TextView)findViewById(R.id.artistSearchField);
            String key = temp.getText().toString();
            if(key != null && key != "")
            {
                intent = new Intent(this, SearchResultsActivity.class);
                intent.putExtra("searchKey", key);
                intent.putExtra("isArtist", true);
                intent.putExtra("isList", false);
            }
        }

        else if(findViewById(R.id.titleSearchButton) == v)
        {
            TextView temp = (TextView)findViewById(R.id.titleSearchField);
            String key = temp.getText().toString();
            if(key != null && key != "")
            {
                intent = new Intent(this, SearchResultsActivity.class);
                intent.putExtra("searchKey", key);
                intent.putExtra("isArtist", false);
                intent.putExtra("isList", false);
            }
        }

        else if(findViewById(R.id.listButton) == v)
        {
            intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra("isList", true);
        }

        if(intent != null)
            startActivity(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ServerHandler.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRecord(View v)
    {
        if(!recording)
        {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizer.startListening(intent);
            recording = true;
        }
        else
        {
            speechRecognizer.stopListening();
            recording = false;
        }

        /*if(!recording)
        {
            recording = true;
            position = 0;
            audioRecord.startRecording();
            new Thread(new Runnable() {
                public void run() {
                    while (position < bufferSize * 50 && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                        int temp = audioRecord.read(recordData, position, bufferSize);
                        position += temp;
                    }
                }
            }).start();
        }
        else
        {
            recording = false;
            audioRecord.stop();

            new Thread(new Runnab
            le()
            {
                @Override
                public void run()
                {

                    String result = null;
                    try
                    {
                        //ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner);
                        //AsyncResult asyncResult = VoiceServerHandler.getServer().begin_decode(recordData);
                        //spinner.setVisibility(View.VISIBLE);

                        //asyncResult.waitForCompleted();
                        //result = VoiceServerHandler.getServer().end_decode(asyncResult);
                        //spinner.setVisibility(View.GONE);
                        result = VoiceServerHandler.getServer().decode(recordData);
                    }
                    catch(PocketSphinxIce.Error e)
                    {
                        System.out.println(e.what);
                    }
                    if(result != null)
                    {
                        System.out.println("RESULTAT : " + result);
                    }
                }
            }).start();
        }*/
    }

    private class recognitionListener implements RecognitionListener
    {
        @Override
        public void onReadyForSpeech(Bundle params)
        {}

        @Override
        public void onBeginningOfSpeech()
        {}

        @Override
        public void onRmsChanged(float rmsdB)
        {}

        @Override
        public void onBufferReceived(byte[] buffer)
        {}

        @Override
        public void onEndOfSpeech()
        {}

        @Override
        public void onError(int error)
        {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio recording error.";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "Other client side errors.";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Other network related errors.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network operation timed out.";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "No recognition result matched.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy.";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "Server sends error status.";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Error";
            }
            Log.e("SpeechRecognizer Error", message);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        }

        @Override
        public void onResults(Bundle results)
        {
            String s = "";
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i = 0; i < data.size(); i++)
            {
                s += data.get(i);
            }
            Log.e("Speech Reco Output", data.get(0).toString());
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {}

        @Override
        public void onEvent(int eventType, Bundle params)
        {}

    }

    private void setupSearchInputHandlers()
    {
        ((EditText)findViewById(R.id.artistSearchField)).setOnEditorActionListener(
                new TextView.OnEditorActionListener()
                {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                    {
                        if(actionId == EditorInfo.IME_ACTION_SEARCH)
                        {
                            onAction(findViewById(R.id.artistSearchButton));
                            return true;
                        }
                        return false;
                    }
                }
        );

        ((EditText)findViewById(R.id.titleSearchField)).setOnEditorActionListener(
                new TextView.OnEditorActionListener()
                {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                    {
                        if(actionId == EditorInfo.IME_ACTION_SEARCH)
                        {
                            onAction(findViewById(R.id.titleSearchButton));
                            return true;
                        }
                        return false;
                    }
                }
        );
    }
}
