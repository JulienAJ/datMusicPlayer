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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Player.song;


public class SearchResultsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        song[] results = null;

        if(getIntent().getExtras().getBoolean("isList"))
            results = ServerHandler.getServer().list();

        else
        {
            String searchKey = getIntent().getExtras().getString("searchKey");

            if (getIntent().getExtras().getBoolean("isArtist"))
                results = ServerHandler.getServer().findByArtist(searchKey);
            else if (!(getIntent().getExtras().getBoolean("isArtist")))
                results = ServerHandler.getServer().findByTitle(searchKey);

        }
        setContentView(R.layout.activity_search_results);
        new SearchSongLoader().run(results);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private class SearchSongLoader extends Thread
    {
        public void run(final song[] songs)
        {
            final String c1 = "title";
            final String c2 = "artist";

            List<HashMap<String, String>> data = new ArrayList<>();

            for (song s : songs) {
                HashMap<String, String> e = new HashMap<>();

                e.put(c1, s.name);
                e.put(c2, s.artist);
                data.add(e);
            }

            SimpleAdapter adapter = new SimpleAdapter(SearchResultsActivity.this,
                    data,
                    android.R.layout.simple_list_item_2,
                    new String[]{c1, c2},
                    new int[]{android.R.id.text1,
                            android.R.id.text2});
            ListView lv = (ListView)findViewById(R.id.searchResults);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    //if(v!=null)
                    //    v.setBackgroundColor(Color.TRANSPARENT);
                    //view.setBackgroundColor(Color.LTGRAY);
                    //v = view;
                    //selectSong(songs[position]);
                    Intent intent = new Intent(SearchResultsActivity.this, SongActivity.class);
                    // set the song as an extra
                    intent.putExtra("SONG", (Parcelable)(new ParcelableSong(songs[position])));
                    startActivity(intent);
                }
            });
        }
    }
}
