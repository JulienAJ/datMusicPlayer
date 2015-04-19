package alonsojimenez.julien.datmusicplayer;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Player.song;


public class SearchResultsActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        song[] results = null;

        if(getIntent().getExtras().getBoolean("isList"))
        {
            results = ServerHandler.getServer().list();
            ((TextView)findViewById(R.id.listHeader)).setText(R.string.registeredSongs);
        }

        else
        {
            String searchKey = getIntent().getExtras().getString("searchKey");

            if (getIntent().getExtras().getBoolean("isArtist"))
                results = ServerHandler.getServer().findByArtist(searchKey);
            else if (!(getIntent().getExtras().getBoolean("isArtist")))
                results = ServerHandler.getServer().findByTitle(searchKey);

        }
        if(results.length == 0)
        {
            ((TextView)findViewById(R.id.listHeader)).setText(R.string.noResults);
        }
        else
        {
            new SearchSongLoader().run(results);
        }
    }

    private class SearchSongLoader extends Thread
    {
        public void run(final song[] songs)
        {
            final String c1 = "title";
            final String c2 = "artist";

            List<HashMap<String, String>> data = new ArrayList<>();

            for(song s : songs)
            {
                HashMap<String, String> e = new HashMap<>();

                e.put(c1, s.name);
                e.put(c2, s.artist);
                data.add(e);
            }

            SimpleAdapter adapter = new SimpleAdapter(SearchResultsActivity.this,
                    data,
                    android.R.layout.simple_list_item_2,
                    new String[]{c1, c2},
                    new int[]{android.R.id.text1, android.R.id.text2});
            ListView lv = (ListView)findViewById(R.id.searchResults);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
                {
                    Intent intent = new Intent(SearchResultsActivity.this, SongActivity.class);
                    intent.putExtra("SONG", (Parcelable)(new ParcelableSong(songs[position])));
                    startActivity(intent);
                }
            });
        }
    }
}
