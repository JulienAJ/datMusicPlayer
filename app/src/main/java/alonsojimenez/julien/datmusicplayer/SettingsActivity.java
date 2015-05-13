package alonsojimenez.julien.datmusicplayer;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import Ice.LocalException;
import Player.Server;


public class SettingsActivity extends ActionBarActivity {

    String oldHost = null;
    String oldPort = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /*oldHost = ServerHandler.getHostname();
        oldPort = ServerHandler.getPort();
        ((EditText) findViewById(R.id.hostnameField)).setText(oldHost);
        ((EditText) findViewById(R.id.portField)).setText(oldPort);
        ((TextView) findViewById(R.id.statusLabel)).setTextColor(Color.GREEN);
        ((Button) (findViewById(R.id.validateSettings))).setEnabled(false);*/
    }

    public void onPing(View v) {
        /*String newHost = ((EditText) (findViewById(R.id.hostnameField))).getText().toString();
        String newPort = ((EditText) (findViewById(R.id.portField))).getText().toString();

        if (oldHost.equals(newHost) && oldPort.equals(newPort))
            return;

        ServerHandler.setHostname(newHost);
        ServerHandler.setPort(newPort);

        try {
            //ServerHandler.destroyServer();
            ServerHandler.initServer();
            if (ServerHandler.getServer() != null) {
                ServerHandler.getServer().ice_ping();
                ((TextView) (findViewById(R.id.statusLabel))).setTextColor(Color.GREEN);
                ((Button) (findViewById(R.id.validateSettings))).setEnabled(true);
            } else {
                ((TextView) (findViewById(R.id.statusLabel))).setTextColor(Color.RED);
                ((Button) (findViewById(R.id.validateSettings))).setEnabled(false);
            }
        } catch (LocalException e) {
            ((TextView) (findViewById(R.id.statusLabel))).setTextColor(Color.RED);
            ((Button) (findViewById(R.id.validateSettings))).setEnabled(false);
            System.err.println(e.getMessage());
        }*/
    }

    public void onCancel(View v) {
        /*ServerHandler.setHostname(oldHost);
        ServerHandler.setPort(oldPort);

        try {
            //ServerHandler.destroyServer();
            ServerHandler.initServer();
            if (ServerHandler.getServer() != null) {
                ServerHandler.getServer().ice_ping();
                ((TextView) (findViewById(R.id.statusLabel))).setTextColor(Color.GREEN);
                ((Button) (findViewById(R.id.validateSettings))).setEnabled(true);
            } else {
                ((TextView) (findViewById(R.id.statusLabel))).setTextColor(Color.RED);
                ((Button) (findViewById(R.id.validateSettings))).setEnabled(false);
            }
        } catch (LocalException e) {
            ((TextView) (findViewById(R.id.statusLabel))).setTextColor(Color.RED);
            ((Button) (findViewById(R.id.validateSettings))).setEnabled(false);
            System.err.println(e.getMessage());
        }*/

    }

    public void onValidate(View v)
    {
        finish();
    }
}
