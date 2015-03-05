package alonsojimenez.julien.datmusicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import Player.song;

/**
 * Created by julien on 05/03/15.
 */
public class ParcelableSong implements Parcelable
{
    protected song s;
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeSerializable(s);
    }

    private void readFromParcel(Parcel in)
    {
        s = (song)in.readSerializable();
    }

    public ParcelableSong(song so)
    {
        s = so;
    }

    public song getSong()
    {
        return s;
    }

    public ParcelableSong(Parcel in)
    {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<ParcelableSong> CREATOR = new Parcelable.Creator<ParcelableSong>()
    {

        public ParcelableSong createFromParcel(Parcel in)
        {
            return new ParcelableSong(in);
        }

        public ParcelableSong[] newArray(int size) {
            return new ParcelableSong[size];
        }

    };
}
