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

public abstract class _ServerDisp extends Ice.ObjectImpl implements Server
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::Player::Server"
    };

    public boolean ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[] ice_ids()
    {
        return __ids;
    }

    public String[] ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String ice_id()
    {
        return __ids[1];
    }

    public String ice_id(Ice.Current __current)
    {
        return __ids[1];
    }

    public static String ice_staticId()
    {
        return __ids[1];
    }

    public final void addSong(String name, String artist, String path, String coverPath)
    {
        addSong(name, artist, path, coverPath, null);
    }

    public final song[] findByAny(String searchKey)
    {
        return findByAny(searchKey, null);
    }

    public final song[] findByArtist(String artist)
    {
        return findByArtist(artist, null);
    }

    public final song findByBoth(String title, String artist)
    {
        return findByBoth(title, artist, null);
    }

    public final song[] findByTitle(String name)
    {
        return findByTitle(name, null);
    }

    public final int getCount()
    {
        return getCount(null);
    }

    public final int getFileSize(String path)
    {
        return getFileSize(path, null);
    }

    public final String getStreamingPort()
    {
        return getStreamingPort(null);
    }

    public final song[] list()
    {
        return list(null);
    }

    public final void play(String id)
    {
        play(id, null);
    }

    public final byte[] read(String filename, int offset, int count)
    {
        return read(filename, offset, count, null);
    }

    public final void remove(String path)
    {
        remove(path, null);
    }

    public final String start(String path)
    {
        return start(path, null);
    }

    public final void stop(String id)
    {
        stop(id, null);
    }

    public final void write(String name, int offset, byte[] data)
    {
        write(name, offset, data, null);
    }

    public static Ice.DispatchStatus ___getCount(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.readEmptyParams();
        int __ret = __obj.getCount(__current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        __os.writeInt(__ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___getStreamingPort(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.readEmptyParams();
        String __ret = __obj.getStreamingPort(__current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        __os.writeString(__ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___addSong(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String name;
        String artist;
        String path;
        String coverPath;
        name = __is.readString();
        artist = __is.readString();
        path = __is.readString();
        coverPath = __is.readString();
        __inS.endReadParams();
        __obj.addSong(name, artist, path, coverPath, __current);
        __inS.__writeEmptyParams();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___remove(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String path;
        path = __is.readString();
        __inS.endReadParams();
        __obj.remove(path, __current);
        __inS.__writeEmptyParams();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___findByTitle(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String name;
        name = __is.readString();
        __inS.endReadParams();
        song[] __ret = __obj.findByTitle(name, __current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        songSeqHelper.write(__os, __ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___findByArtist(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String artist;
        artist = __is.readString();
        __inS.endReadParams();
        song[] __ret = __obj.findByArtist(artist, __current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        songSeqHelper.write(__os, __ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___findByBoth(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String title;
        String artist;
        title = __is.readString();
        artist = __is.readString();
        __inS.endReadParams();
        song __ret = __obj.findByBoth(title, artist, __current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        __ret.__write(__os);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___findByAny(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String searchKey;
        searchKey = __is.readString();
        __inS.endReadParams();
        song[] __ret = __obj.findByAny(searchKey, __current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        songSeqHelper.write(__os, __ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___list(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.readEmptyParams();
        song[] __ret = __obj.list(__current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        songSeqHelper.write(__os, __ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___start(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String path;
        path = __is.readString();
        __inS.endReadParams();
        String __ret = __obj.start(path, __current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        __os.writeString(__ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___play(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String id;
        id = __is.readString();
        __inS.endReadParams();
        __obj.play(id, __current);
        __inS.__writeEmptyParams();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___stop(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String id;
        id = __is.readString();
        __inS.endReadParams();
        __obj.stop(id, __current);
        __inS.__writeEmptyParams();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___write(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String name;
        int offset;
        byte[] data;
        name = __is.readString();
        offset = __is.readInt();
        data = ByteSeqHelper.read(__is);
        __inS.endReadParams();
        __obj.write(name, offset, data, __current);
        __inS.__writeEmptyParams();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___read(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String filename;
        int offset;
        int count;
        filename = __is.readString();
        offset = __is.readInt();
        count = __is.readInt();
        __inS.endReadParams();
        byte[] __ret = __obj.read(filename, offset, count, __current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        ByteSeqHelper.write(__os, __ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus ___getFileSize(Server __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.startReadParams();
        String path;
        path = __is.readString();
        __inS.endReadParams();
        int __ret = __obj.getFileSize(path, __current);
        IceInternal.BasicStream __os = __inS.__startWriteParams(Ice.FormatType.DefaultFormat);
        __os.writeInt(__ret);
        __inS.__endWriteParams(true);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "addSong",
        "findByAny",
        "findByArtist",
        "findByBoth",
        "findByTitle",
        "getCount",
        "getFileSize",
        "getStreamingPort",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "list",
        "play",
        "read",
        "remove",
        "start",
        "stop",
        "write"
    };

    public Ice.DispatchStatus __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ___addSong(this, in, __current);
            }
            case 1:
            {
                return ___findByAny(this, in, __current);
            }
            case 2:
            {
                return ___findByArtist(this, in, __current);
            }
            case 3:
            {
                return ___findByBoth(this, in, __current);
            }
            case 4:
            {
                return ___findByTitle(this, in, __current);
            }
            case 5:
            {
                return ___getCount(this, in, __current);
            }
            case 6:
            {
                return ___getFileSize(this, in, __current);
            }
            case 7:
            {
                return ___getStreamingPort(this, in, __current);
            }
            case 8:
            {
                return ___ice_id(this, in, __current);
            }
            case 9:
            {
                return ___ice_ids(this, in, __current);
            }
            case 10:
            {
                return ___ice_isA(this, in, __current);
            }
            case 11:
            {
                return ___ice_ping(this, in, __current);
            }
            case 12:
            {
                return ___list(this, in, __current);
            }
            case 13:
            {
                return ___play(this, in, __current);
            }
            case 14:
            {
                return ___read(this, in, __current);
            }
            case 15:
            {
                return ___remove(this, in, __current);
            }
            case 16:
            {
                return ___start(this, in, __current);
            }
            case 17:
            {
                return ___stop(this, in, __current);
            }
            case 18:
            {
                return ___write(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    protected void __writeImpl(IceInternal.BasicStream __os)
    {
        __os.startWriteSlice(ice_staticId(), -1, true);
        __os.endWriteSlice();
    }

    protected void __readImpl(IceInternal.BasicStream __is)
    {
        __is.startReadSlice();
        __is.endReadSlice();
    }

    public static final long serialVersionUID = 0L;
}
