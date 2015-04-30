package alonsojimenez.julien.datmusicplayer;

import android.content.Context;

/**
 * Created by julien on 30/04/15.
 */
public interface IVoiceRecognizer
{
    public void init(Context context);
    public void start();
    public void stop();
    public String getResult();
}
