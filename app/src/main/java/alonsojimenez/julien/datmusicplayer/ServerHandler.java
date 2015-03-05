package alonsojimenez.julien.datmusicplayer;

import Ice.Communicator;
import Player.ServerPrx;

/**
 * Created by julien on 03/03/15.
 */
public class ServerHandler
{
    private static Communicator ic = null;
    private static ServerPrx server = null;
    private static String hostname = "datdroplet.ovh";
    private static String port = "10000";
    private static String streamingPort = "8090";
    private static String searchKey = null;

    public static Communicator getCommunicator() { return ic; }
    public static void setCommunicator(Communicator c) { ic = c; }
    public static ServerPrx getServer() { return server; }
    public static void setServer(ServerPrx srv) { server = srv; }
    public static String getHostname() { return hostname; }
    public static void setHostname(String host) { host = hostname; }
    public static String getPort() { return port; }
    public static void setPort(String p) { port = p; }
    public static String getStreamingPort() { return streamingPort; }
    public static void setStreamingPort(String p) { streamingPort = p; }
    public static String getSearchKey() { return searchKey; }
    public static void setSearchKey(String key) { searchKey = key; }

    public static void initCommunicator()
    {
        try
        {
            if(ic == null)
                ic = Ice.Util.initialize(new String[]{""});

        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    public static void initServer()
    {
        try
        {
            if(ic == null)
                initCommunicator();

            if(server == null)
            {
                Ice.ObjectPrx base = ic.stringToProxy("Server:default -h " + hostname +
                        " -p " + port);
                server = Player.ServerPrxHelper.checkedCast(base);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    public static void destroy()
    {
        if (ic == null)
            return;
        try
        {
            ic.destroy();
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}
