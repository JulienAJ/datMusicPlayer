// **********************************************************************
//
// Copyright (c) 2003-2013 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.5.1
//
// <auto-generated>
//
// Generated from file `server.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Player;

public class song implements java.lang.Cloneable, java.io.Serializable
{
    public String name;

    public String artist;

    public String path;

    public String coverPath;

    public song()
    {
    }

    public song(String name, String artist, String path, String coverPath)
    {
        this.name = name;
        this.artist = artist;
        this.path = path;
        this.coverPath = coverPath;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        song _r = null;
        if(rhs instanceof song)
        {
            _r = (song)rhs;
        }

        if(_r != null)
        {
            if(name != _r.name)
            {
                if(name == null || _r.name == null || !name.equals(_r.name))
                {
                    return false;
                }
            }
            if(artist != _r.artist)
            {
                if(artist == null || _r.artist == null || !artist.equals(_r.artist))
                {
                    return false;
                }
            }
            if(path != _r.path)
            {
                if(path == null || _r.path == null || !path.equals(_r.path))
                {
                    return false;
                }
            }
            if(coverPath != _r.coverPath)
            {
                if(coverPath == null || _r.coverPath == null || !coverPath.equals(_r.coverPath))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 5381;
        __h = IceInternal.HashUtil.hashAdd(__h, "::Player::song");
        __h = IceInternal.HashUtil.hashAdd(__h, name);
        __h = IceInternal.HashUtil.hashAdd(__h, artist);
        __h = IceInternal.HashUtil.hashAdd(__h, path);
        __h = IceInternal.HashUtil.hashAdd(__h, coverPath);
        return __h;
    }

    public java.lang.Object
    clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeString(name);
        __os.writeString(artist);
        __os.writeString(path);
        __os.writeString(coverPath);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        name = __is.readString();
        artist = __is.readString();
        path = __is.readString();
        coverPath = __is.readString();
    }

    public static final long serialVersionUID = -3385583092675533714L;
}
