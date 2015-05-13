package alonsojimenez.julien.datmusicplayer;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Player.song;
import alonsojimenez.julien.datmusicplayer.musicServer.ServerHandler;


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

        SearchType searchType = (SearchType)getIntent().getExtras().get("searchType");
        boolean isRemove = getIntent().getExtras().getBoolean("isRemove", false);

        if(searchType == SearchType.LIST)
        {
            results = ServerHandler.list();
            ((TextView)findViewById(R.id.listHeader)).setText(R.string.registeredSongs);
        }

        else if(searchType == SearchType.ARTIST)
        {
            String searchKey = getIntent().getExtras().getString("searchKey");
            results = ServerHandler.findByArtist(searchKey);
        }
        else if(searchType == SearchType.TITLE)
        {
            String searchKey = getIntent().getExtras().getString("searchKey");
            results = ServerHandler.findByTitle(searchKey);
        }
        else if(searchType == SearchType.ANY)
        {
            String searchKey = getIntent().getExtras().getString("searchKey");
            results = ServerHandler.findByAny(searchKey);
        }
        else if(searchType == SearchType.BOTH)
        {
            String title = getIntent().getExtras().getString("title");
            String artist = getIntent().getExtras().getString("artist");
            song result = ServerHandler.findByBoth(title, artist);

            if(result != null)
            {
                Intent intent = new Intent(SearchResultsActivity.this, SongActivity.class);
                intent.putExtra("SONG", (Parcelable)(new ParcelableSong(result)));
                intent.putExtra("isRemove", isRemove);
                // TODO say for what (removal, playing)
                startActivity(intent);
            }
        }
        else
            finish();

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
