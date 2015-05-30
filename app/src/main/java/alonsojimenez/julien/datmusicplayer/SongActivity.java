package alonsojimenez.julien.datmusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import Player.song;
import alonsojimenez.julien.datmusicplayer.musicServer.ServerHandler;


public class SongActivity extends ActionBarActivity
{
    private song s;
    private String serverId = null;
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
        boolean isRemove = getIntent().getExtras().getBoolean("isRemove", false);

        if(s != null)
        {
            serverId = ServerHandler.cutPath(s.path, true);
            s.path = ServerHandler.cutPath(s.path, false);
            String coverPath = s.coverPath;
            if(coverPath != null && coverPath != "" && coverPath.length() != 0)
            {
                int size = ServerHandler.getFileSize(serverId, s.coverPath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream(size);
                try
                {
                    int offset = 0;
                    int max = ServerHandler.getMessageSizeMax(serverId);
                    while(offset < size)
                    {
                        int end = offset + max;
                        if(end > size)
                            end = size;

                        byte[] data = ServerHandler.read(serverId, s.coverPath, offset, end);
                        stream.write(data);
                        offset = end;
                    }

                    byte[] image = stream.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    ((ImageView) findViewById(R.id.imgView2)).setImageBitmap(getScaledCover(bitmap, 250, 250));

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else
            {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.music_launcher);
                ((ImageView) findViewById(R.id.imgView2)).setImageBitmap(getScaledCover(bitmap, 250, 250));
            }
            TextView temp = (TextView)findViewById(R.id.nameLabelSong);
            if(temp != null)
                temp.setText(s.name);

            temp = (TextView)findViewById(R.id.artistLabelSong);
            if(temp != null)
                temp.setText(s.artist);

            token = ServerHandler.start(serverId, s.path);
            ServerHandler.play(serverId, token);
            started = true;
            streamURL = "http://" + ServerHandler.getHostname(serverId) + ":"
                    + ServerHandler.getStreamingPort(serverId) + "/" + token;

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new preparedHandler());

            try
            {
                mediaPlayer.setDataSource(streamURL);
            }
            catch(IOException e)
            {
                System.err.println(e.getMessage());
            }
            mediaPlayer.prepareAsync();

            ((Button)findViewById(R.id.playButton)).setEnabled(false);
            ((Button)findViewById(R.id.pauseButton)).setEnabled(false);
            ((Button)findViewById(R.id.stopButton)).setEnabled(false);
            ((Button)findViewById(R.id.removeButton)).setEnabled(true);

            if(isRemove)
                onRemove(null);
        }

        else
            finish();

    }

    public Bitmap getScaledCover(Bitmap bmp, int width, int height)
    {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
    }

    public void onRemove(View v)
    {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirmRemoveTitle))
                .setMessage(getString(R.string.confirmRemoveText) + ' ' + s.name + '?')
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ServerHandler.stop(serverId, token);
                        ServerHandler.remove(serverId, s.path);
                        Toast.makeText(getApplicationContext(), s.name + " " + getString(R.string.by)
                                + " " + s.artist + getString(R.string.removeSuccess), Toast.LENGTH_SHORT).show();
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public void onPlay(View v)
    {
        if(!started)
            ServerHandler.play(serverId, token);

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
    public void onStop()
    {
        super.onStop();
        ServerHandler.stop(serverId, token);
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
