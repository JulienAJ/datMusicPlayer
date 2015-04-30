package alonsojimenez.julien.datmusicplayer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
{
    boolean recording = false;
    IVoiceRecognizer voiceRecognizer = new SphinxVoiceRecognizer();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ServerHandler.initCommunicator();
        ServerHandler.initServer();

        voiceRecognizer.init(getApplicationContext());

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
            voiceRecognizer.start();
            recording = true;
        }
        else
        {
            voiceRecognizer.stop();
            recording = false;
        }
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
