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
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import Ice.AsyncResult;
import Ice.Communicator;
import Player.ServerPrx;
import PocketSphinxIce.*;


public class MainActivity extends ActionBarActivity
{
    private static final int REC_SR = 16000;
    private static final int REC_CHAN = AudioFormat.CHANNEL_IN_MONO;
    private static final int REC_ENC = AudioFormat.ENCODING_PCM_16BIT;
    AudioRecord audioRecord = null;
    int bufferSize;
    short[] recordData;
    int position;
    boolean recording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ServerHandler.initCommunicator();
        ServerHandler.initServer();

        VoiceServerHandler.initCommunicator();
        VoiceServerHandler.initServer();

        bufferSize = AudioRecord.getMinBufferSize(REC_SR, REC_CHAN, REC_ENC);
        recordData = new short[50*bufferSize];
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                REC_SR, REC_CHAN,
                REC_ENC, bufferSize);

        setContentView(R.layout.activity_main);
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

            new Thread(new Runnable()
            {
                @Override
                public void run()
                {

                    String result = null;
                    try
                    {
                        /*ProgressBar spinner = (ProgressBar)findViewById(R.id.spinner);
                        AsyncResult asyncResult = VoiceServerHandler.getServer().begin_decode(recordData);
                        spinner.setVisibility(View.VISIBLE);

                        asyncResult.waitForCompleted();
                        result = VoiceServerHandler.getServer().end_decode(asyncResult);
                        spinner.setVisibility(View.GONE);*/
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
        }
    }
}
