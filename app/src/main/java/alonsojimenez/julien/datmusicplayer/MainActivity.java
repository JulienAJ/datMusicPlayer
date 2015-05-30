package alonsojimenez.julien.datmusicplayer;

import android.content.Intent;
import android.os.StrictMode;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import alonsojimenez.julien.datmusicplayer.commandParser.Action;
import alonsojimenez.julien.datmusicplayer.commandParser.Command;
import alonsojimenez.julien.datmusicplayer.commandParser.CommandParserHandler;
import alonsojimenez.julien.datmusicplayer.musicServer.ServerHandler;
import alonsojimenez.julien.datmusicplayer.voiceRecognition.AndroidVoiceRecognizer;
import alonsojimenez.julien.datmusicplayer.voiceRecognition.IRecognitionResultListener;
import alonsojimenez.julien.datmusicplayer.voiceRecognition.IVoiceRecognizer;
import alonsojimenez.julien.datmusicplayer.voiceRecognition.SphinxVoiceRecognizer;
import alonsojimenez.julien.datmusicplayer.voiceRecognition.VoiceRecognizerFactory;


public class MainActivity extends ActionBarActivity
{
    private static String defaultServerHostName = "datdroplet.ovh";
    private static String defaultServerPort = "10000";

    boolean recording = false;
    IVoiceRecognizer voiceRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ServerHandler.initMonitor(getApplicationContext());
        //ServerHandler.addServer("188.226.154.9", "15000");
        ServerHandler.addServer(defaultServerHostName, defaultServerPort);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(SpeechRecognizer.isRecognitionAvailable(this))
            Settings.setVoiceRecognizer(VoiceRecognizerFactory.create(AndroidVoiceRecognizer.class));
        else
            Settings.setVoiceRecognizer(VoiceRecognizerFactory.create(SphinxVoiceRecognizer.class));

        setContentView(R.layout.activity_main);

        setupSearchInputHandlers();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        voiceRecognizer = Settings.getVoiceRecognizer();
        voiceRecognizer.init(this);

        voiceRecognizer.setRecognitionResultListener(new IRecognitionResultListener()
        {
            @Override
            public void onRecognitionResult(String result)
            {
                Command command = CommandParserHandler.parse(result);
                if(command != null)
                    onCommand(command);
            }
        });
    }

    public void onAction(View v)
    {
        Intent intent = null;

        if(findViewById(R.id.add) == v)
        {
            intent = new Intent(this, AddActivity.class);
            intent.putExtra("params", false);
        }

        else if(findViewById(R.id.artistSearchButton) == v)
        {
            TextView temp = (TextView)findViewById(R.id.artistSearchField);
            String key = temp.getText().toString();
            if(key != null && key != "")
            {
                intent = new Intent(this, SearchResultsActivity.class);
                intent.putExtra("searchKey", key);
                intent.putExtra("searchType", SearchType.ARTIST);
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
                intent.putExtra("searchType", SearchType.TITLE);
            }
        }

        else if(findViewById(R.id.listButton) == v)
        {
            intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra("searchType", SearchType.LIST);
        }

        if(intent != null)
            startActivity(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //ServerHandler.destroy();
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

    private void onCommand(Command command)
    {
        if(command.getAction() == Action.ADD)
        {
            Intent intent = new Intent(this, AddActivity.class);
            intent.putExtra("params", true);
            intent.putExtra("title", command.getTitle());
            intent.putExtra("artist", command.getArtist());
            startActivity(intent);
        }

        else
        {
            if(command.getTitle() == null)
                return;

            Intent intent = new Intent(this, SearchResultsActivity.class);

            if(command.getArtist() == null)
            {
                intent.putExtra("searchType", SearchType.ANY);
                intent.putExtra("searchKey", command.getTitle());
            }

            else
            {
                intent.putExtra("searchType", SearchType.BOTH);
                intent.putExtra("title", command.getTitle());
                intent.putExtra("artist", command.getArtist());
            }
            startActivity(intent);
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
