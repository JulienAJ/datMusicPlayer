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

public interface _ServerOperationsNC
{
    int getCount();

    String getStreamingPort();

    void addSong(String name, String artist, String path, String coverPath);

    void remove(String path);

    song[] findByTitle(String name);

    song[] findByArtist(String artist);

    song findByBoth(String title, String artist);

    song[] findByAny(String searchKey);

    song[] list();

    String start(String path);

    void play(String id);

    void stop(String id);

    void write(String name, int offset, byte[] data);

    byte[] read(String filename, int offset, int count);

    int getFileSize(String path);
}
