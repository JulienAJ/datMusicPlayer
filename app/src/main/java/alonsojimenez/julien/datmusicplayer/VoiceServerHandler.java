package alonsojimenez.julien.datmusicplayer;

import Ice.Communicator;
import Player.ServerPrx;
import PocketSphinxIce.IPocketSphinxServerHolder;
import PocketSphinxIce.IPocketSphinxServerPrx;
import PocketSphinxIce.IPocketSphinxServerPrxHelper;

/**
 * Created by julien on 03/03/15.
 */
public class VoiceServerHandler
{
    private static Communicator ic = null;
    private static IPocketSphinxServerPrx server = null;
    private static String hostname = "188.226.241.233";
    private static String port = "20000";
    private static String searchKey = null;

    public static Communicator getCommunicator() { return ic; }
    public static void setCommunicator(Communicator c) { ic = c; }
    public static IPocketSphinxServerPrx getServer() { return server; }
    public static void setServer(IPocketSphinxServerPrx srv) { server = srv; }
    public static String getHostname() { return hostname; }
    public static void setHostname(String host) { host = hostname; }
    public static String getPort() { return port; }
    public static void setPort(String p) { port = p; }
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
                Ice.ObjectPrx base = ic.stringToProxy("PocketSphinxServer:default -h " + hostname +
                        " -p " + port);
                server = IPocketSphinxServerPrxHelper.checkedCast(base);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    public static void destroyServer()
    {
        server = null;
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
            System.err.println(e);
        }
    }
}
