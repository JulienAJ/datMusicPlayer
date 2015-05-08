package alonsojimenez.julien.datmusicplayer;

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

    @Override
    public void report(String action, song s, Current __current)
    {
        if(context == null)
            return;
        if(!action.equals("error"))
        {
            String notif = "Song " + action + " : " + s.name + " by " + s.artist;

            Log.i("Monitor", notif);

            NotificationCompat.Builder notifBuilder;
            notifBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.music_launcher)
                    .setContentTitle("DatMusicPlayer")
                    .setContentText(notif);
            Intent intent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            notifBuilder.setContentIntent(pendingIntent);
            NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.notify(0, notifBuilder.build());
        }

    }
}
