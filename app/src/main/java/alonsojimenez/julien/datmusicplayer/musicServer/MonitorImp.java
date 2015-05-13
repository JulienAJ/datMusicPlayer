package alonsojimenez.julien.datmusicplayer.musicServer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import Ice.Current;
import Player._MonitorDisp;
import Player.song;
import alonsojimenez.julien.datmusicplayer.MainActivity;
import alonsojimenez.julien.datmusicplayer.R;

/**
 * Created by julien on 06/05/15.
 */
public class MonitorImp extends _MonitorDisp
{
    Context context = null;

    public MonitorImp(Context context)
    {
        this.context = context;
    }

    public void notify(String message)
    {
        Log.i("Monitor", message);

        NotificationCompat.Builder notifBuilder;
        notifBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.music_launcher)
                .setContentTitle("DatMusicPlayer")
                .setContentText(message);
        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notifBuilder.setContentIntent(pendingIntent);
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(0, notifBuilder.build());
    }

    @Override
    public void songRemoved(song s, Current __current)
    {
        notify(s.name + " by " + s.artist + " was removed !");
    }

    @Override
    public void newSong(song s, Current __current)
    {
        notify(s.name + " by " + s.artist + " was added !");
    }

    @Override
    public void serverUp(Current __current)
    {
        notify("A Server is Up !");
    }

    @Override
    public void serverDown(Current __current)
    {
        notify("A Server is Down !");
    }
}
