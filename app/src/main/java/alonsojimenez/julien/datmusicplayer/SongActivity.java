package alonsojimenez.julien.datmusicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import Player.song;


public class SongActivity extends ActionBarActivity
{
    private song s;
    private String token;
    private String streamURL;
    private MediaPlayer mediaPlayer;
    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_song);

        ParcelableSong parcelableSong = getIntent().getParcelableExtra("SONG");

        s = parcelableSong.getSong();

        if(s != null)
        {
            TextView temp = (TextView)findViewById(R.id.nameLabelSong);
            if(temp != null)
                temp.setText(s.name);

            temp = (TextView)findViewById(R.id.artistLabelSong);
            if(temp != null)
                temp.setText(s.artist);

            token = ServerHandler.getServer().start(s.path);
            ServerHandler.getServer().play(token);
            started = true;
            streamURL = "http://" + ServerHandler.getHostname() + ":"
                    + ServerHandler.getStreamingPort() + "/" + token;

            mediaPlayer = new MediaPlayer();

            //mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //mediaPlayer.setOnPreparedListener(new preparedHandler());
            mediaPlayer.setOnPreparedListener(new preparedHandler());

            try
            {
                mediaPlayer.setDataSource(streamURL);
            }
            catch(IOException e)
            {
                System.err.println(e.getMessage());
            }
            //isLoading = true;
            mediaPlayer.prepareAsync();

            //started = false;

            ((Button)findViewById(R.id.playButton)).setEnabled(false);
            ((Button)findViewById(R.id.pauseButton)).setEnabled(false);
            ((Button)findViewById(R.id.stopButton)).setEnabled(false);
            ((Button)findViewById(R.id.removeButton)).setEnabled(true);
        }

        else
            finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_view, menu);
        return true;
    }

    public void onRemove(View v)
    {
        ServerHandler.getServer().stop(token);
        ServerHandler.getServer().remove(s.path);
        finish();
    }

    public void onPlay(View v)
    {
        if(!started)
            ServerHandler.getServer().play(token);

        mediaPlayer.start();
        ((Button)findViewById(R.id.playButton)).setEnabled(false);
        ((Button)findViewById(R.id.pauseButton)).setEnabled(true);
        ((Button)findViewById(R.id.stopButton)).setEnabled(true);
        ((Button)findViewById(R.id.removeButton)).setEnabled(false);
    }

    public void onPause(View v)
    {
        mediaPlayer.pause();
        ((Button)findViewById(R.id.playButton)).setEnabled(true);
        ((Button)findViewById(R.id.pauseButton)).setEnabled(false);
        ((Button)findViewById(R.id.stopButton)).setEnabled(false);
        ((Button)findViewById(R.id.removeButton)).setEnabled(false);
    }

    public void onStopButton(View v)
    {
        //mediaPlayer.stop();
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        ((Button)findViewById(R.id.playButton)).setEnabled(true);
        ((Button)findViewById(R.id.pauseButton)).setEnabled(false);
        ((Button)findViewById(R.id.stopButton)).setEnabled(false);
        ((Button)findViewById(R.id.removeButton)).setEnabled(true);
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

    @Override
    public void onStop()
    {
        super.onStop();
        ServerHandler.getServer().stop(token);
    }

    private class preparedHandler extends Thread implements MediaPlayer.OnPreparedListener
    {
        @Override
        public void onPrepared(MediaPlayer m)
        {
            ((Button)findViewById(R.id.playButton)).setEnabled(true);
        }
    }
}
