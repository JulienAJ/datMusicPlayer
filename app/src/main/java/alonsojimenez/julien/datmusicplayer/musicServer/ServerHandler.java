package alonsojimenez.julien.datmusicplayer.musicServer;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Ice.ObjectPrx;
import IceStorm.TopicPrx;
import Player.Monitor;
import Player.song;

/**
 * Created by julien on 03/03/15.
 */
public class ServerHandler
{
    // Ice Objects
    private static Map<String, Server> servers = null;
    private static Monitor monitor;

    // Glacier / IceStorm connectivity
    private static String glacierHostname = "datdroplet.ovh";
    private static String stormHostname = "datdroplet.ovh";
    private static String glacierPort = "4041";
    private static String stormPort = "9999";
    private static String user = "user";
    private static String password = "password";

    public static String getHostname(String id) { return servers.get(id).getHostname(); }
    public static String getStreamingPort(String id) { return  servers.get(id).getStreamingPort(); }

    public static void addServer(String hostname, String port, String streamingPort)
    {
        try
        {
            if(servers == null)
                servers = new HashMap<>();

            Server server = new Server(hostname, port, streamingPort);
            server.initCommunicator(glacierHostname, glacierPort);
            server.initRouter(user, password);
            server.setUpStorm(stormHostname, stormPort, monitor);
            server.init();
            if(servers.get(server.getIdentifier()) == null)
                servers.put(server.getIdentifier(), server);
        }
        catch(Exception e)
        {
            Log.e("Ice Server", e.getMessage());
        }

    }

    public static void initMonitor(Context context)
    {
        if(monitor == null)
            monitor = new MonitorImp(context);
    }

    public static String getLessLoadedServer()
    {
        if(servers == null)
            return null;

        int min = -1;
        String identifier = null;
        Iterator it = servers.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            int temp = ((Server)pair.getValue()).getCount();
            if(temp == 0)
                return (String)pair.getKey();
            if(min == -1 || temp < min)
            {
                min = temp;
                identifier = (String)pair.getKey();
            }
        }
        return identifier;
    }

    public static song[] list()
    {
        if(servers == null)
            return null;

        List<song> songs = new ArrayList<>();
        Iterator it = servers.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            String serverId = (String)pair.getKey();
            song[] temp = ((Server)pair.getValue()).getServerPrx().list();
            songs.addAll(Arrays.asList(setPath(serverId, temp)));
        }
        song[] result = songs.toArray(new song[songs.size()]);
        return result;
    }

    public static song[] findByTitle(String title)
    {
        if(servers == null)
            return null;

        List<song> songs = new ArrayList<>();
        Iterator it = servers.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            String serverId = (String)pair.getKey();
            song[] temp = ((Server)pair.getValue()).getServerPrx().findByTitle(title);
            songs.addAll(Arrays.asList(setPath(serverId, temp)));
        }
        song[] result = songs.toArray(new song[songs.size()]);
        return result;
    }

    public static song[] findByArtist(String artist)
    {
        if(servers == null)
            return null;

        List<song> songs = new ArrayList<>();
        Iterator it = servers.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            String serverId = (String)pair.getKey();
            song[] temp = ((Server)pair.getValue()).getServerPrx().findByArtist(artist);
            songs.addAll(Arrays.asList(setPath(serverId, temp)));
        }
        song[] result = songs.toArray(new song[songs.size()]);
        return result;
    }

    public static song findByBoth(String title, String artist)
    {
        if(servers == null)
            return null;

        Iterator it = servers.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            String serverId = (String)pair.getKey();
            song temp = ((Server)pair.getValue()).getServerPrx().findByBoth(title, artist);
            if(temp != null)
            {
                temp.path = serverId + '@' + temp.path;
                return temp;
            }
        }
        return null;
    }

    public static song[] findByAny(String searchKey)
    {
        if(servers == null)
            return null;

        List<song> songs = new ArrayList<>();
        Iterator it = servers.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            String serverId = (String)pair.getKey();
            song[] temp = ((Server)pair.getValue()).getServerPrx().findByAny(searchKey);
            songs.addAll(Arrays.asList(setPath(serverId, temp)));
        }
        song[] result = songs.toArray(new song[songs.size()]);
        return result;
    }

    public static String start(String id, String path)
    {
        if(servers == null)
            return null;

        Server server = servers.get(id);
        if(server == null)
            return null;

        return server.getServerPrx().start(path);
    }

    public static void play(String id, String token)
    {
        if(servers == null)
            return;

        Server server = servers.get(id);
        if(server == null)
            return;

        server.getServerPrx().play(token);
    }

    public static void stop(String id, String token)
    {
        if(servers == null)
            return;

        Server server = servers.get(id);
        if(server == null)
            return;

        server.getServerPrx().stop(token);
    }

    public static void remove(String id, String path)
    {
        if(servers == null)
            return;

        Server server = servers.get(id);
        if(server == null)
            return;

        server.getServerPrx().remove(path);
    }

    public static int getMessageSizeMax(String identifier)
    {
        if(servers == null)
            return -1;

        return servers.get(identifier).getMessageSizeMax();
    }

    public static void write(String identifier, String name, int offset, byte[] data)
    {
        if(servers == null)
            return;

        servers.get(identifier).getServerPrx().write(name, offset, data);
    }

    public static void addSong(String identifier, String name, String artist, String path)
    {
        if(servers == null)
            return;

        servers.get(identifier).getServerPrx().addSong(name, artist, path);
    }

    private static song[] setPath(String identifier, song[] original)
    {
        for(song s : original)
        {
            s.path = identifier + '@' + s.path;
        }
        return original;
    }

    public static String cutPath(String original, boolean isId)
    {
        String regex = "(.+)@(.+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(original);
        if(matcher.find())
        {
            String identifier = matcher.group(1);
            String path = matcher.group(2);

            if(isId)
                return identifier;

            return path;
        }
        return null;
    }
}
