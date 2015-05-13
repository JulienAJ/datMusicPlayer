package alonsojimenez.julien.datmusicplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import Ice.IllegalMessageSizeException;
import alonsojimenez.julien.datmusicplayer.musicServer.ServerHandler;


public class AddActivity extends ActionBarActivity
{
    private boolean params = false;
    private String title = null;
    private String artist = null;

    private static final int PICK_COVER_REQUEST = 0;
    private static final int PICK_SONG_REQUEST = 1;
    private Uri coverUri = null;
    private Uri songUri = null;
    private ProgressDialog progressDialog = null;

    private String serverIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        params = getIntent().getExtras().getBoolean("params");

        if(params)
        {
            title = getIntent().getExtras().getString("title");
            artist = getIntent().getExtras().getString("artist");

            if(title != null)
                ((EditText)findViewById(R.id.nameAdd)).setText(title);
            if(artist != null)
                ((EditText)findViewById(R.id.artistAdd)).setText(artist);
        }
    }

    public void onValidate(View v)
    {
        if(songUri == null)
        {
            Log.e("Validate Add", "No Song Selected");
            Toast.makeText(getApplicationContext(), getString(R.string.noSong), Toast.LENGTH_SHORT);
            return;
        }
        if(((EditText)findViewById(R.id.nameAdd)).getText().toString().equals(""))
        {
            Log.e("Validate Add", "No title");
            Toast.makeText(getApplicationContext(), getString(R.string.noTitle), Toast.LENGTH_SHORT);
            return;
        }
        if(((EditText)findViewById(R.id.artistAdd)).getText().toString().equals(""))
        {
            Log.e("Validate Add", "No artist");
            Toast.makeText(getApplicationContext(), getString(R.string.noArtist), Toast.LENGTH_SHORT);
            return;
        }
        new Async().execute();
    }

    public void upload(Uri uri, boolean isSong, String name)
    {
        try
        {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            AssetFileDescriptor assetFileDescriptor = getContentResolver().openAssetFileDescriptor(uri, "r");
            int size = (int)assetFileDescriptor.getLength();

            byte[] data = new byte[size];

            BufferedInputStream buf = new BufferedInputStream(inputStream);
            buf.read(data, 0, data.length);
            buf.close();

            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));

            name = name + "." + ext;

            int offset = 0;

            serverIdentifier = ServerHandler.getLessLoadedServer();
            if(serverIdentifier == null)
                return;

            if(size <= ServerHandler.getMessageSizeMax(serverIdentifier))
            {
                ServerHandler.write(serverIdentifier, name, offset, data);
            }

            else
            {
                int max = ServerHandler.getMessageSizeMax(serverIdentifier);
                while(offset < size)
                {
                    int end = offset + max;
                    if(end > size)
                        end = size;
                    byte[] temp = Arrays.copyOfRange(data, offset, end);
                    ServerHandler.write(serverIdentifier, name, offset, temp);
                    offset = end;
                }
            }
        }
        catch(IllegalMessageSizeException | IOException e)
        {
            Log.e("Upload Exception", e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == PICK_COVER_REQUEST)
            {
                try
                {
                    this.coverUri = data.getData();
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(this.coverUri));
                    ((ImageView) findViewById(R.id.imgView)).setImageBitmap(getScaledCover(bitmap, 250, 250));
                }
                catch (FileNotFoundException e)
                {
                    Log.e("Cover Pick Exception", e.getMessage());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                }
            }
            else if(requestCode == PICK_SONG_REQUEST)
            {
                this.songUri = data.getData();
                ((EditText)findViewById(R.id.pathAdd)).setText(songUri.toString());
                if(!params)
                {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(this, songUri);
                    title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                    ((EditText)findViewById(R.id.nameAdd)).setText(title);
                    ((EditText)findViewById(R.id.artistAdd)).setText(artist);
                }
            }
        }
    }

    public void onChooseImg(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_COVER_REQUEST);
    }

    public void onChooseSong(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_SONG_REQUEST);
    }

    public Bitmap getScaledCover(Bitmap bmp, int width, int height)
    {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
    }

    private class Async extends AsyncTask<Void, Integer, Void>
    {
        private String name, artist, upName, path;

        protected void onPreExecute()
        {
            EditText temp = (EditText)(findViewById(R.id.nameAdd));
            name = temp.getText().toString();
            temp = (EditText)(findViewById(R.id.artistAdd));
            artist = temp.getText().toString();

            upName = (artist + "_" + name).replaceAll(" ", "_");
            path = upName + ".mp3";

            progressDialog = new ProgressDialog(AddActivity.this);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            if(coverUri != null)
                upload(coverUri, false, upName);

            upload(songUri, true, upName);

            return null;
        }

        public void onPostExecute(Void result)
        {
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            ServerHandler.addSong(serverIdentifier, name, artist, path);
            Toast.makeText(getApplicationContext(), name + " " + getString(R.string.by) + " "
                    + artist + " " + getString(R.string.addSuccess), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
