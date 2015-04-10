package alonsojimenez.julien.datmusicplayer;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import Ice.IllegalMessageSizeException;


public class AddActivity extends ActionBarActivity
{
    private static final int PICK_COVER_REQUEST = 0;
    private static final int PICK_SONG_REQUEST = 1;
    private Uri coverUri = null;
    private Uri songUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
    }

    public void onValidate(View v)
    {
        if(songUri == null)
            return;

        EditText temp = (EditText)(findViewById(R.id.nameAdd));
        String name = temp.getText().toString();
        temp = (EditText)(findViewById(R.id.artistAdd));
        String artist = temp.getText().toString();

        String upName = (artist + "_" + name).replaceAll(" ", "_");
        String path = "songs/" + upName + ".mp3";
        System.out.println("PATH : " + path);

        if(coverUri != null)
            upload(coverUri, false, upName);

        upload(songUri, true, upName);

        ServerHandler.getServer().addSong(name, artist, path);
        Toast.makeText(getApplicationContext(), name + " by " + artist + " was successfully added",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    public void upload(Uri uri, boolean isSong, String name)
    {
        try
        {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            AssetFileDescriptor assetFileDescriptor = getContentResolver().openAssetFileDescriptor(uri, "r");
            int size = (int)assetFileDescriptor.getLength();
            System.out.println("DAT SIZE : " + size);

            byte[] data = new byte[size];

            BufferedInputStream buf = new BufferedInputStream(inputStream);
            buf.read(data, 0, data.length);
            buf.close();

            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));

            if(isSong)
                name = "songs/" + name + "." + ext;
            else
                name = "covers/" + name + "." + ext;

            int offset = 0;

            if(size <= ServerHandler.getMessageSizeMax())
            {
                System.out.println("FIRST BYTE : " + (int)data[0]);
                ServerHandler.getServer().write(name, offset, data);
            }

            else
            {
                int max = ServerHandler.getMessageSizeMax();
                while(offset < size)
                {
                    int end = offset + max;
                    if(end > size)
                        end = size;
                    byte[] temp = Arrays.copyOfRange(data, offset, end);
                    ServerHandler.getServer().write(name, offset, temp);
                    offset = end;
                }
            }
        }
        catch(IllegalMessageSizeException | IOException e)
        {
            System.out.println(e.getMessage());
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
                    System.out.println(e.getMessage());
                }
            }
            else if(requestCode == PICK_SONG_REQUEST)
            {
                this.songUri = data.getData();
                ((EditText)findViewById(R.id.pathAdd)).setText(songUri.toString());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Bitmap getScaledCover(Bitmap bmp, int width, int height)
    {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
    }
}
