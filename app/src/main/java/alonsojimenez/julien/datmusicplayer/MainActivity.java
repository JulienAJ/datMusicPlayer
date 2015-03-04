package alonsojimenez.julien.datmusicplayer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import Ice.Communicator;
import Player.ServerPrx;


public class MainActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ServerHandler.initCommunicator();
        ServerHandler.initServer();

        setContentView(R.layout.activity_main);
    }

    public void onAction(View v)
    {
        Intent intent = null;

        if(findViewById(R.id.add) == v)
            intent = new Intent(this, AddActivity.class);

        else if(findViewById(R.id.remove) == v)
            intent = new Intent(this, RemoveActivity.class);

        else if(findViewById(R.id.search) == v)
        {
            TextView temp = (TextView)findViewById(R.id.searchField);
            String key = temp.getText().toString();
            if(key != null && key != "")
            {
                intent = new Intent(this, SearchResultsActivity.class);
                intent.putExtra("searchKey", key);
            }
        }

        if(intent != null)
            startActivity(intent);
    }

    @Override
    public void onDestroy()
    {
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
