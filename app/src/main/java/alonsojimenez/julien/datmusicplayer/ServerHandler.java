package alonsojimenez.julien.datmusicplayer;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import Ice.Communicator;
import Ice.Identity;
import Ice.InitializationData;
import Ice.ObjectPrx;
import IceStorm.TopicManagerPrx;
import IceStorm.TopicManagerPrxHelper;
import IceStorm.TopicPrx;
import Player.Monitor;
import Player.ServerPrx;

/**
 * Created by julien on 03/03/15.
 */
public class ServerHandler
{
    // Context stored to give to monitor (to build notifications)
    private static Context context = null;

    // Ice Objects
    private static Communicator ic = null;
    private static ServerPrx server = null;
    private static Glacier2.RouterPrx router;
    private static ObjectPrx monitorProxy;
    private static TopicPrx topic;

    // Hostname, ports and connectivity variables
    private static String hostname = "datdroplet.ovh";
    private static String port = "10000";
    private static String streamingPort = "8090";
    private static String glacierPort = "4041";
    private static String stormPort = "9999";
    private static String user = "user";
    private static String password = "password";

    // Maximum message Size
    private static int messageSizeMax = 1000000;

    public static Communicator getCommunicator() { return ic; }
    public static ServerPrx getServer() { return server; }
    public static void setServer(ServerPrx srv) { server = srv; }
    public static String getHostname() { return hostname; }
    public static void setHostname(String host) { host = hostname; }
    public static String getPort() { return port; }
    public static void setPort(String p) { port = p; }
    public static String getStreamingPort() { return streamingPort; }
    public static int getMessageSizeMax() { return messageSizeMax; }
    public static void setContext(Context context) { ServerHandler.context = context; }

    public static void initCommunicator()
    {
        try
        {
            if(ic == null)
            {
                InitializationData initializationData = new InitializationData();
                initializationData.properties = Ice.Util.createProperties();
                initializationData.properties.setProperty("Ice.Default.Router",
                        "Glacier2/router:tcp -h " + hostname + " -p " + glacierPort);
                initializationData.properties.setProperty("Ice.ACM.Client", "0");
                initializationData.properties.setProperty("Ice.RetryIntervals", "-1");
                initializationData.properties.setProperty("CallbackAdapter.Router",
                        "Glacier2/router:tcp -h " + hostname + " -p " + glacierPort);
                ic = Ice.Util.initialize(initializationData);
                initRouter();
                setUpStorm();
            }

        }
        catch(Exception e)
        {
            Log.e("Ice Communicator", e.getMessage());
        }

    }

    public static void initRouter()
    {
        try
        {
            Ice.RouterPrx defaultRouter = ic.getDefaultRouter();
            router = Glacier2.RouterPrxHelper.checkedCast(defaultRouter);
            router.createSession(user, password);
        }
        catch(Exception e)
        {
            Log.e("Glacier Router", e.getMessage());
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
            Log.e("Ice Server", e.getMessage());
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
            Log.e("Ice Communicator Destroy", e.getMessage());
        }
    }

    public static void setUpStorm()
    {
        try
        {
            ObjectPrx objectPrx = ic.stringToProxy("IceStorm/TopicManager:tcp -h " + hostname + " -p " + stormPort);
            TopicManagerPrx topicManager = TopicManagerPrxHelper.checkedCast(objectPrx);
            Ice.ObjectAdapter adapter = ic.createObjectAdapterWithRouter("MonitorAdapter", router);
            Monitor monitor = new MonitorImp(context);
            monitorProxy = adapter.add(monitor, new Identity("default", router.getCategoryForClient())).ice_twoway();
            adapter.activate();

            topic = topicManager.retrieve("DatMusic");
            Map<String, String> qos = new HashMap<>();
            topic.subscribeAndGetPublisher(qos, monitorProxy);

            Log.i("IceStorm", "Topic active");
            ObjectPrx publisher = topic.getPublisher().ice_twoway();
        }
        catch(Exception e)
        {
            Log.e("IceStorm", e.getMessage());
        }

    }
}
