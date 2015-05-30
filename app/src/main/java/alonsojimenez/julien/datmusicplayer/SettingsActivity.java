package alonsojimenez.julien.datmusicplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alonsojimenez.julien.datmusicplayer.musicServer.Server;
import alonsojimenez.julien.datmusicplayer.musicServer.ServerHandler;
import alonsojimenez.julien.datmusicplayer.voiceRecognition.AndroidVoiceRecognizer;
import alonsojimenez.julien.datmusicplayer.voiceRecognition.SphinxVoiceRecognizer;
import alonsojimenez.julien.datmusicplayer.voiceRecognition.VoiceRecognizerFactory;


public class SettingsActivity extends ActionBarActivity {

    private static final int ANDROID = 0;
    private static final int SPHINX = 1;

    private boolean isAndroid = true;
    private boolean isSphinx = false;

    private String newServerId = null;
    private boolean newServerPing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if(SpeechRecognizer.isRecognitionAvailable(this))
        {
            Spinner spinner = (Spinner)findViewById(R.id.speechSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.speechRecognizersArray, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            if(Settings.getVoiceRecognizerType() == AndroidVoiceRecognizer.class)
                spinner.setSelection(ANDROID);
            else
                spinner.setSelection(SPHINX);

            spinner.setOnItemSelectedListener(new SpeechChangeListener());
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        TextView label = (TextView)findViewById(R.id.serversLabel);
        int number = ServerHandler.getNumberOfServers();
        if(number == 0)
            label.setText(R.string.serverListEmpty);
        else
            label.setText(number + getString(R.string.serversInList));
        new ServerLoader().run(ServerHandler.getServersId());
    }

    public void onAddServer(View v)
    {
            LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_new_server, null);

            ((Button)view.findViewById(R.id.pingButtonAdd)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(newServerId != null)
                    {
                        ServerHandler.removeServer(newServerId);
                        newServerId = null;
                    }
                    EditText newHostname = (EditText)view.findViewById(R.id.newServerName);
                    EditText newPort = (EditText)view.findViewById(R.id.addServerPort);
                    TextView statusView = (TextView)view.findViewById(R.id.addServerStatus);
                    String stringHostname = newHostname.getText().toString();
                    String stringPort = newPort.getText().toString();
                    ServerHandler.addServer(stringHostname, stringPort);
                    String id = Server.identifierFromHostPort(stringHostname, stringPort);
                    boolean ok = ServerHandler.ping(id);
                    if(ok)
                    {
                        statusView.setText(getString(R.string.statusOK));
                        statusView.setTextColor(Color.GREEN);
                        newServerPing = true;
                    }
                    else
                    {
                        statusView.setText(getString(R.string.statusOFF));
                        statusView.setTextColor(Color.RED);
                        newServerPing = false;
                    }
                }
            });

            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle(getString(R.string.serverAddTitle))
                    .setView(view)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton(getString(R.string.validate), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (newServerId != null && !newServerPing) {
                                ServerHandler.removeServer(newServerId);
                                newServerId = null;
                                Toast.makeText(getApplicationContext(), getString(R.string.noServerAdded),
                                        Toast.LENGTH_SHORT).show();
                            } else if (!newServerPing) {
                                Toast.makeText(getApplicationContext(), getString(R.string.mustPing),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.serverAdded),
                                        Toast.LENGTH_SHORT).show();

                            }

                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (newServerId != null) {
                                ServerHandler.removeServer(newServerId);
                                newServerId = null;
                                Toast.makeText(getApplicationContext(), getString(R.string.noServerAdded),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
    }

    private class ServerLoader extends Thread
    {
        public void run(final String[] servers)
        {
            final String c1 = "identifier";
            final String c2 = "status";

            List<HashMap<String, String>> data = new ArrayList<>();

            for(String s : servers)
            {
                HashMap<String, String> e = new HashMap<>();

                e.put(c1, s);
                if(ServerHandler.ping(s))
                    e.put(c2, "Online");
                else
                    e.put(c2, "Offline");
                data.add(e);
            }

            final SimpleAdapter adapter = new SimpleAdapter(SettingsActivity.this,
                    data,
                    android.R.layout.simple_list_item_2,
                    new String[]{c1, c2},
                    new int[]{android.R.id.text1, android.R.id.text2});
            ListView lv = (ListView)findViewById(R.id.serversList);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                {
                    String current = (String)((Map)adapter.getItem(position)).get(c1);
                    createServerDialog(current);
                }
            });
        }

        public void createServerDialog(final String id)
        {
            LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.alert_servers, null);

            ((Button)view.findViewById(R.id.pingButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView idView = (TextView)view.findViewById(R.id.alert_id);
                    TextView statusView = (TextView)view.findViewById(R.id.alert_status);
                    String id = idView.getText().toString();
                    boolean ok = ServerHandler.ping(id);
                    if(ok)
                    {
                        statusView.setText(getString(R.string.statusOK));
                        statusView.setTextColor(Color.GREEN);
                    }
                    else
                    {
                        statusView.setText(getString(R.string.statusOFF));
                        statusView.setTextColor(Color.RED);
                    }
                }
            });

            ((TextView)view.findViewById(R.id.alert_id)).setText(id);
            TextView statusView = (TextView)view.findViewById(R.id.alert_status);
            if(ServerHandler.ping(id))
            {
                statusView.setText(getString(R.string.statusOK));
                statusView.setTextColor(Color.GREEN);
            }
            else
            {
                statusView.setText(getString(R.string.statusOFF));
                statusView.setTextColor(Color.RED);
            }

            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle(getString(R.string.serverManagementTitle))
                    .setView(view)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ServerHandler.removeServer(id);
                            Toast.makeText(getApplicationContext(), id + " " + getString(R.string.removeSuccess),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.close), null)
                    .show();
        }
    }

    public void onValidate(View v)
    {
        Class type = null;
        if(isAndroid)
            type = AndroidVoiceRecognizer.class;
        else
            type = SphinxVoiceRecognizer.class;

        Settings.setVoiceRecognizer(VoiceRecognizerFactory.create(type));
        finish();
    }

    private class SpeechChangeListener implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            String selected = (String)parent.getItemAtPosition(position);
            if(selected.equals("Android"))
            {
                isAndroid = true;
                isSphinx = false;
            }

            else
            {
                isAndroid = false;
                isSphinx = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
        }
    }
}
